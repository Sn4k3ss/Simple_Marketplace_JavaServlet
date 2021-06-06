package com.ss.TIW_2021project.web.controller.Actions;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer selectedProductId = 0;

        ProductService productService = new ProductService();

        ProductsCatalogue retrievedProducts = null;

        try {
            String keyword = req.getParameter("keyword");

            if (keyword == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                forward(req, resp, "Search string can't be null");
                return;
            } else if (keyword.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                forward(req, resp, "Search string can't be empty");
                return;
            }

            retrievedProducts = productService.getRelevantProducts(keyword);
        } catch (ServiceException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            forward(req, resp, "Couldn't execute your request");
            return;
        }


        req.getSession().setAttribute("products_from_query", retrievedProducts);

        req.setAttribute("products", retrievedProducts);
        req.setAttribute("selectedProductId", selectedProductId);

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
        } else if (errorMessage != null && resp.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
            req.setAttribute("errorMessage", errorMessage);
            path = PathUtils.pathToErrorPage;
        }

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        TemplateHandler.templateEngine.process(path, webContext, resp.getWriter() );
    }
}
