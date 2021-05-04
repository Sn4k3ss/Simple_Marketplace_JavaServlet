package com.ss.TIW_2021project.business.entities.supplier;

import com.ss.TIW_2021project.business.entities.Product;

public class SupplierProduct extends Product {

    private Integer supplierId = null;
    private String supplierName = null;
    private Float price = null;
    private Integer isAvailable = null;
    private boolean discounted = false;


    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

}
