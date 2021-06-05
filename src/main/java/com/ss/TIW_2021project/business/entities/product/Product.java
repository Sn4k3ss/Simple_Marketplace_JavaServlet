package com.ss.TIW_2021project.business.entities.product;


public class Product {

    private Integer productId = null;
    private String productName = null;
    private String productDescription = null;
    private String productCategory = null;
    private Integer productCategoryId = null;
    private String productImagePath = null;

    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }
    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductImagePath() {
        return productImagePath;
    }
    public void setProductImagePath(String productImagePath) {
        this.productImagePath = productImagePath;
    }
}
