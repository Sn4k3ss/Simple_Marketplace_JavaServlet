package com.ss.TIW_2021project.web.controllers;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "SetProductDisplayed",
        description = "This servlet handle the click on a specific product to show the relative info",
        value = "/SetProductDisplayed"
)
public class SetProductDisplayed extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer productId = Integer.parseInt(req.getParameter("productId"));
        ProductService productService = new ProductService();
        User user = (User) req.getSession().getAttribute("user");

        try {
            productService.setProductDisplayed(user.getUserId(), productId);
        } catch (ServiceException ex) {
                        String errorMessage = "Couldn't update user last product";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }

        /*
        ProductsCatalogue productsFromQuery;

        if ( req.getSession().getAttribute("products_from_query") != null ) {
            productsF romQuery = (ProductsCatalogue) req.getSession().getAttribute("products_from_query");

        } else {
            String errorMessage = "Something ain't right";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            forward(req, resp, errorMessage);
            return;
        }


        req.setAttribute("products", productsFromQuery);
        req.setAttribute("selectedProductId", productId);

        forward(req, resp, null);

         */

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer productId = Integer.parseInt(req.getParameter("productId"));
        ProductService productService = new ProductService();
        User user = (User) req.getSession().getAttribute("user");

        try {
            productService.setProductDisplayed(user.getUserId(), productId);
        } catch (ServiceException ex) {
            String errorMessage = "Couldn't update user last product";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }

}