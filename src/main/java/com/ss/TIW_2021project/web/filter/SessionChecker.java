package com.ss.TIW_2021project.web.filter;

import com.ss.TIW_2021project.web.application.MarketplaceApp;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionChecker implements Filter {

    private ServletContext servletContext;
    private MarketplaceApp application;

    public SessionChecker() {
        super();
    }



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        this.application = new MarketplaceApp(this.servletContext);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        if (process((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            res.sendRedirect("login.html");
        }

    }

    /**
     *
     *
     *
     * @param request
     * @param response
     * @return true if user is logged in, false otherwisee
     * @throws ServletException
     */

    private boolean process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        HttpSession session = request.getSession();

        boolean loggedIn = !session.isNew() && session != null && session.getAttribute("user") != null;
        boolean loginRequest = request.getRequestURI().equals("/login.html");

        if(loggedIn || loginRequest)
            return true;
        else
            return false;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
