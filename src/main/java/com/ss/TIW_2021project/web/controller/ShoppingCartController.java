package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "shoppingCartController",
        description = "This is my first annotated servlet",
        urlPatterns = "/shoppingCart"
)
public class ShoppingCartController extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = MarketplaceApp.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        CartService cartService = new CartService(getServletContext());
        ShoppingCart shoppingCart = cartService.getShoppingCart(req.getSession());

        User user = (User) req.getSession().getAttribute("user");

        if (user.getShippingAddresses().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User has no shipping address ");
            return;
        }


        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        webContext.setVariable("shoppingCart", shoppingCart);
        webContext.setVariable("shoppingAddresses", user.getShippingAddresses());
        templateEngine.process("shoppingCart", webContext, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
