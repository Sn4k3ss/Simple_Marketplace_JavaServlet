package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.utils.ServletUtility;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "AddToCart",
        description = "This servlet handles the 'add to cart' action whenever is triggered",
        value = "/products/AddToCart"
)
public class AddToCart extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
       this.templateEngine = MarketplaceApp.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SupplierProduct supplierProduct;

        //the request contains: -productId
        //                      -supplierId
        //                      -supplierProductCost

        try {
            supplierProduct = ServletUtility.buildProductFromRequest(req);
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        CartService cartService = new CartService(getServletContext());
        ShoppingCart shoppingCart = cartService.addToCart(req.getSession(), supplierProduct);


        String path = getServletContext().getContextPath() + "/shoppingCart";
        resp.sendRedirect(path);
    }

}
