package com.ss.TIW_2021project.web.application;

import com.ss.TIW_2021project.web.controller.HomeController;
import com.ss.TIW_2021project.web.controller.LoginController;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class MarketplaceApp {



    private static TemplateEngine templateEngine;
    private Map<String, HttpServlet> controllersByURL;



    public MarketplaceApp(final ServletContext servletContext) {

        super();

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);

        // HTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(true);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);


        this.controllersByURL = new HashMap<>();

        this.controllersByURL.put("/", new HomeController());
        this.controllersByURL.put("/login", new LoginController());

        /*

        this.controllersByURL.put("/product/comments", new ProductCommentsController());
        this.controllersByURL.put("/order/list", new OrderListController());
        this.controllersByURL.put("/order/details", new OrderDetailsController());
        this.controllersByURL.put("/subscribe", new SubscribeController());
        this.controllersByURL.put("/userprofile", new UserProfileController());

         */

    }

    public static ITemplateEngine getTemplateEngine() {

        if (MarketplaceApp.templateEngine == null)
            MarketplaceApp.templateEngine = new TemplateEngine();
        return MarketplaceApp.templateEngine;
    }


    public HttpServlet resolveControllerForRequest(final HttpServletRequest request) {
        final String path = getRequestPath(request);
        return this.controllersByURL.get(path);
    }






    private static String getRequestPath(final HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        final String contextPath = request.getContextPath();

        final int fragmentIndex = requestURI.indexOf(';');
        if (fragmentIndex != -1) {
            requestURI = requestURI.substring(0, fragmentIndex);
        }

        if (requestURI.startsWith(contextPath)) {
            return requestURI.substring(contextPath.length());
        }
        return requestURI;
    }
}
