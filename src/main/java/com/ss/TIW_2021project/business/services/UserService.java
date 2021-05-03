package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.User;

public class UserService {










    public void createUserTest() {

        User user1 = new User();

        user1.setName("Simone");
        user1.setSurname("Sangeniti");
        user1.setEmail("sangeniti.ss@gmail.com");
        user1.setPassword("prova1234");

        ShippingAddress sa = new ShippingAddress();
        sa.setRecipient("Simone Sangeniti");
        sa.setAddress("Via Custoza 13");
        sa.setCity("Sesto San Giovanni 20099 MI");
        sa.setState("Italy");
        sa.setPhone("3277479411");

        user1.setShippingAddress(sa);




    }

}
