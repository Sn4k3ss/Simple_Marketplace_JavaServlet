package com.ss.TIW_2021project.web.application;


import javax.servlet.ServletContext;


public class MarketplaceApp {

    private static MarketplaceApp marketplaceAppInstance = null;

    public MarketplaceApp(final ServletContext servletContext) {

        super();

        TemplateHandler.setupTemplateEngine(servletContext);
    }


    public static MarketplaceApp getInstance(ServletContext servletContext) {

        if (MarketplaceApp.marketplaceAppInstance == null) {
            marketplaceAppInstance = new MarketplaceApp(servletContext);
        }

        return MarketplaceApp.marketplaceAppInstance;
    }

}
