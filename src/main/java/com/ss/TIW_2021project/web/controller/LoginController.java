package com.ss.TIW_2021project.web.controller;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.UserService;
import com.ss.TIW_2021project.web.application.MarketplaceApp;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "LoginController",
        description = "This is my first annotated servlet",
        value = "/login"
)
public class LoginController extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
       this.templateEngine = MarketplaceApp.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        templateEngine.process("../../index.html", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String email;
        String password;

        try {
            email = req.getParameter("email");
            password = req.getParameter("password");

            if (email==null || password==null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                invalidCredentials("Login error: Credentials can't be null", req, resp);
                return;
            }
            else if(email.isEmpty() || password.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                invalidCredentials("Login error: Credentials can't be empty", req, resp);
                return;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            invalidCredentials("Somthing went wrong, please try again", req, resp);
            return;
        }

        UserService userService = new UserService(getServletContext());
        User user;

        try {
            user = userService.checkCredentials(email, password);
        } catch (ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login error: Not Possible to check credentials");
            return;
        }


        if (user == null) {
            invalidCredentials("Login error: Incorrect email or password", req, resp);
            return;
        }

        req.getSession().setAttribute("user", user);

        String path = getServletContext().getContextPath() + "/home";
        resp.sendRedirect(path);
    }

    private void invalidCredentials(String errorMessage, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("errorMessage", errorMessage);
        templateEngine.process("../../index", webContext, response.getWriter());
    }
}
