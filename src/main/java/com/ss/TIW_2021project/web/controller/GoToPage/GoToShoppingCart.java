package com.ss.TIW_2021project.web.controller.GoToPage;

import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

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

        User user = (User) req.getSession().getAttribute("user");

        if (user.getShippingAddresses().isEmpty()) {
            String errorMessage = "User has no shipping address, that shouldn't be allowed";
            req.setAttribute("errorMessage", errorMessage);
            forward(req, resp, errorMessage);
            return;
        }

        req.setAttribute("shoppingAddresses", user.getShippingAddresses());
        forward(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String errorMessage) throws IOException {

        String path = PathUtils.pathToShoppingCartPage;

        if (errorMessage != null) {
            req.setAttribute("errorMessage", errorMessage);
            path = PathUtils.pathToErrorPage;
        }

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        TemplateHandler.templateEngine.process(path, webContext, resp.getWriter() );
    }
}
