package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.Date;
import java.util.List;

public class Order {

    private Integer id = null;
    private String supplier = null;
    private List<SupplierProduct> productsList = null;
    private Integer total = null;
    private Double shippingCost = null;
    private Date shippingDate = null;
    private ShippingAddress shippingAddress = null;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getSupplier() {
        return supplier;
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public List<SupplierProduct> getProductsList() {
        return productsList;
    }
    public void setProductsList(List<SupplierProduct> productsList) {
        this.productsList = productsList;
    }

    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getShippingCost() {
        return shippingCost;
    }
    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Date getShippingDate() {
        return shippingDate;
    }
    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }
    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
