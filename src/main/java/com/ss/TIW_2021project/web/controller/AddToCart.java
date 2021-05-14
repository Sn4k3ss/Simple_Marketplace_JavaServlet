package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.business.utils.ServletUtility;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(
        name = "AddToCart",
        description = "This servlet handles the 'add to cart' action whenever is triggered",
        value = "/products/AddToCart"
)
public class AddToCart extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
       this.templateEngine = MarketplaceApp.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SupplierProduct supplierProduct;

        //the request contains: -productId
        //                      -supplierId
        //                      -supplierProductCost

        //TODO
        //to be handled the session check before getting the attr

        Integer howMany;

        try {
            howMany= Integer.parseInt(req.getParameter("howMany"));
        } catch (NumberFormatException e) {
            //se errore nell'invio del parametro setta a 1 e chi s'è visto s'è visto
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
        } catch (UnavailableException e) {
            System.out.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        ProductService productService = new ProductService(getServletContext());
        SupplierProduct product = productService.lookForProduct(catalogues, supplierProduct);

        if(product == null) {
            System.out.println("Where did you get that product?");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }



        CartService cartService = new CartService(getServletContext());
        ShoppingCart shoppingCart = cartService.addToCart(req.getSession(), product, howMany);


        String path = getServletContext().getContextPath() + "/shoppingCart";
        resp.sendRedirect(path);
    }



}
