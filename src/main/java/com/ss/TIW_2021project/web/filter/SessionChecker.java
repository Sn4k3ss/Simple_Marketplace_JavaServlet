package com.ss.TIW_2021project.web.filter;

import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionChecker implements Filter {

    private MarketplaceApp marketplaceApp;

    public SessionChecker() {
        super();
    }


    @Override
    public void init(FilterConfig filterConfig) {
        this.marketplaceApp = MarketplaceApp.getInstance(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession s = req.getSession();

        if(s != null) {
            Object user = s.getAttribute("user");
            if(user != null) {
                filterChain.doFilter(req, res);
                return;
            }
        }

        res.sendRedirect(req.getServletContext().getContextPath() + PathUtils.pathToLoginPage);
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
