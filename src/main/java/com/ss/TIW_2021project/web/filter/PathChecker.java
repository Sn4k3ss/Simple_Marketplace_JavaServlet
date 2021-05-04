package com.ss.TIW_2021project.web.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class PathChecker implements Filter {



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /*
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String URI = req.getRequestURI();
        */
    }

    @Override
    public void destroy() {

    }
}
