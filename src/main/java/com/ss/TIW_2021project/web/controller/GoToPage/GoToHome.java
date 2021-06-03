package com.ss.TIW_2021project.web.controller.GoToPage;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.business.utils.PathUtils;
import com.ss.TIW_2021project.web.application.TemplateHandler;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "GoToHome",
        description = "This servlet handles how the home page must be processed",
        value = "/GoToHome"
)
public class GoToHome extends HttpServlet {

    private ITemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        this.templateEngine = TemplateHandler.getTemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = (User) req.getSession(false).getAttribute("user");

        ProductService productService = new ProductService(getServletContext());
        SupplierService supplierService = new SupplierService(getServletContext());
        ProductsCatalogue retrievedProducts = null;

        try {
            retrievedProducts = productService.getLastUserProducts(user.getUserId());
            supplierService.setSuppliersToProductsInCatalogue(retrievedProducts);
        } catch (ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't get user's last products");
            return;
        }

        req.getSession().setAttribute("last_user_products", retrievedProducts);

        WebContext webContext = new WebContext(req, resp, getServletContext(), req.getLocale());
        webContext.setVariable("userInfo", user);
        webContext.setVariable("products", retrievedProducts);
        templateEngine.process(PathUtils.pathToHomePage, webContext, resp.getWriter());
    }

}
