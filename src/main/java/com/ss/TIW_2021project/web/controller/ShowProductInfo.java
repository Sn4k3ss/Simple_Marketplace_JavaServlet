package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(
        name = "ShowProductInfo",
        description = "This servlet handle the click on a specific product to show the relative info",
        value = "/search/products/showProductInfo"
)
public class ShowProductInfo extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = MarketplaceApp.getTemplateEngine();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer productId = Integer.parseInt(req.getParameter("productId"));
        ProductService productService = new ProductService(getServletContext());

        User user = (User) req.getSession().getAttribute("user");
        try {
            productService.setProductDisplayed(user.getUserId(), productId);
        } catch (UnavailableException ex) {
            //TODO
            //TO BE HANDLED
        }

        ProductsCatalogue productsFromQuery = (ProductsCatalogue) req.getSession().getAttribute("products_from_query");

        //redirect to the search page with the products retrieved displayed in a table
        final WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        webContext.setVariable("products", productsFromQuery);
        webContext.setVariable("selectedProductId", productId);
        templateEngine.process("searchProducts", webContext, resp.getWriter());
    }

}