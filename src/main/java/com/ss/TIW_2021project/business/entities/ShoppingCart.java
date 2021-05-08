package com.ss.TIW_2021project.business.entities;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    //TODO all'interno dello shoppingCart si potrebbero utilizzare degli indici per riferirsi ai vari prodotti inseriti
    //In questa lista i prodotti hanno solo:    -productId
    //                                          -supplierId
    //                                          -supplierProductCost



    //A simple MultiMap with supplierId as key mapped to multiple products
    private Multimap<Integer, SupplierProduct> shoppingCartList = null;




    /**
     * Instantiates a new Products catalogue.
     *
     * @param productsList a {@link List<SupplierProduct> productsList from wich the catalogue wee'll be generated}
     */
    public ShoppingCart(List<SupplierProduct> productsList) {
        shoppingCartList = ArrayListMultimap.create();

        for (SupplierProduct supProd : productsList) {
            shoppingCartList.put(supProd.getSupplierId(), supProd);
        }
    }

    public void addProductToCart(SupplierProduct supplierProduct) {
        shoppingCartList.put(supplierProduct.getSupplierId(), supplierProduct);
    }


    public Multimap<Integer, SupplierProduct> getShoppingCartList() {
        return shoppingCartList;
    }

}
