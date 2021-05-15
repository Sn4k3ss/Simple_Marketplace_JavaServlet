package com.ss.TIW_2021project.business.entities;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It is a catalogue with some utility functions
 */
public class ProductsCatalogue {

    //A simple MultiMap with productId as key mapped to multiple supplier
    private Multimap<Integer, SupplierProduct> supplierProductMultiMap = null;
    //A multiMap where the first value from key is the product that cost less
    private Multimap<Integer, SupplierProduct> sortedByMinPrice = null;


    /**
     * Instantiates a new Products catalogue.
     * !!!Sorted by productId!!!
     *
     * @param productsList a {@link List<SupplierProduct> productsList from which the catalogue will be generated}
     */
    public ProductsCatalogue(List<SupplierProduct> productsList) {
        Function<SupplierProduct, Integer> productIdFunc = SupplierProduct::getProductId;
        supplierProductMultiMap = Multimaps.index(productsList.listIterator(), productIdFunc);
    }

    /**
     * Gets supplier product multi map.
     *
     * @return the supplier product multi map
     */
    public Multimap<Integer, SupplierProduct> getSupplierProductMultiMap() {
        return supplierProductMultiMap;
    }

    /**
     * Sets supplier product multi map.
     *
     * @param supplierProductMultiMap the supplier product multi map
     */
    public void setSupplierProductMultiMap(Multimap<Integer, SupplierProduct> supplierProductMultiMap) {
        this.supplierProductMultiMap = supplierProductMultiMap;
    }


    /**
     * This method checks if there are at least the given number of unique products.
     * NB Still every product can have multiple supplier
     *
     * @param num the minimum number of products required
     * @return true if the unique products are at least <code>num</code>
     */
    public boolean containsAtLeast(int num) {

        int productsInCatalogue = supplierProductMultiMap.asMap().size();

        return productsInCatalogue >= num;
    }


    /**
     *
     *
     */
    public void sortByPrice() {
        List<SupplierProduct> products = new ArrayList<>();

        for (Integer prodId : supplierProductMultiMap.keySet())
            products.addAll(supplierProductMultiMap.get(prodId));

        Multimap<Integer, SupplierProduct> sorted = ArrayListMultimap.create();


    }

}
