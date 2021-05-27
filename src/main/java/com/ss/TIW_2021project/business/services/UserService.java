package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.Exceptions.ServiceException;
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
    public User checkCredentials(String email, String password) throws ServiceException {

        UsersDAO usersDao;
        User userRetrieved;

        try {
            usersDao = new UsersDAO(servletContext);
            userRetrieved = usersDao.getUser(email, password);
            userRetrieved.setShippingAddresses(usersDao.getShippingAddresses(userRetrieved.getUserId()));
        } catch (UtilityException | DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_CHECK_CREDENTIALS);
        }

        if (userRetrieved != null) {
            if (email.equals(userRetrieved.getEmail()) && password.equals(userRetrieved.getPassword()))
                return userRetrieved;
            else
                return null;
        }

        return null;
    }


    public List<User> getAllUsers() throws ServiceException {

        try {
            UsersDAO usersDao = new UsersDAO(servletContext);
            return usersDao.getAllUsers();
        } catch ( UtilityException | DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_USERS_INFO);
        }
    }

    public List<ShippingAddress> getShippingAddresses(Integer userId) throws ServiceException {

        try {
            UsersDAO usersDAO = new UsersDAO(servletContext);
            return usersDAO.getShippingAddresses(userId);
        } catch (DAOException | UtilityException ex) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_USERS_INFO);
        }
    }

    public ShippingAddress getShippingAddress(Integer userId, Integer userShippingAddressId) throws ServiceException {

        List<ShippingAddress> shippingAddressList = new ArrayList<>();
        try {
            UsersDAO usersDAO = new UsersDAO(servletContext);
            shippingAddressList = usersDAO.getShippingAddresses(userId);
        } catch (UtilityException | DAOException ex) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_USERS_INFO);
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


    public void setUserInfoOnOrders(List<Order> orders) throws ServiceException {
        User user;

        try {
            UsersDAO usersDAO = new UsersDAO(servletContext);
            user = usersDAO.getUserById(orders.get(0).getUser().getUserId());
        } catch (DAOException | UtilityException exception) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_USERS_INFO);
        }

        for(Order order : orders) {
            int userShippingAddressId = order.getShippingAddress().getShippingAddressId();
            order.setUser(user);
            order.setShippingAddress(user.getShippingAddresses().get(userShippingAddressId -1));
        }

    }
}
