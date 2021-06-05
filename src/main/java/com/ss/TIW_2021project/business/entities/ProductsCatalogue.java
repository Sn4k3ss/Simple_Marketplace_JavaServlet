package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.product.SupplierProduct;
import com.ss.TIW_2021project.business.utils.MutliMapUtility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * It is a catalogue with some utility functions
 */
public class ProductsCatalogue {

    //A simple Map with productId as key mapped to multiple supplier
    private Map<Integer, List<SupplierProduct>> supplierProductMap = null;

    /**
     * Instantiates a new Products catalogue.
     * !!!Sorted by productId!!!
     *
     * @param productsList a {@link List<SupplierProduct> productsList from which the catalogue will be generated}
     */
    public ProductsCatalogue(List<SupplierProduct> productsList) {

        supplierProductMap = new LinkedHashMap<>();

        for (SupplierProduct prod : productsList) {
            MutliMapUtility.addToList(supplierProductMap, prod.getProductId(), prod);
        }

    }

    //removing guava
    public Map<Integer, List<SupplierProduct>> getSupplierProductMap() {
        return supplierProductMap;
    }

    public void setSupplierProductMap(Map<Integer, List<SupplierProduct>> supplierProductMap) {
        this.supplierProductMap = supplierProductMap;
    }
    //removing guava



    /**
     * This method checks if there are at least the given number of unique products.
     * NB Still every product can have multiple supplier
     *
     * @param num the minimum number of products required
     * @return true if the unique products are at least <code>num</code>
     */
    public boolean containsAtLeast(int num) {
        return supplierProductMap.size() >= num;
    }

}
