package com.ss.TIW_2021project.web.controller.GoToPage;

import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(
        name = "ShoppingCartController",
        description = "Controller that shows the user's shopping cart",
        urlPatterns = "/GoToShoppingCart"
)
public class GoToShoppingCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        CartService cartService = new CartService();
        ShoppingCart shoppingCart = cartService.getShoppingCart(req.getSession());

        User user = (User) req.getSession().getAttribute("user");

        if (user.getShippingAddresses().isEmpty()) {
            //TODO goToErrorPage instaed of returning an error response
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User has no shipping address, that shouldn't be allowed ");
            return;
        }


        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        webContext.setVariable("shoppingCart", shoppingCart);
        webContext.setVariable("shoppingAddresses", user.getShippingAddresses());
        TemplateHandler.templateEngine.process(PathUtils.pathToShoppingCartPage, webContext, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
