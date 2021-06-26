package com.ss.TIW_2021project.web.controller.GoToPage;

import com.google.gson.Gson;
import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.services.ProductService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "GetHomeProducts",
        description = "This servlet look for the p",
        value = "/GetHomeProducts"
)
public class GetHomeProducts extends HttpServlet {

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
            resp.getWriter().println(errorMessage);
            return;
        }

        String products = new Gson().toJson(retrievedProducts);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(products);
    }


}
