package com.ss.TIW_2021project.web.controllers;

import com.google.gson.*;
import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.entities.product.ShoppingCartProduct;
import com.ss.TIW_2021project.business.entities.product.SupplierProduct;
import com.ss.TIW_2021project.business.services.OrderService;
import com.ss.TIW_2021project.business.services.ProductService;
import com.ss.TIW_2021project.business.services.SupplierService;


import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(
        name = "PlaceOrder",
        description = "This controller handles everything about orders",
        value = "/PlaceOrder"
)
@MultipartConfig
public class PlaceOrder extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ShoppingCart shoppingCart = new ShoppingCart();

        JsonElement jsonElement;
        try {
            jsonElement = JsonParser.parseString(req.getParameter("prodsJson"));
        } catch (JsonParseException e) {
            String errorMessage = "Couldn't get products from cart";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }

        //no products for selected supplierId
        if (jsonElement.toString().equals("\"undefined\"")) {
            String errorMessage = "You have no products in cart for the requested supplier";
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(errorMessage);
            return;
        }

        int uniqueProds = jsonElement.getAsJsonArray().size();

        Integer supplId = new Gson().fromJson(((JsonArray) jsonElement).get(0), ShoppingCartProduct.class).getSupplierId();

        ProductService productService = new ProductService();
        List<SupplierProduct> supplierProductsList = null;
        try {
            supplierProductsList = productService.getProductsBySupplier(supplId);
        } catch (ServiceException e) {
            String errorMessage = "Not possible to check if products is on sale";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }

        try {
            SupplierService supplierService = new SupplierService();

            if (supplierService.getSupplierById(supplId) == null) {
                String errorMessage = "Supplier not found";
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(errorMessage);
                return;
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < uniqueProds; i++) {
            JsonElement j = ((JsonArray) jsonElement).get(i);
            ShoppingCartProduct p = new Gson().fromJson(j, ShoppingCartProduct.class);

            if (checkProductIsOnSale(supplierProductsList, p.getProductId())) {
                shoppingCart.addProductToCart(p, p.getHowMany());
            } else {
                String errorMessage = "You're not allowed to place the order";
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(errorMessage);
                return;
            }
        }


        Integer supplierId = Integer.parseInt(req.getParameter("supplierId"));
        Integer userShippingAddressId = Integer.parseInt(req.getParameter("userShippingAddressId"));
        User user = (User) req.getSession(false).getAttribute("user");



        ShippingAddress shippingAddress = user.getShippingAddresses().stream()
                    .filter(x -> x.getShippingAddressId().equals(userShippingAddressId)).findFirst().orElse(null);

        if (shippingAddress == null) {
            String errorMessage = "You have selected an invalid address";
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(errorMessage);
            return;
        }

        SupplierService supplierService = new SupplierService();
        OrderService orderService = new OrderService();

        try {

            Float totalAmountAtSupplier = shoppingCart.getAmountBySupplier(supplierId);
            Float shippingFees = supplierService.computeShippingFees(shoppingCart.getProductsFromSupplier(supplierId), supplierId, totalAmountAtSupplier);
            LocalDate deliveryDate = supplierService.computeDeliveryDate(shippingAddress, supplierId);
            List<ShoppingCartProduct> cartProductsList = shoppingCart.getProductsFromSupplier(supplierId);

            Order newOrder = orderService.createOrder(cartProductsList, user, shippingAddress, totalAmountAtSupplier, shippingFees, deliveryDate);
            orderService.placeOrder(newOrder);
        } catch (ServiceException e) {
            String errorMessage = "Couldn't place your order";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(errorMessage);
            return;
        }

        shoppingCart.emptyShoppingCart(supplierId);

        resp.setStatus(HttpServletResponse.SC_OK);
    }


    private boolean checkProductIsOnSale(List<SupplierProduct> productsOnSale, Integer p) {

        for(SupplierProduct productOnSale : productsOnSale)
            if (productOnSale.getProductId().equals(p))
                return true;

        return false;
    }

}
