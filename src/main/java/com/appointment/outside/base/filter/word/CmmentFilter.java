package com.appointment.outside.base.filter.word;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * 敏感词过滤
 * 
 * @author zhouxy
 *
 */
public class CmmentFilter extends OncePerRequestFilter {
	/* 敏感词 */
	private SensitivewordFilter sensitivewordFilter = null;

	public CmmentFilter() {
		sensitivewordFilter = new SensitivewordFilter();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();

		if (uri.indexOf("writeCom") != -1) {
			// 是否过滤
			boolean doFilter = true;

			if (doFilter) {
				String comment = request.getParameter("comment");

				boolean flag = sensitivewordFilter.isContaintSensitiveWord(comment, SensitivewordFilter.maxMatchType);
				if (flag) {
					// filterChain.doFilter(request, response);
					response.getWriter().write("false");
					// response.getWriter().print("false");
				} else {
					filterChain.doFilter(request, response);
				}

			}
		} else {
			// 如果uri中不包含do，则继续
			filterChain.doFilter(request, response);
		}

	}

}
