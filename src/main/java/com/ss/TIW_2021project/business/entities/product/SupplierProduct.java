package com.ss.TIW_2021project.business.entities.product;

import com.ss.TIW_2021project.business.entities.supplier.Supplier;

public class SupplierProduct extends Product {

    private Integer supplierId = null;
    private String supplierName = null;
    private Supplier supplier = null;
    private Float supplierProductCost = null;
    private Integer isAvailable = null;
    private boolean onDiscount = false;
    private Float originalProductCost = null;


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

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Float getSupplierProductCost() {
        return supplierProductCost;
    }

    public void setSupplierProductCost(Float supplierProductCost) {
        this.supplierProductCost = supplierProductCost;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isOnDiscount() {
        return onDiscount;
    }

    public void setOnDiscount(boolean onDiscount) {
        this.onDiscount = onDiscount;
    }

    public Float getOriginalProductCost() {
        return originalProductCost;
    }

    public void setOriginalProductCost(Float originalProductCost) {
        this.originalProductCost = originalProductCost;
    }
}
