package com.ss.TIW_2021project.business.entities;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.List;

public class ProductsCatalogue {

    //use the productId as key mapped to multiple supplier
    private Multimap<Integer, SupplierProduct> supplierProductMultiMap = null;

    public ProductsCatalogue(List<SupplierProduct> productsList) {

        supplierProductMultiMap = ArrayListMultimap.create();

        for (SupplierProduct supProd : productsList) {
            supplierProductMultiMap.put(supProd.getProductId(), supProd);
        }

    }

    public Multimap<Integer, SupplierProduct> getSupplierProductMultiMap() {
        return supplierProductMultiMap;
    }

    public void setSupplierProductMultiMap(Multimap<Integer, SupplierProduct> supplierProductMultiMap) {
        this.supplierProductMultiMap = supplierProductMultiMap;
    }
}
