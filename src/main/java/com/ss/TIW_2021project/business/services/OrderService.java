package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.dao.OrdersDAO;
import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;

import java.time.LocalDate;
import java.util.*;

public class OrderService {

    public OrderService() {
        super();
    }



    public Order createOrder(List<ShoppingCartProduct> productsList,
                             User user,
                             ShippingAddress shippingAddress,
                             Float productsOnlyCost,
                             Float shippingFees,
                             LocalDate deliveryDate) {

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

    public void placeOrder(Order newOrder) throws ServiceException {

        try {
            OrdersDAO ordersDAO = new OrdersDAO();
            ordersDAO.placeOrder(newOrder);
        } catch (DAOException e) {
            throw new ServiceException(ServiceException._PLACE_ORDER_ERROR);
        }

    }

    public List<Order> retrieveUserOrders(Integer userId) throws ServiceException {
        List<Order> orders;

        try {
            OrdersDAO ordersDAO = new OrdersDAO();
            orders = ordersDAO.retrieveUserOrders(userId);
        } catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_ORDERS);
        }

        return orders;
    }
}
