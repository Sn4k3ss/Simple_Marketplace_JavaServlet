package com.ss.TIW_2021project.web.controller.GoToPage;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.CartService;
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
        name = "GoToHome",
        description = "This servlet handles how the home page must be processed",
        value = "/GoToHome"
)
public class GoToHome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = (User) req.getSession(false).getAttribute("user");

        ProductService productService = new ProductService();
        new CartService().initShoppingCart(req.getSession(false));

        ProductsCatalogue retrievedProducts = null;

        try {
            retrievedProducts = productService.getLastUserProducts(user.getUserId());
        } catch (ServiceException e) {
            String errorMessage = "Couldn't get user's last products";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            forward(req, resp, errorMessage);
            return;
        }

        req.getSession().setAttribute("last_user_products", retrievedProducts);
        forward(req, resp, null);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String errorMessage) throws IOException {

        String path = PathUtils.pathToHomePage;

        if (errorMessage != null) {
            req.setAttribute("errorMessage", errorMessage);
            path = PathUtils.pathToErrorPage;
        }

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        TemplateHandler.templateEngine.process(path, webContext, resp.getWriter() );
    }

}
