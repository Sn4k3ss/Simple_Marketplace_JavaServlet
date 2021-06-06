package com.ss.TIW_2021project.web.controller.Actions;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.UserService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "Signup",
        description = "This is my first annotated servlet",
        value = "/signup"
)
public class Signup extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        TemplateHandler.templateEngine.process(PathUtils.pathToLoginPage, webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String email;
        String password;
        String firstName;
        String lastName;
        String address;
        String addrCity;
        String addrState;
        String addrPhone;

        try {
            email = req.getParameter("email");
            password = req.getParameter("password");
            firstName = req.getParameter("firstName");
            lastName = req.getParameter("lastName");
            address = req.getParameter("address");
            addrCity = req.getParameter("addrCity");
            addrState = req.getParameter("addrState");
            addrPhone = req.getParameter("addrPhone");

            if (email==null || password==null || firstName == null || lastName == null
                    || address == null || addrCity == null || addrState == null || addrPhone == null) {
                invalidSignUpCredentials("Signup error: Credentials can't be null", req, resp);
                return;
            }
            else if(email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()
                    || address.isEmpty() || addrCity.isEmpty() || addrState.isEmpty() || addrPhone.isEmpty()) {
                invalidSignUpCredentials("Signup error: Credentials can't be empty", req, resp);
                return;
            }
        } catch (NullPointerException e) {
            invalidSignUpCredentials("Somthing went wrong, please try again", req, resp);
            return;
        }

        UserService userService = new UserService();
        User user;

        try {
            user = userService.registerUser(email, password, firstName, lastName, address, addrCity, addrState, addrPhone);
        } catch (ServiceException e) {
            String errorMessage = "Login error: Not Possible to validate data";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            forward(req, resp, errorMessage);
            return;
        }

        if (user == null) {
            String errorMessage = "Registration successful but can't get user info";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            forward(req, resp, errorMessage);
            return;
        }

        req.getSession().setAttribute("user", user);

        String path = getServletContext().getContextPath() + PathUtils.goToHomeServletPath;
        resp.sendRedirect(path);
    }

    private void invalidSignUpCredentials(String errorMessage, HttpServletRequest request, HttpServletResponse response) throws IOException {

        request.setAttribute("signupErrorMessage", errorMessage);
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
