package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    //TODO all'interno dello shoppingCart si potrebbero utilizzare degli indici per riferirsi ai vari prodotti inseriti
    //In questa lista i prodotti hanno solo:    -productId
    //                                          -supplierId
    //                                          -supplierProductCost

    private List<SupplierProduct> shoppingCartList = null;

    public ShoppingCart() {
        this.shoppingCartList = new ArrayList<>();
    }

    public void addProduct(SupplierProduct supplierProduct) {
        this.getShoppingCartList().add(supplierProduct);
    }

    public void removeProduct(SupplierProduct productToRemove) {
        this.getShoppingCartList().removeIf(productInCart -> productInCart.getProductId() == productToRemove.getProductId()
                && productInCart.getSupplierId() == productToRemove.getSupplierId()
                && productInCart.getSupplierProductCost() == productInCart.getSupplierProductCost());

    }

    public List<SupplierProduct> getShoppingCartList() {
        return shoppingCartList;
    }
}
