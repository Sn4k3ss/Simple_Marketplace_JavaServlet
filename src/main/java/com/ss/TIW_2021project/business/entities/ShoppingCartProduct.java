package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

public class ShoppingCartProduct extends SupplierProduct {

    private Integer howMany = null;
    private Float totalAmount = null;

    public ShoppingCartProduct(){
        super();
    }

    public ShoppingCartProduct(SupplierProduct supplierProduct, Integer howMany) {
        this.setProductId(supplierProduct.getProductId());
        this.setProductName(supplierProduct.getProductName());
        this.setProductDescription(supplierProduct.getProductDescription());
        this.setProductCategory(supplierProduct.getProductCategory());
        this.setProductCategory(supplierProduct.getProductCategory());
        this.setProductCategoryId(supplierProduct.getProductCategoryId());
        this.setProductImagePath(supplierProduct.getProductImagePath());
        this.setSupplierId(supplierProduct.getSupplierId());
        this.setSupplierName(supplierProduct.getSupplierName());
        this.setSupplier(supplierProduct.getSupplier());
        this.setSupplierProductCost(supplierProduct.getSupplierProductCost());
        this.setIsAvailable(supplierProduct.getIsAvailable());
        this.setOnDiscount(supplierProduct.isOnDiscount());
        this.setOriginalProductCost(supplierProduct.getOriginalProductCost());
        this.howMany = howMany;
        this.totalAmount = (float) howMany * this.getSupplierProductCost();
    }

    public Integer getHowMany() {
        return howMany;
    }
    public void setHowMany(Integer howMany) {
        this.howMany = howMany;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }
}
