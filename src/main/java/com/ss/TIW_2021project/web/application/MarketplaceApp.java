package com.ss.TIW_2021project.web.application;


import com.ss.TIW_2021project.business.utils.ConnectionHandler;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;


public class MarketplaceApp {

    private static MarketplaceApp marketplaceAppInstance = null;

    public MarketplaceApp(final ServletContext servletContext) {

        super();

        TemplateHandler.setupTemplateEngine(servletContext);
        
        try {
            ConnectionHandler.setup(servletContext);
        } catch (UnavailableException e) {
            e.printStackTrace();
        }
    }


    public static MarketplaceApp getInstance(ServletContext servletContext) {

        if (MarketplaceApp.marketplaceAppInstance == null) {
            marketplaceAppInstance = new MarketplaceApp(servletContext);
        }

        return MarketplaceApp.marketplaceAppInstance;
    }

}
