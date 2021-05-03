package com.ss.TIW_2021project.business.entities.supplier;

public class Supplier {

    private Integer id = null;
    private String name = null;
    private Float rating = null;
    private ShippingPolicy shippingPolicy= null;
    private Float freeShippingMinAmount = null;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Float getRating() {
        return rating;
    }
    public void setRating(Float rating) {
        this.rating = rating;
    }

    public ShippingPolicy getShippingPolicy() {
        return shippingPolicy;
    }
    public void setShippingPolicy(ShippingPolicy shippingPolicy) {
        this.shippingPolicy = shippingPolicy;
    }

    public Float getFreeShippingMinAmount() {
        return freeShippingMinAmount;
    }
    public void setFreeShippingMinAmount(Float freeShippingMinAmount) {
        this.freeShippingMinAmount = freeShippingMinAmount;
    }
}
