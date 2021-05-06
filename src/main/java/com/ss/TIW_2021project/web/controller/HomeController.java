package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User user = (User) req.getSession(false).getAttribute("user");

        ProductService productService = new ProductService(getServletContext());


        List<SupplierProduct> retrievedProducts = productService.getLastUserProducts(user.getUserId());



        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        webContext.setVariable("userInfo", user);
        webContext.setVariable("retrievedProducts", retrievedProducts);
        templateEngine.process("home", webContext, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }
}
