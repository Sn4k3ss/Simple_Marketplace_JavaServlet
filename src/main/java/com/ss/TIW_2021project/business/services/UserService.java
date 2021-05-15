package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.UsersDAO;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.User;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private ServletContext servletContext;

    public UserService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Check credentials user.
     *
     * @param email    the email
     * @param password the password
     * @return the user if credentials are correct, null otherwise
     * @throws UnavailableException if can't get connection to db
     * @throws SQLException         mySQL exception
     */
    public User checkCredentials(String email, String password) throws UnavailableException, SQLException {

        UsersDAO usersDao = new UsersDAO(servletContext);
        User userRetrieved = usersDao.getUser(email, password);

        if (userRetrieved != null) {

            if (email.equals(userRetrieved.getEmail()) && password.equals(userRetrieved.getPassword()))
                return userRetrieved;
            else
                return null;
        }

        return null;
    }


    public List<User> getAllUsers() throws UnavailableException, SQLException {

        UsersDAO usersDao = new UsersDAO(servletContext);
        return usersDao.getAllUsers();

    }

    public List<ShippingAddress> getShippingAddresses(Integer userId) throws UnavailableException {

        UsersDAO usersDAO = new UsersDAO(servletContext);

        try {
            return usersDAO.getShippingAddresses(userId);
        } catch (SQLException ex) {
            throw new UnavailableException("Error while interacting with the database");
        }
    }

    public ShippingAddress getShippingAddress(Integer userId, Integer userShippingAddressId) throws UnavailableException {
        UsersDAO usersDAO = new UsersDAO(servletContext);
        List<ShippingAddress> shippingAddressList = new ArrayList<>();
        try {
            shippingAddressList = usersDAO.getShippingAddresses(userId);
        } catch (SQLException ex) {
            throw new UnavailableException("Error while interacting with the database");
        }

        for (ShippingAddress address: shippingAddressList) {
            if (address.getShippingAddressId().equals(userShippingAddressId))
                return address;
        }

        return null;
    }

    /**
     * THIS METHOD REQUIRES THAT ALL THE ORDERS IN THE LIST ARE FROM THE SAME USER
     *
     * @param orders
     *
     */


    public void setUserInfoOnOrder(List<Order> orders) throws UnavailableException {
        UsersDAO usersDAO = new UsersDAO(servletContext);
        User user;
        try {
            user = usersDAO.getUserById(orders.get(0).getUser().getUserId());
        } catch (SQLException exception) {
            throw new UnavailableException("Error while getting info about user");
        }

        for(Order order : orders)
            order.setUser(user);

    }
}
