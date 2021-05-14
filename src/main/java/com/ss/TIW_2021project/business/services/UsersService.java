package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.UsersDAO;
import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.User;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.util.List;

public class UsersService {

    private ServletContext servletContext;

    public UsersService(ServletContext servletContext) {
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
}
