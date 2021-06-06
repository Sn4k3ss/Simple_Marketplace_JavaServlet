package com.ss.TIW_2021project.business.entities.supplier;

public class Supplier {

    private Integer supplierId = null;
    private String supplierName = null;
    private Float supplierRating = null;
    private ShippingPolicy supplierShippingPolicy = null;
    private boolean hasFreeShipping = false;
    private Float freeShippingMinAmount = null;
    private String imagePath = null;

    public Supplier(){
        super();
    }

    public Supplier(Integer supplierId) {
        this.supplierId = supplierId;
    }

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

    public Float getSupplierRating() {
        return supplierRating;
    }
    public void setSupplierRating(Float supplierRating) {
        this.supplierRating = supplierRating;
    }

    public ShippingPolicy getSupplierShippingPolicy() {
        return supplierShippingPolicy;
    }
    public void setSupplierShippingPolicy(ShippingPolicy supplierShippingPolicy) {
        this.supplierShippingPolicy = supplierShippingPolicy;
    }

    public boolean isHasFreeShipping() {
        return hasFreeShipping;
    }
    public void setHasFreeShipping(boolean hasFreeShipping) {
        this.hasFreeShipping = hasFreeShipping;
    }

    public Float getFreeShippingMinAmount() {
        return freeShippingMinAmount;
    }
    public void setFreeShippingMinAmount(Float freeShippingMinAmount) {
        this.freeShippingMinAmount = freeShippingMinAmount;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
