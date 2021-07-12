package com.ss.TIW_2021project.web.controller.Actions;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.product.SupplierProduct;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.business.utils.ServletUtility;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
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
        value = "/products/AddToCart"
)
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

        if(howMany <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            forwardToErrorPage(req, resp, "You must add at least one product, please");
            return;
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
            forwardToErrorPage(req, resp, "The server encountered an error parsing the request's parameters");
            return;
        }

        ProductService productService = new ProductService();
        SupplierProduct product = productService.searchProductInCatalogue(catalogues, supplierProduct);

        if(product == null) {
            forwardToErrorPage(req, resp, "Where did you get that product?");
            return;
        }

        CartService cartService = new CartService();
        cartService.addToCart(req.getSession(), product, howMany);

        req.getSession().removeAttribute("products_from_query");
        req.getSession().removeAttribute("last_user_products");


        String path = getServletContext().getContextPath() + PathUtils.goToShoppingCartServletPath;
        resp.sendRedirect(path);
    }


    private void forwardToErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorMessage) throws IOException {

        req.setAttribute("errorMessage", errorMessage);
        String path = PathUtils.pathToErrorPage;

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        TemplateHandler.templateEngine.process(path, webContext, resp.getWriter() );
    }


}
