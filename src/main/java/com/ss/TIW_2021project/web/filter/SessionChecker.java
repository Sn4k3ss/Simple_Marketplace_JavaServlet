package com.ss.TIW_2021project.web.filter;

import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionChecker implements Filter {

    private ServletContext servletContext;
    private ITemplateEngine templateEngine;
    private MarketplaceApp marketplaceApp;

    public SessionChecker() {
        super();
    }



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.marketplaceApp = MarketplaceApp.getInstance(filterConfig.getServletContext());
        this.templateEngine = TemplateHandler.getTemplateEngine();
        this.servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        if (process(req,res)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            req.setAttribute("error", "You are not authorized to access this page");
            forward(req, res, PathUtils.pathToLoginPage);
        }



    }

    /**
     *
     * Process the request and check user's session
     *
     * @param request
     * @param response
     * @return true if user is logged in, false otherwisee
     * @throws ServletException
     */

    private boolean process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        boolean loggedIn = !session.isNew() && session != null && session.getAttribute("user") != null;
        boolean loginRequest = request.getRequestURI().equals("/login");

        return loggedIn || loginRequest;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException{

        ServletContext servletContext = request.getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());

    }
}
