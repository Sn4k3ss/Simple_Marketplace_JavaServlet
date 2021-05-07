package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.List;
import java.util.Map;

public class ProductsCatalogue {

    private List<SupplierProduct> supplierProductList = null;

    private Map<SupplierProduct, Supplier> productsSupplierMap = null;

    public ProductsCatalogue(List<SupplierProduct> productsList) {


    }


    public List<SupplierProduct> getSupplierProductList() {
        return supplierProductList;
    }

    public void setSupplierProductList(List<SupplierProduct> supplierProductList) {
        this.supplierProductList = supplierProductList;
    }
}
