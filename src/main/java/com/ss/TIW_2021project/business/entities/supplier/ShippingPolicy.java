package com.ss.TIW_2021project.business.entities.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShippingPolicy {

    private List<ItemRangeCost> ranges = null;


    public List<ItemRangeCost> getRanges() {
        return ranges;
    }

    public void setRanges(List<ItemRangeCost> ranges) {
        this.ranges = new ArrayList<>(List.copyOf(ranges));
    }
}
