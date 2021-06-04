package com.ss.TIW_2021project.web.controller.Actions;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.UserService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

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
public class Login extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = getServletContext().getContextPath() + PathUtils.goToHomeServletPath;
        resp.sendRedirect(path);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String email;
        String password;

        try {
            email = req.getParameter("email");
            password = req.getParameter("password");

            if (email==null || password==null) {
                invalidCredentials("Login error: Credentials can't be null", req, resp);
                return;
            }
            else if(email.isEmpty() || password.isEmpty()) {
                invalidCredentials("Login error: Credentials can't be empty", req, resp);
                return;
            }
        } catch (NullPointerException e) {
            invalidCredentials("Somtehing went wrong, please try again", req, resp);
            return;
        }

        UserService userService = new UserService();
        User user;

        try {
            user = userService.checkCredentials(email, password);
        } catch (ServiceException e) {
            String errorMessage = "Login error: Not Possible to check credentials";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            forward(req, resp, errorMessage);
            return;
        }


        if (user == null) {
            String errorMessage = "Login error: Incorrect email or password";
            invalidCredentials(errorMessage, req, resp);
            return;
        }

        req.getSession().setAttribute("user", user);

        String path = getServletContext().getContextPath() + PathUtils.goToHomeServletPath;
        resp.sendRedirect(path);
    }

    private void invalidCredentials(String errorMessage, HttpServletRequest request, HttpServletResponse response) throws IOException {

        request.setAttribute("loginErrorMessage", errorMessage);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        forward(request, response, errorMessage);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String errorMessage) throws IOException {

        String path = PathUtils.pathToHomePage;

        if (errorMessage != null && resp.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            req.setAttribute("errorMessage", errorMessage);
            path = PathUtils.pathToErrorPage;
        } else if (errorMessage != null && resp.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
            req.setAttribute("errorMessage", errorMessage);
            path = PathUtils.pathToLoginPage;
        }

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        TemplateHandler.templateEngine.process(path, webContext, resp.getWriter() );
    }

}
