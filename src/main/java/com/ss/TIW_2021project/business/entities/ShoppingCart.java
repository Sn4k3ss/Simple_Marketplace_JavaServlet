package com.ss.TIW_2021project.business.entities;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShoppingCart {

    //TODO all'interno dello shoppingCart si potrebbero utilizzare degli indici per riferirsi ai vari prodotti inseriti
    //In questa lista i prodotti hanno solo:    -productId
    //                                          -supplierId
    //                                          -supplierProductCost



    //A simple MultiMap with supplierId as key mapped to multiple products
    private Multimap<Integer, SupplierProduct> shoppingCartList = null;
    private Multimap<Integer, SupplierProduct> sortedBySupplierCart = null;



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

    /**
     * Sort the shopping cart by supplierId
     *
     * @return a multimap sorted by key mapped to supplierId (from 1 to maxSupplierId)
     */
    public Multimap<Integer, SupplierProduct> sortShoppingCart() {

        List<SupplierProduct> supplierProducts = new ArrayList<>();

        for(Integer supplierId: shoppingCartList.keySet())
            supplierProducts.addAll(shoppingCartList.get(supplierId));


        Function<SupplierProduct, Integer> productIdFunc = SupplierProduct::getSupplierId;
        sortedBySupplierCart = Multimaps.index(supplierProducts.listIterator(), productIdFunc);
        return sortedBySupplierCart;
    }

    public Multimap<Integer, SupplierProduct> getSortedBySupplierCart() {
        return sortedBySupplierCart;
    }
}
