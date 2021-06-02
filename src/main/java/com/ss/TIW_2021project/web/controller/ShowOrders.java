package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
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
public class ShowOrders extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = MarketplaceApp.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, UnavailableException {

        User user = (User) req.getSession().getAttribute("user");
        List<Order> orders;

        try {

            OrderService orderService = new OrderService(getServletContext());
            orders = orderService.retrieveUserOrders(user.getUserId());

            if (!orders.isEmpty()) {
            //serve per prendere le informazioni di User
            //senza prendere lo user dal db si potrebbe settare quello già disponibile nella sessione
            UserService userService = new UserService(getServletContext());
            userService.setUserInfoOnOrders(orders);

            //serve prendere informazioni di Supplier
            SupplierService supplierService = new SupplierService(getServletContext());
            supplierService.setSupplierInfoOnOrders(orders);

            //serve per prendere informazioni su ShoppingCartProduct
            ProductService productService = new ProductService(getServletContext());
            productService.setProductInfoOnOrders(orders);
            }
        } catch (ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't get infos about orders");
            return;
        }


        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        webContext.setVariable("orders", orders);
        templateEngine.process("orders", webContext, resp.getWriter());
    }
}
