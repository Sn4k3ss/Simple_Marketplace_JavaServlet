package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.OrdersDAO;
import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class OrderService {

    private ServletContext servletContext;

    public OrderService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    public Order createOrder(List<ShoppingCartProduct> productsList, User user, ShippingAddress shippingAddress,Float productsOnlyCost, Float shippingFees, LocalDate deliveryDate) {

        Order newOrder = new Order();

        ShoppingCartProduct prod = productsList.get(0);
        Supplier sup = prod.getSupplier();

        newOrder.setOrderSupplier(prod.getSupplier());
        newOrder.setDeliveryDate(deliveryDate);
        newOrder.setUser(user);
        newOrder.setShippingAddress(shippingAddress);
        newOrder.setOrderAmount(productsOnlyCost + shippingFees);
        newOrder.setOrderProductsList(productsList);
        newOrder.setOrderShippingFees(shippingFees);

        return newOrder;
    }

    public void placeOrder(Order newOrder) throws UnavailableException {

        OrdersDAO ordersDAO = new OrdersDAO(servletContext);

        try {
            ordersDAO.placeOrder(newOrder);
        } catch (SQLException ex) {
            //TODO
            ex.printStackTrace();
        }

    }

    public List<Order> retrieveUserOrders(Integer userId) throws UnavailableException {
        List<Order> orders;

        OrdersDAO ordersDAO = new OrdersDAO(servletContext);

        try {
             orders = ordersDAO.retrieveUserOrders(userId);
        } catch (SQLException exception) {
            throw new UnavailableException("Error while loading user orders");
        }

        return orders;
    }
}
