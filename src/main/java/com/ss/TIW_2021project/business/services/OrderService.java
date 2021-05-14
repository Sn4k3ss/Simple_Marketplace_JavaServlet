package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.OrdersDAO;
import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderService {

    private ServletContext servletContext;

    public OrderService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    public Order createOrder(List<ShoppingCartProduct> productsList, User user, ShippingAddress shippingAddress,Float productsOnlyCost, Float shippingFees) {

        Order newOrder = new Order();

        ShoppingCartProduct prod = productsList.get(0);
        Supplier sup = prod.getSupplier();

        newOrder.setOrderSupplier(prod.getSupplier());

        //setting deliveryDate
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        Date fiveDaysLater = calendar.getTime();
        newOrder.setDeliveryDate(fiveDaysLater);

        newOrder.setUser(user);
        newOrder.setShippingAddress(shippingAddress);
        newOrder.setOrderAmount(productsOnlyCost + shippingFees);
        newOrder.setOrderProductsList(productsList);
        newOrder.setOrderShippingFees(shippingFees);

        return newOrder;
    }

    public void placeOrder(Order newOrder) throws UnavailableException {

        OrdersDAO ordersDAO = new OrdersDAO(servletContext);

        ordersDAO.placeOrder(newOrder);

    }

    public List<Order> retrieveUserOrders(Integer userId) throws UnavailableException {

        OrdersDAO ordersDAO = new OrdersDAO(servletContext);

        return ordersDAO.getAllUserOrders(userId);

    }
}
