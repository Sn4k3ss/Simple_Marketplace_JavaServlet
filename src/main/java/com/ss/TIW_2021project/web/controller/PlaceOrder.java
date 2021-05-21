package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.services.OrderService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.business.services.UserService;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(
        name = "PlaceOrder",
        description = "This controller handles everything about orders",
        value = "/placeOrder"
)
public class PlaceOrder extends HttpServlet {

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

        Integer supplierId = Integer.parseInt(req.getParameter("supplierId"));
        Integer userShippingAddressId = Integer.parseInt(req.getParameter("userShippingAddressId"));

        CartService cartService = new CartService(getServletContext());
        ShoppingCart shoppingCart = cartService.getShoppingCart(req.getSession());
        User user = (User) req.getSession(false).getAttribute("user");


        try {
            UserService userService = new UserService(getServletContext());
            ShippingAddress shippingAddress = userService.getShippingAddress(user.getUserId(), userShippingAddressId);

            SupplierService supplierService = new SupplierService(getServletContext());
            Float totalAmountAtSupplier = shoppingCart.getTotalAmountBySupplier(supplierId);
            Float shippingFees = supplierService.computeShippingFees(shoppingCart.getProductsFromSupplier(supplierId), supplierId, totalAmountAtSupplier);
            LocalDate deliveryDate = supplierService.computeDeliveryDate(shippingAddress, supplierId);
            List<ShoppingCartProduct> productsList = shoppingCart.getProductsFromSupplier(supplierId);


            OrderService orderService = new OrderService(getServletContext());
            Order newOrder = orderService.createOrder(productsList, user, shippingAddress, totalAmountAtSupplier, shippingFees, deliveryDate);
            orderService.placeOrder(newOrder);
        } catch (ServletException e) {
            //To be handled
            //TODO


        }

        //Once the order is placed now we can remove that supplier from the shoppingCart
        shoppingCart.emptyShoppingCart(supplierId);

        String path = getServletContext().getContextPath() + "/showOrders";
        resp.sendRedirect(path);
    }
}
