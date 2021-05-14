package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OrdersDAO {

    private Connection connection;

    public OrdersDAO(ServletContext servletContext) throws UnavailableException {
        connection = ConnectionFactory.getConnection(servletContext);
    }


    public Order getOrder(Integer orderId) {
        //TODO
        return null;
    }

    public List<Order> getAllUserOrders(Integer userId) {
        //TODO
        return Collections.emptyList();
    }

    public void placeOrder(Order newOrder) {
        //TODO
    }
}

