package com.ss.TIW_2021project.web.application;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

public class TemplateHandler {

    private static TemplateEngine templateEngine = null;

    public static void setupTemplateEngine(ServletContext servletContext) {


        if(TemplateHandler.templateEngine != null)
            return;

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        TemplateHandler.templateEngine = templateEngine;
    }

    public static ITemplateEngine getTemplateEngine() {
        return templateEngine;
    }
}
