package com.ss.TIW_2021project.web.controllers;

import com.google.gson.Gson;
import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.OrderService;

import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "getOrders",
        description = "This servlet handles the requests to get all the orders by the current user",
        value = "/GetOrders"
)
public class GetOrders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, UnavailableException {


        User user = (User) req.getSession().getAttribute("user");
        OrderService orderService = new OrderService();
        List<Order> orders;

        try {
            orders = orderService.retrieveUserOrders(user.getUserId());
        } catch (ServiceException e) {
            String errorMessage = "Couldn't get infos about orders";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }

        String ordersJson = new Gson().toJson(orders);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(ordersJson);
    }


}
