package com.ss.TIW_2021project.web.controller.action;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.product.SupplierProduct;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.utils.ServletUtility;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(
        name = "AddToCart",
        description = "This servlet handles the 'add to cart' action whenever triggered",
        value = "/AddToCart"
)
@MultipartConfig
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        SupplierProduct supplierProduct;

        Integer howMany;

        try {
            howMany= Integer.parseInt(req.getParameter("howMany"));
        } catch (NumberFormatException e) {
            howMany = 1;
        }

        ProductsCatalogue lastUserProducts = (ProductsCatalogue) req.getSession().getAttribute("last_user_products");
        ProductsCatalogue productsFromQuery = (ProductsCatalogue) req.getSession().getAttribute("products_from_query");

        List<ProductsCatalogue> catalogues = new ArrayList<>();
        if (lastUserProducts != null)
            catalogues.add(lastUserProducts);
        if (productsFromQuery != null)
            catalogues.add(productsFromQuery);

        try {
            supplierProduct = ServletUtility.buildProductFromRequest(req);
        } catch (UtilityException e) {
            String errorMessage = "The server encountered an error parsing the request's parameters";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }

        ProductService productService = new ProductService();
        SupplierProduct product = productService.lookForProduct(catalogues, supplierProduct);

        if(product == null) {
            String errorMessage = "Where did you get that product?";
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println(errorMessage);
            return;
        }

        //il carrello esiste solo sul client (session storage)
        //la servlet si può forse eliminare totalmente


        resp.setStatus(HttpServletResponse.SC_OK);
    }


}
