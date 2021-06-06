package com.ss.TIW_2021project.web.controller.Actions;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ShowProductInfo",
        description = "This servlet handle the click on a specific product to show the relative info",
        value = "/search/products/ShowProductInfo"
)
public class ShowProductInfo extends HttpServlet {

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
            forward(req, resp, errorMessage);
            return;
        }

        ProductsCatalogue productsFromQuery;

        if ( req.getSession().getAttribute("products_from_query") != null ) {
            productsFromQuery = (ProductsCatalogue) req.getSession().getAttribute("products_from_query");

        } else {
            String errorMessage = "Something ain't right";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            forward(req, resp, errorMessage);
            return;
        }

        
        req.setAttribute("products", productsFromQuery);
        req.setAttribute("selectedProductId", productId);

        forward(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String errorMessage) throws IOException {

        String path = PathUtils.pathToSearchProductsPage;

        if (errorMessage != null && resp.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            req.setAttribute("errorMessage", errorMessage);
            path = PathUtils.pathToErrorPage;
        }

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        TemplateHandler.templateEngine.process(path, webContext, resp.getWriter() );
    }

}