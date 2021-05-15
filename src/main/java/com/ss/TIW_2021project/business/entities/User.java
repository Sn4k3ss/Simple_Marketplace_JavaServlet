package com.ss.TIW_2021project.business.entities;

import java.util.List;

public class User {

    private Integer userId = null;
    private String userName = null;
    private String userSurname = null;
    private String email = null;
    private String password = null;
    private List<ShippingAddress> shippingAddresses = null;

    public User() {}
    public User(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }
    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<ShippingAddress> getShippingAddresses() {
        return shippingAddresses;
    }
    public void setShippingAddresses(List<ShippingAddress> shippingAddresses) {
        this.shippingAddresses = shippingAddresses;
    }
}
