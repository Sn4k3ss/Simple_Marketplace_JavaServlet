package com.ss.TIW_2021project.web.controller.Actions;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "SearchProducts",
        description = "This servlet handles the search through the catalogue",
        value = "/search/products"
)
public class SearchProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer selectedProductId = 0;

        ProductService productService = new ProductService();

        ProductsCatalogue retrievedProducts = null;

        try {
            String keyword = req.getParameter("keyword");

            if (keyword == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Search string can't be null");
                return;
            } else if (keyword.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Search string can't be empty");
                return;
            }

            retrievedProducts = productService.getRelevantProducts(keyword);
        } catch (ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't execute your request");
            return;
        }


        req.getSession().setAttribute("products_from_query", retrievedProducts);

        //redirect to the search page with the products retrieved displayed in a table
        final WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        webContext.setVariable("products", retrievedProducts);
        webContext.setVariable("selectedProductId", selectedProductId);
        TemplateHandler.templateEngine.process(PathUtils.pathToSearchProductsPage, webContext, resp.getWriter());
    }
}
