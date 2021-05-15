package com.ss.TIW_2021project.business.entities;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    //A simple MultiMap with supplierId as key mapped to multiple products
    private Multimap<Integer, ShoppingCartProduct> shoppingCartList = null;
    private Multimap<Integer, ShoppingCartProduct> sortedBySupplierCart = null;

    private Map<Integer, Float> totalAmountBySupplier = null;



    public ShoppingCart(){
    }

    /**
     * Instantiates a new Products catalogue.
     *
     * @param productsList a {@link List<SupplierProduct> productsList from which the catalogue will be generated}
     */
    public ShoppingCart(List<ShoppingCartProduct> productsList) {
        this.shoppingCartList = ArrayListMultimap.create();

        for (ShoppingCartProduct supProd : productsList) {
            shoppingCartList.put(supProd.getSupplierId(), supProd);
        }

        this.totalAmountBySupplier = new HashMap<>();
    }

    public void addProductToCart(SupplierProduct supplierProduct, Integer howMany) {

        //This checks that a product from a specific supplier isn't alreay in the shopping cart
        boolean alreadyInShoppingCart = false;

        List<ShoppingCartProduct> shoppingCartProducts = new ArrayList<>(shoppingCartList.get(supplierProduct.getSupplierId()));

        for(ShoppingCartProduct prod : shoppingCartProducts){
            if (prod.getProductId().equals(supplierProduct.getProductId())) {
                alreadyInShoppingCart = true;
                int tmp = prod.getHowMany();
                tmp+=howMany;
                prod.setHowMany(tmp);
                prod.setTotalAmount((float)tmp * prod.getSupplierProductCost());
            }
        }

        if (!alreadyInShoppingCart) {
            ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct(supplierProduct, howMany);
            shoppingCartList.put(shoppingCartProduct.getSupplierId(), shoppingCartProduct);
        }

        //update the total of the current supplier
        Float totalAmoount = 0f;
        Integer supplierId = supplierProduct.getSupplierId();
        for (ShoppingCartProduct prod : shoppingCartList.get(supplierId))
            totalAmoount+= prod.getTotalAmount();
        totalAmountBySupplier.put(supplierId, totalAmoount);

    }


    public Multimap<Integer, ShoppingCartProduct> getShoppingCartList() {
        return shoppingCartList;
    }

    /**
     * Sort the shopping cart by supplierId
     *
     * @return a multimap sorted by key mapped to supplierId (from 1 to maxSupplierId)
     */
    public Multimap<Integer, ShoppingCartProduct> sortShoppingCart() {

        List<ShoppingCartProduct> supplierProducts = new ArrayList<>();

        for(Integer supplierId: shoppingCartList.keySet())
            supplierProducts.addAll(shoppingCartList.get(supplierId));


        Function<ShoppingCartProduct, Integer> productIdFunc = ShoppingCartProduct::getSupplierId;
        sortedBySupplierCart = Multimaps.index(supplierProducts.listIterator(), productIdFunc);
        return sortedBySupplierCart;
    }

    public Multimap<Integer, ShoppingCartProduct> getSortedBySupplierCart() {
        return sortedBySupplierCart;
    }

    public Map<Integer, Float> getTotalAmountBySupplier() {
        return totalAmountBySupplier;
    }

    public Float getTotalAmountBySupplier(Integer supplierId) {
        return totalAmountBySupplier.get(supplierId);
    }

    public void setTotalAmountBySupplier(Map<Integer, Float> totalAmountBySupplier) {
        this.totalAmountBySupplier = totalAmountBySupplier;
    }

    public List<ShoppingCartProduct> getProductsFromSupplier(Integer supplierId) {

        List<ShoppingCartProduct> productsLists = new ArrayList<>(shoppingCartList.get(supplierId));

        return productsLists;

    }

    /**
     * This method remove all the products by the {@link Supplier supplier} with the same supplierId passed as parameter
     *
     * @param supplierId the supplierId which all the products need to be removed
     */
    public void emptyShoppingCart(Integer supplierId) {
        this.shoppingCartList.removeAll(supplierId);
        this.totalAmountBySupplier.remove(supplierId);
    }
}
