package com.ss.TIW_2021project.business.entities;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.List;

/**
 * It is a catalogue with some utility functions
 */
public class ProductsCatalogue {

    //A simple MultiMap with productId as key mapped to multiple supplier
    private Multimap<Integer, SupplierProduct> supplierProductMultiMap = null;


    /**
     * Instantiates a new Products catalogue.
     *
     * @param productsList a {@link List<SupplierProduct> productsList from wich the catalogue wee'll be generated}
     */
    public ProductsCatalogue(List<SupplierProduct> productsList) {
        supplierProductMultiMap = ArrayListMultimap.create();

        for (SupplierProduct supProd : productsList) {
            supplierProductMultiMap.put(supProd.getProductId(), supProd);
        }

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

    public boolean containsAtLeast(int num) {

        int productsInCatalogue = supplierProductMultiMap.asMap().size();

        return productsInCatalogue >= num;
    }
}
