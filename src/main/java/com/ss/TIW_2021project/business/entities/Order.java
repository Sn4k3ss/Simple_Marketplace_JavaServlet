package com.ss.TIW_2021project.business.entities;

import com.ss.TIW_2021project.business.entities.product.ShoppingCartProduct;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private Integer orderId = null;
    private User user = null;
    private LocalDateTime orderPlacementDate = null;
    private Supplier orderSupplier = null;
    private List<ShoppingCartProduct> orderProductsList = null;
    private Float orderAmount = null;
    private Float orderShippingFees = null;
    private LocalDate deliveryDate = null;
    private ShippingAddress shippingAddress = null;

    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOrderPlacementDate() {
        return orderPlacementDate;
    }
    public void setOrderPlacementDate(LocalDateTime orderPlacementDate) {
        this.orderPlacementDate = orderPlacementDate;
    }

    public Supplier getOrderSupplier() {
        return orderSupplier;
    }
    public void setOrderSupplier(Supplier orderSupplier) {
        this.orderSupplier = orderSupplier;
    }

    public List<ShoppingCartProduct> getOrderProductsList() {
        return orderProductsList;
    }
    public void setOrderProductsList(List<ShoppingCartProduct> orderProductsList) {
        this.orderProductsList = orderProductsList;
    }

    public Float getOrderAmount() {
        return orderAmount;
    }
    public void setOrderAmount(Float orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Float getOrderShippingFees() {
        return orderShippingFees;
    }
    public void setOrderShippingFees(Float orderShippingFees) {
        this.orderShippingFees = orderShippingFees;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }
    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
