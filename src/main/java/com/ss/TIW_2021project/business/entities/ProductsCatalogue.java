package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.List;

public class ProductsCatalogue {

    private List<SupplierProduct> supplierProductList = null;


    public List<SupplierProduct> getSupplierProductList() {
        return supplierProductList;
    }

    public void setSupplierProductList(List<SupplierProduct> supplierProductList) {
        this.supplierProductList = supplierProductList;
    }
}
