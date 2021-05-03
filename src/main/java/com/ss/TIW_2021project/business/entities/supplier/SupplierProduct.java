package com.ss.TIW_2021project.business.entities.supplier;

import com.ss.TIW_2021project.business.entities.Product;

public class SupplierProduct extends Product {

    private Integer supplierId = null;
    private Float price = null;
    private Integer isAvailable= null;


    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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
