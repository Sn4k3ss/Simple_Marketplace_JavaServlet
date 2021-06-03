package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.http.HttpSession;
import java.util.Collections;

public class CartService {

    public CartService() {
    }

    public ShoppingCart getShoppingCart(HttpSession session) {

        ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");

        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart(Collections.emptyList());
            session.setAttribute("shoppingCart", shoppingCart);
        }

        return shoppingCart;
    }

    public ShoppingCart addToCart(HttpSession session, SupplierProduct supplierProduct, Integer howMany) {


        ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");

        if(shoppingCart == null) {
            shoppingCart = new ShoppingCart(Collections.emptyList());
            session.setAttribute("shoppingCart", shoppingCart);
        }

        shoppingCart.addProductToCart(supplierProduct, howMany);

        return shoppingCart;
    }
}
