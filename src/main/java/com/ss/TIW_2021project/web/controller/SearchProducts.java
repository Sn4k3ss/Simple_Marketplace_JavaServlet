package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
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

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = MarketplaceApp.getTemplateEngine();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer selectedProductId = 0;
        String keyword = req.getParameter("keyword");

        ProductService productService = new ProductService(req.getServletContext());
        SupplierService supplierService = new SupplierService(req.getServletContext());

        //Product are duplicated but with different suppliers
        ProductsCatalogue retrievedProducts = null;

        try {
            retrievedProducts = productService.getRelevantProducts(keyword);
            supplierService.setSuppliersToProducts(retrievedProducts);

        } catch (UnavailableException e) {
            //ERRORE SERVLET
            //TODO.....
        }


        req.getSession().setAttribute("products_from_query", retrievedProducts);

        //redirect to the search page with the products retrieved displayed in a table
        final WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        webContext.setVariable("products", retrievedProducts);
        webContext.setVariable("selectedProductId", selectedProductId);
        templateEngine.process("searchProducts", webContext, resp.getWriter());
    }
}
