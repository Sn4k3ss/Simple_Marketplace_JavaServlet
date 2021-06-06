package com.ss.TIW_2021project.web.application;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

public class TemplateHandler {

    public static TemplateEngine templateEngine = null;

    private TemplateHandler() {

    }

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
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.setTemplateResolver(templateResolver);

        TemplateHandler.templateEngine = templateEngine;
    }

}
