package com.appointment.outside.base.interceptor;

import com.common.common.ResultJson;
import com.common.interceptor.comment.SensitiveWords;
import com.common.interceptor.comment.SensitivewordManage;
import com.common.util.ConfigUtil;
import com.common.util.Tool;
import com.richgo.redis.RedisClusterUtil;
import com.user.common.CodeConts;
import com.user.entity.emp.Emp;
import com.user.service.emp.EmpService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 拦截器
 *
 * @author zhouxy
 *
 */
public class AuthInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
	/*  */
	private static final String contentType = "application/json;charset=utf-8";
	/* 敏感词返回信息 */
	private static String existsSensitiveWord = null;
	@Autowired
	private EmpService empService;
	/** 允许通过的url */
	private String[] allowUrls;
	/** 允许通过静态文件 */
	private String[] staticFiles;
	/* swagger相关请求都不拦截 */
	private String[] swaggerUrls;

	/* 敏感词管理对象 */
	private static SensitivewordManage sensitivewordManage = null;

	public AuthInterceptor() {
		sensitivewordManage = new SensitivewordManage();
	}

	public void setAllowUrls(String[] allowUrls) {
		this.allowUrls = allowUrls;
	}

	public void setStaticFiles(String[] staticFiles) {
		this.staticFiles = staticFiles;
	}

	public void setSwaggerUrls(String[] swaggerUrls) {
		this.swaggerUrls = swaggerUrls;
	}

	/**
	 *
	 * 在业务处理器处理请求之前被调用，在该方法中对用户请求request进行处理
	 *
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String contxtPath = request.getContextPath();
		String requestUrl = request.getRequestURI().replace(contxtPath, "");

		if (log.isDebugEnabled()) {
			log.debug("preHandle:{}", requestUrl);
		}
		// 敏感词判断
		boolean ret = existsSensitiveWord(request, response, handler);
		if (ret) {
			// 存在敏感词
			return false;
		}

		// 静态文件不需要拦截
		if (null != staticFiles && staticFiles.length >= 1) {
			for (String url : staticFiles) {
				if (requestUrl.startsWith(url)) {
					return true;
				}
			}
		}
		// 允许通过请求
		if (null != allowUrls && allowUrls.length >= 1) {
			for (int i = 0; i < allowUrls.length; i++) {
				String allowUrl = allowUrls[i];
				if (requestUrl.startsWith(allowUrl)) {
					return true;
				}
			}
		}

		// swagger请求不拦截
		if (null != swaggerUrls && swaggerUrls.length >= 1) {
			for (int i = 0; i < swaggerUrls.length; i++) {
				String swaggerUrl = swaggerUrls[i];
				if (requestUrl.startsWith(swaggerUrl)) {
					return true;
				}
			}
		}
		// 校验登录
//		String token = request.getHeader("token");
//		if (StringUtils.isBlank(token)) {
//			returnLoginFail(response);
//			return false;
//		}
		StringBuffer origUrl = request.getRequestURL();
        String queryStr = request.getQueryString();
        if (StringUtils.isNotBlank(queryStr)) {
            origUrl.append("?" + queryStr);
        }

		String code = "";
		// PC端token在cookie中
		Cookie cookie = Tool.ReadCookieMap(request).get("token");
		String jwtToken="";
		if (cookie != null) {
			//PC获取cookie令牌
			jwtToken = cookie.getValue();
		}else {
			redirectLogin(response, origUrl);
			return false;
		}
		if (StringUtils.isNotBlank(jwtToken)){
			Emp emp = empService.verificationToken(jwtToken);
			if (StringUtils.isNotBlank(emp.getCode())) {//PC端 token验证
				if (RedisClusterUtil.hget(CodeConts.REDIS_USER_LOGIN_PC_TOKENCODE, jwtToken).equals(emp.getCode())){
					code = emp.getCode();
				}else {
					redirectLogin(response, origUrl);
					return false;
				}
			} else {
				redirectLogin(response, origUrl);
				return false;
			}
		}

		if (StringUtils.isBlank(code)) {
			redirectLogin(response, origUrl);
			return false;
		}
        // 刷新token缓存 60 * 60
        int time = 3600;
        Map<String, String> codesmap = new HashMap<String, String>();
        codesmap.put(jwtToken, code);
        RedisClusterUtil.hmset(CodeConts.REDIS_USER_LOGIN_PC_TOKENCODE, codesmap, time);
        Map<String, String> tokenmap = new HashMap<String, String>();
        tokenmap.put("tokenPC_" + code, jwtToken);
        RedisClusterUtil.hmset(CodeConts.REDIS_USER_LOGIN_PC_CODETOKEN, tokenmap, time);
        return true;
    }

	private void redirectLogin(HttpServletResponse response, StringBuffer origUrl) throws IOException {
		response.sendRedirect("http://user.chtwm.com:8080/login.html"+"?origurl="+origUrl);
	}

	/**
	 *
	 * 登录失败
	 *
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	private void returnLoginFail(HttpServletResponse response) throws IOException {
		response.getWriter().write(ResultJson.getResultFail(com.user.common.CodeConts.LOGIN_FAILURE));
	}

	/**
	 *
	 * 在DispatcherServlet完全处理完请求后被调用，可以在该方法中进行一些资源清理的操作。
	 *
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	/**
	 *
	 * 在业务处理器处理完请求后，但是DispatcherServlet向客户端返回请求前被调用，在该方法中对用户请求request进行处理。
	 *
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 *
	 * 防盗链处理
	 *
	 * @param request
	 *            HttpServletRequest
	 * @return boolean true:来源属于本网站，false：来源不属于本网站
	 * @throws IOException
	 */
	private boolean headerPro(HttpServletRequest request) throws IOException {
		return ConfigUtil.checkReferer(request);
	}

	/**
	 *
	 * 敏感词判断
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            Object
	 * @return boolean true：存在敏感词；false:不需要敏感词判断或判断不存在敏感词
	 * @throws IOException
	 *
	 */
	private boolean existsSensitiveWord(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			// 获取Controller方法
			Method method = handlerMethod.getMethod();
			// 获取违禁词注解
			SensitiveWords annotation = method.getAnnotation(SensitiveWords.class);

			if (annotation != null) {
				if (annotation.check()) {// 需要检查敏感词
					if (log.isDebugEnabled()) {
						log.debug("需要检查敏感词是否存在，方法名称={}", method);
					}

					// 获取参数值
					String value = String.valueOf(request.getAttribute(annotation.requestParamName()));
					// 检查是否存在敏感词
					boolean ret = sensitivewordManage.isContaintSensitiveWord(value, SensitivewordManage.minMatchTYpe);

					if (ret) {
						response.setContentType(contentType);
						if (existsSensitiveWord == null) {
							existsSensitiveWord = ResultJson.getResultFail(CodeConts.EXISTS_SENSITIVE_WORD);
						}
						response.getWriter().write(existsSensitiveWord);
						return true;
					}
				}
			}
		}
		return false;
	}

	public  final String getEmpcode(HttpSession session){
		String code="";
		/*String code = (String) session.getAttribute(CodeConts.EMP_CODE);
		if(code == null){
//			throw new NeedLoginException();
		}*/
		return code;
	}
}
