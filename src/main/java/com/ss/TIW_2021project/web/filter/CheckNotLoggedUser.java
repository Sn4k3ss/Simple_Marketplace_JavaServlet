package com.ss.TIW_2021project.web.filter;

import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.MarketplaceApp;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CheckNotLoggedUser implements Filter {

    private MarketplaceApp marketplaceApp;

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig filterConfig) {
        this.marketplaceApp = MarketplaceApp.getInstance(filterConfig.getServletContext());
    }


    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession s = req.getSession(false);

        if(s != null) {
            Object user = s.getAttribute("user");
            if(user != null) {
                res.sendRedirect(servletRequest.getServletContext().getContextPath() + PathUtils.goToHomeServletPath);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);


    }

}
