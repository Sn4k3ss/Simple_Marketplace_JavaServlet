package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.OrderService;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.business.services.UserService;
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
import java.util.List;

@WebServlet(
        name = "showOrders",
        description = "This is my first annotated servlet",
        urlPatterns = {("/orders"),("/showOrders")}
)
public class showOrders extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = MarketplaceApp.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, UnavailableException {

        User user = (User) req.getSession().getAttribute("user");

        OrderService orderService = new OrderService(getServletContext());
        List<Order> orders = orderService.retrieveUserOrders(user.getUserId());

        //serve per prendere le informazioni di User
        UserService userService = new UserService(getServletContext());
        userService.setUserInfoOnOrder(orders);
        //serve prendere informazioni sul venditore
        SupplierService supplierService = new SupplierService(getServletContext());
        supplierService.setSupplierIndoOnOrder(orders);
        //serve per prendere informazioni sui prodotti dell'ordine
        ProductService productService = new ProductService(getServletContext());
        productService.setProductInfoOnOrder(orders);

        //TODO da implementare




        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        webContext.setVariable("orders", orders);
        templateEngine.process("orders", webContext, resp.getWriter());
    }
}
