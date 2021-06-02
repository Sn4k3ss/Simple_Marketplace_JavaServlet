package com.ss.TIW_2021project.web.application;


import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;


public class MarketplaceApp {

    private static TemplateEngine templateEngine;


    public MarketplaceApp(final ServletContext servletContext) {

        super();

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(true);

        templateEngine = (TemplateEngine) getTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    public static ITemplateEngine getTemplateEngine() {

        if (MarketplaceApp.templateEngine == null)
            MarketplaceApp.templateEngine = new TemplateEngine();
        return MarketplaceApp.templateEngine;
    }

}
