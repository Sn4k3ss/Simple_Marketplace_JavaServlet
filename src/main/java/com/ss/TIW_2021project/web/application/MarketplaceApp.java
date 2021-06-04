package com.ss.TIW_2021project.web.application;


import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.utils.ConnectionHandler;

import javax.servlet.ServletContext;


public class MarketplaceApp {

    private static MarketplaceApp marketplaceAppInstance = null;

    private MarketplaceApp(final ServletContext servletContext) {

        super();

        TemplateHandler.setupTemplateEngine(servletContext);

        try {
            ConnectionHandler.setupConnectionPool(servletContext);
        } catch (UtilityException e) {
            //errore creazione connection pool
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
