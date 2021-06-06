package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.product.ShoppingCartProduct;
import com.ss.TIW_2021project.business.entities.product.SupplierProduct;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.utils.MutliMapUtility;

import java.util.*;

public class ShoppingCart {

    //A simple Map with supplierId as key mapped to multiple products
    private Map<Integer, List<ShoppingCartProduct>> shoppingCartMap = null;

    private Map<Integer, Float> totalAmountBySupplier = null;
    private Map<Integer, Integer> totalItemsBySupplier = null;

    public ShoppingCart() {  }

    /**
     * Instantiates a new Products catalogue.
     *
     * @param productsList a {@link List< SupplierProduct > productsList from which the catalogue will be generated}
     */
    public ShoppingCart(List<ShoppingCartProduct> productsList) {

        this.shoppingCartMap = new HashMap<>();

        for (ShoppingCartProduct prod : productsList) {
            MutliMapUtility.addToList(shoppingCartMap, prod.getSupplierId(), prod);
        }

        this.totalAmountBySupplier = new HashMap<>();
        this.totalItemsBySupplier = new HashMap<>();
    }

    public void addProductToCart(SupplierProduct supplierProduct, Integer howMany) {

        boolean alreadyInShoppingCart = false;

        if (shoppingCartMap.get(supplierProduct.getSupplierId()) != null) {
            List<ShoppingCartProduct> supProds = new ArrayList<>(shoppingCartMap.get(supplierProduct.getSupplierId()));
            for (ShoppingCartProduct prod : supProds) {
                if (prod.getProductId().equals(supplierProduct.getProductId())) {
                    alreadyInShoppingCart = true;
                    int tmp = prod.getHowMany();
                    tmp+=howMany;
                    prod.setHowMany(tmp);
                    prod.setTotalAmount((float)tmp * prod.getSupplierProductCost());
                }
            }
        }


        if (!alreadyInShoppingCart) {
            ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct(supplierProduct, howMany);
            MutliMapUtility.addToList(shoppingCartMap, shoppingCartProduct.getSupplierId(), shoppingCartProduct);
        }





        //update the total of the current supplier
        Float totalAmount = 0f;
        int totalItems = 0;
        Integer supplierId = supplierProduct.getSupplierId();
        for (ShoppingCartProduct prod : shoppingCartMap.get(supplierId)) {
            totalAmount += prod.getTotalAmount();
            totalItems+= prod.getHowMany();
        }
        totalAmountBySupplier.put(supplierId, totalAmount);
        totalItemsBySupplier.put(supplierId, totalItems);
    }

    public Map<Integer, List<ShoppingCartProduct>> getShoppingCartMap() {
        return shoppingCartMap;
    }

    public Float getAmountBySupplier(Integer supplierId) {
        return totalAmountBySupplier.get(supplierId);
    }

    public void setTotalAmountBySupplier(Map<Integer, Float> totalAmountBySupplier) {
        this.totalAmountBySupplier = totalAmountBySupplier;
    }

    public Map<Integer, Float> getTotalAmountBySupplier() {
        return totalAmountBySupplier;
    }

    public List<ShoppingCartProduct> getProductsFromSupplier(Integer supplierId) {

        if (shoppingCartMap.get(supplierId) != null) {
            return new ArrayList<>(List.copyOf(shoppingCartMap.get(supplierId)));
        }
        return new ArrayList<>(Collections.emptyList());

    }

    /**
     * This method remove all the products by the {@link Supplier supplier} with the same supplierId passed as parameter
     *
     * @param supplierId the supplierId which all the products need to be removed
     */
    public void emptyShoppingCart(Integer supplierId) {
        this.shoppingCartMap.remove(supplierId);
        this.totalAmountBySupplier.remove(supplierId);
        this.totalItemsBySupplier.remove(supplierId);
    }

    public Map<Integer, Integer> getTotalItemsBySupplier() {
        return totalItemsBySupplier;
    }

    public void setTotalItemsBySupplier(Map<Integer, Integer> totalItemsBySupplier) {
        this.totalItemsBySupplier = totalItemsBySupplier;
    }
}
