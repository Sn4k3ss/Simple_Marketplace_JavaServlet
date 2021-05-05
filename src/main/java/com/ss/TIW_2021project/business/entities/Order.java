package com.ss.TIW_2021project.business.entities;

import java.util.Date;
import java.util.List;

public class Order {

    private Integer orderId = null;
    private String orderSupplier = null;
    private List<Product> orderProductsList = null;
    private Float orderAmount = null;
    private Float orderShippingCosts = null;
    private Date deliveryDate = null;
    private ShippingAddress shippingAddress = null;

    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderSupplier() {
        return orderSupplier;
    }
    public void setOrderSupplier(String orderSupplier) {
        this.orderSupplier = orderSupplier;
    }

    public List<Product> getOrderProductsList() {
        return orderProductsList;
    }
    public void setOrderProductsList(List<Product> orderProductsList) {
        this.orderProductsList = orderProductsList;
    }

    public Float getOrderAmount() {
        return orderAmount;
    }
    public void setOrderAmount(Float orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Float getOrderShippingCosts() {
        return orderShippingCosts;
    }
    public void setOrderShippingCosts(Float orderShippingCosts) {
        this.orderShippingCosts = orderShippingCosts;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }
    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
