package com.ss.TIW_2021project.business.entities;

public class ShippingAddress {

    private Integer userId = null;
    private Integer shippingAddressId = null;
    private String recipient = null;
    private String address = null;
    private String city = null;
    private String state = null;
    private String phone = null;

    public ShippingAddress() {
        super();
    }

    public ShippingAddress(Integer userShippingAddress) {
        this.shippingAddressId = userShippingAddress;
    }


    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getShippingAddressId() {
        return shippingAddressId;
    }
    public void setShippingAddressId(Integer shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
