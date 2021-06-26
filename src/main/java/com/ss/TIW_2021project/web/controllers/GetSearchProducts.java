package com.ss.TIW_2021project.web.controllers;

import com.google.gson.Gson;
import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
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
        name = "GetSearchProducts",
        description = "This servlet handles the search through the catalogue",
        value = "/GetSearchProducts"
)
public class GetSearchProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer selectedProductId = 0;

        ProductService productService = new ProductService();

        ProductsCatalogue retrievedProducts = null;

        try {
            String keyword = req.getParameter("keyword");

            if (keyword == null) {
                String errorMessage = "Search string can't be null";
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(errorMessage);
                return;
            } else if (keyword.isEmpty()) {
                String errorMessage = "Search string can't be empty";
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(errorMessage);
                return;
            }

            retrievedProducts = productService.getRelevantProducts(keyword);
        } catch (ServiceException e) {
            String errorMessage = "Couldn't execute your request";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }


        //req.getSession().setAttribute("products_from_query", retrievedProducts);

        //req.setAttribute("products", retrievedProducts);
        //req.setAttribute("selectedProductId", selectedProductId);


        String products = new Gson().toJson(retrievedProducts);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(products);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

}
