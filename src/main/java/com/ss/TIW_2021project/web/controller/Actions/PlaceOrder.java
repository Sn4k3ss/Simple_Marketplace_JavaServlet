package com.ss.TIW_2021project.web.controller.Actions;

import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.services.CartService;
import com.ss.TIW_2021project.business.services.OrderService;
import com.ss.TIW_2021project.business.services.SupplierService;
import com.ss.TIW_2021project.business.utils.PathUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(
        name = "PlaceOrder",
        description = "This controller handles everything about orders",
        value = "/PlaceOrder"
)
public class PlaceOrder extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer supplierId = Integer.parseInt(req.getParameter("supplierId"));
        Integer userShippingAddressId = Integer.parseInt(req.getParameter("userShippingAddressId"));

        CartService cartService = new CartService();
        ShoppingCart shoppingCart = cartService.getShoppingCart(req.getSession());
        User user = (User) req.getSession(false).getAttribute("user");
        ShippingAddress shippingAddress = user.getShippingAddresses().stream()
                .filter(x -> x.getShippingAddressId().equals(userShippingAddressId)).findFirst().orElse(null);

        SupplierService supplierService = new SupplierService();
        OrderService orderService = new OrderService();

        try {

            Float totalAmountAtSupplier = shoppingCart.getAmountBySupplier(supplierId);
            Float shippingFees = supplierService.computeShippingFees(shoppingCart.getProductsFromSupplier(supplierId), supplierId, totalAmountAtSupplier);
            LocalDate deliveryDate = supplierService.computeDeliveryDate(shippingAddress, supplierId);
            List<ShoppingCartProduct> productsList = shoppingCart.getProductsFromSupplier(supplierId);

            Order newOrder = orderService.createOrder(productsList, user, shippingAddress, totalAmountAtSupplier, shippingFees, deliveryDate);
            orderService.placeOrder(newOrder);
        } catch (ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't place your order");
            return;
        }

        shoppingCart.emptyShoppingCart(supplierId);

        String path = getServletContext().getContextPath() + PathUtils.goToOrdersServletPath;
        resp.sendRedirect(path);
    }
}
