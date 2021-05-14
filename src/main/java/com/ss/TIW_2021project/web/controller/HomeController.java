package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.User;
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
import java.util.List;

@WebServlet("/home")
public class HomeController extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
       this.templateEngine = MarketplaceApp.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = (User) req.getSession(false).getAttribute("user");

        ProductService productService = new ProductService(getServletContext());
        SupplierService supplierService = new SupplierService(getServletContext());
        ProductsCatalogue retrievedProducts = null;

        try {
            retrievedProducts = productService.getLastUserProducts(user.getUserId());
            supplierService.setSuppliersToProducts(retrievedProducts);
        } catch (UnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Not possible to get last user's products");
            //una possibile soluzione: piuttosto che far fallire la chaimata alla doGet della servlet
            //si potrebbe gestire l'eccezione in modo tale che la tabellla con i prodotti recenti non venga visualizzata
            //retrievedProducts = new ArrayList<>();
            return;
        }

        req.getSession().setAttribute("last_user_products", retrievedProducts);


        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        webContext.setVariable("userInfo", user);
        webContext.setVariable("products", retrievedProducts);
        templateEngine.process("home", webContext, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
