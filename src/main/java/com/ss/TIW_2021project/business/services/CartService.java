package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.ProductsDAO;
import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartService {



    private ServletContext servletContext;

    public CartService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ShoppingCart getShoppingCart(HttpSession session) {
        ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");

        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            session.setAttribute("shoppingCart", shoppingCart);
        }

        return shoppingCart;
    }

    public ShoppingCart addToCart(HttpSession session, SupplierProduct supplierProduct) {


        ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");

        if(shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            session.setAttribute("shoppingCart", shoppingCart);
        }

        shoppingCart.addProduct(supplierProduct);

        return shoppingCart;
    }
}
