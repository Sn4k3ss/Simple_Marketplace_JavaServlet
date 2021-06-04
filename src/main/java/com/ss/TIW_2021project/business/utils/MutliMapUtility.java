package com.ss.TIW_2021project.business.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MutliMapUtility {

    public static <T> void addToList(Map<Integer,List<T>> map, Integer mapKey, T prod) {
        List<T> list = map.get(mapKey);

        // if list does not exist create it
        if(list == null) {
            list = new ArrayList<>();
            list.add(prod);
            map.put(mapKey, list);
        } else {
            // add if item is not already in list
            if(!list.contains(prod))
                list.add(prod);
        }
    }

    private MutliMapUtility() {

    }

}
