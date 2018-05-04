package com.appointment.outside.base.resolver;

import com.common.common.ResultJson;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Date: 2017/8/8
 * Time: 17:20
 * User: yangkai
 * EMail: yangkai01@chtwm.com
 */
public class MySimpleMappingExceptionResolver extends SimpleMappingExceptionResolver {
    public MySimpleMappingExceptionResolver() {
    }

    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            this.logger.error("controller error.url=" + request.getContextPath(), ex);
            String retJson = ResultJson.getResultFail("4009", ex.getMessage());
            response.setContentType("application/json; charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            PrintWriter writer = response.getWriter();
            writer.write(retJson);
            writer.flush();
        } catch (Exception var7) {
            this.logger.error("", var7);
        }

        return new ModelAndView();
    }
}