package com.ss.TIW_2021project.business.entities.supplier;

public class ItemRangeCost {

    private Integer minAmount = null;
    private Integer maxAmount = null;
    private Float cost = null;

    public Integer getMinAmount() {
        return minAmount;
    }
    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }
    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Float getCost() {
        return cost;
    }
    public void setCost(Float cost) {
        this.cost = cost;
    }
}
