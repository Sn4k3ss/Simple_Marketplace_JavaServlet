package com.ss.TIW_2021project.web.controller.GoToPage;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.OrderService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

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
        value = "/GoToOrders"
)
public class GoToOrders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, UnavailableException {


        User user = (User) req.getSession().getAttribute("user");
        OrderService orderService = new OrderService();
        List<Order> orders;

        try {
            orders = orderService.retrieveUserOrders(user.getUserId());
        } catch (ServiceException e) {
            String errorMessage = "Couldn't get infos about orders";
            req.setAttribute("errorMessage", errorMessage);
            forward(req, resp, errorMessage);
            return;
        }

        req.setAttribute("orders", orders);
        forward(req, resp, null);
    }


    private void forward(HttpServletRequest req, HttpServletResponse resp, String errorMessage) throws IOException {

        String path = PathUtils.pathToOrdersPage;

        if (errorMessage != null) {
            req.setAttribute("errorMessage", errorMessage);
            path = PathUtils.pathToErrorPage;
        }

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        TemplateHandler.templateEngine.process(path, webContext, resp.getWriter() );
    }
}
