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
import java.util.List;

@WebServlet(
        name = "PlaceOrder",
        description = "This servlet checks that the order can be placed",
        value = "/PlaceOrder"
)
@MultipartConfig
public class PlaceOrder extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ShoppingCart shoppingCart = new ShoppingCart();
        JsonElement jsonElement;

        //products parsing
        try {
            jsonElement = JsonParser.parseString(req.getParameter("prodsJson"));
        } catch (JsonParseException e) {
            invalidRequest(resp, "Couldn't get products from cart");
            return;
        }

        //no products for selected supplierId
        if (jsonElement.toString().equals("\"undefined\"")) {
            invalidRequest(resp, "You have no products in cart for the requested supplier");
            return;
        }

        int uniqueProds = jsonElement.getAsJsonArray().size();
        Integer supplierIdFromCart = new Gson().fromJson(((JsonArray) jsonElement).get(0), ShoppingCartProduct.class).getSupplierId();


        //Si prendono i prodotti forniti dal venditore
        ProductService productService = new ProductService();
        List<SupplierProduct> supplierCatalogue = null;
        try {
            supplierCatalogue = productService.getProductsBySupplier(supplierIdFromCart);
        } catch (ServiceException e) {
            internalError(resp, "Not possible to check if products is on sale");
            return;
        }

        //se supplierId non coincide errore
        try {
            SupplierService supplierService = new SupplierService();
            if (supplierService.getSupplierById(supplierIdFromCart) == null) {
                invalidRequest(resp, "Supplier not found");
                return;
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < uniqueProds; i++) {
            JsonElement j = ((JsonArray) jsonElement).get(i);
            ShoppingCartProduct p = new Gson().fromJson(j, ShoppingCartProduct.class);
            Integer prodId = p.getProductId();
            Integer howMany = p.getHowMany();

            if (checkProductIsOnSale(supplierCatalogue, prodId)) {
                SupplierProduct product = null;
                try {
                    product = getProdFromSuppl(supplierCatalogue, prodId);
                } catch (ServiceException e) {
                    internalError(resp, "Not possible to check products from supplier");
                }
                shoppingCart.addProductToCart(product, howMany);
            } else {
                invalidRequest(resp, "You're not allowed to place the order");
                return;
            }
        }

        Integer supplierIdFromReqParam = null;
        Integer userShippingAddressId = null;
        try {
            supplierIdFromReqParam = Integer.parseInt(req.getParameter("supplierId"));
            userShippingAddressId = Integer.parseInt(req.getParameter("userShippingAddressId"));
        } catch (NumberFormatException e) {
            invalidRequest(resp, "Impossible to check correct parameters");
        }
        User user = (User) req.getSession(false).getAttribute("user");


        Integer finalUserShippingAddressId = userShippingAddressId;
        ShippingAddress shippingAddress = user.getShippingAddresses().stream()
                    .filter(x -> x.getShippingAddressId().equals(finalUserShippingAddressId)).findFirst().orElse(null);

        if (shippingAddress == null) {
            invalidRequest(resp, "You have selected an invalid address");
            return;
        } else if (!supplierIdFromCart.equals(supplierIdFromReqParam)) {
            invalidRequest(resp,"The supplier doesn't match");
            return;
        }

        SupplierService supplierService = new SupplierService();
        OrderService orderService = new OrderService();

        try {

            Float totalAmountAtSupplier = shoppingCart.getAmountBySupplier(supplierIdFromReqParam);
            Float shippingFees = supplierService.computeShippingFees(shoppingCart.getProductsFromSupplier(supplierIdFromReqParam), supplierIdFromReqParam, totalAmountAtSupplier);
            LocalDate deliveryDate = supplierService.computeDeliveryDate(shippingAddress, supplierIdFromReqParam);
            List<ShoppingCartProduct> cartProductsList = shoppingCart.getProductsFromSupplier(supplierIdFromReqParam);

            Order newOrder = orderService.createOrder(cartProductsList, user, shippingAddress, totalAmountAtSupplier, shippingFees, deliveryDate);
            orderService.placeOrder(newOrder);
        } catch (ServiceException e) {
            internalError(resp, "Couldn't place your order");
            return;
        }

        shoppingCart.emptyShoppingCart(supplierIdFromReqParam);

        resp.setStatus(HttpServletResponse.SC_OK);
    }


    private boolean checkProductIsOnSale(List<SupplierProduct> productsOnSale, Integer p) {

        for(SupplierProduct productOnSale : productsOnSale)
            if (productOnSale.getProductId().equals(p))
                return true;

        return false;
    }

    private SupplierProduct getProdFromSuppl(List<SupplierProduct> productsOnSale, Integer p) throws ServiceException {

        SupplierService supplierService = new SupplierService();
        supplierService.setSupplierToProducts(productsOnSale);

        for(SupplierProduct productOnSale : productsOnSale) {
            if (productOnSale.getProductId().equals(p)) {
                return productOnSale;
            }
        }

        //should never be reached
        return new SupplierProduct();
    }

    private void invalidRequest(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().println(message);
    }

    private void internalError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().println(message);
    }

}
