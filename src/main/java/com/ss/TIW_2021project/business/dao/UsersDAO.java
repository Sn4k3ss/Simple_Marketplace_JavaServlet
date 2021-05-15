package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UsersDAO {

    private Connection connection;

    public UsersDAO(ServletContext servletContext) throws UnavailableException {
        connection = ConnectionFactory.getConnection(servletContext);
    }


    public User getUserById(Integer userId) throws SQLException, UnavailableException {

        User userRetrieved = null;

        String query = "SELECT * FROM users WHERE userId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);

            try (ResultSet result = preparedStatement.executeQuery();) {

                while (result.next()) {
                    userRetrieved = new User();
                    userRetrieved.setUserId(result.getInt("userId"));
                    userRetrieved.setEmail(result.getString("email"));
                    userRetrieved.setPassword(result.getString("password"));
                    userRetrieved.setUserName(result.getString("userName"));
                    userRetrieved.setUserSurname(result.getString("userSurname"));

                }
            }
        }

        return userRetrieved;
    }


    public User getUser(String email, String password) throws SQLException, UnavailableException {

        User userRetrieved = null;

        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet result = preparedStatement.executeQuery();) {

                while (result.next()) {
                    userRetrieved = new User();
                    userRetrieved.setUserId(result.getInt("userId"));
                    userRetrieved.setEmail(result.getString("email"));
                    userRetrieved.setPassword(result.getString("password"));
                    userRetrieved.setUserName(result.getString("userName"));
                    userRetrieved.setUserSurname(result.getString("userSurname"));

                    //TODO shipping address to be got here is a possibility

                }
            }
        } finally {
        try {
            ConnectionFactory.closeConnection(this.connection);
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't close the connection");
        }
    }


        return userRetrieved;
    }

    public List<User> getAllUsers() throws UnavailableException, SQLException {

        List<User> userList = new ArrayList<>();

        String query = "SELECT * FROM users ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {

            try (ResultSet result = preparedStatement.executeQuery();) {
                User user = null;
                while (result.next()) {
                    user = new User();
                    user.setUserId(result.getInt("userId"));
                    user.setEmail(result.getString("email"));
                    user.setPassword(result.getString("password"));
                    user.setUserName(result.getString("userName"));
                    user.setUserSurname(result.getString("userSurname"));
                    userList.add(user);
                }
            }
        }  finally {
            try {
                ConnectionFactory.closeConnection(this.connection);
            } catch (SQLException e) {
                throw new UnavailableException("Couldn't close the connection");
            }
        }

        return userList;
    }

    /**
     *  This method builds a {@link List<ShippingAddress> shippingAddresses list} by
     *  executing a query with the userId passed as parameter
     *
     * @param userId the userId which shoppingAddresses are going to be retrieved from the db
     * @return a list that contains all the user's shipping addresses, or an empty collection if none is present
     * @throws SQLException if an error occurred while interacting with the db
     */
    public List<ShippingAddress> getShippingAddresses(Integer userId) throws SQLException {

        List<ShippingAddress> userShippingAddresses = new ArrayList<>();

        String query = "" +
                "SELECT sA.userId, userAddressId, recipient, address, city, state, phone " +
                "FROM users as user " +
                "   JOIN shippingAddresses sA on user.userId = sA.userId " +
                "WHERE user.userId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                userShippingAddresses = buildUserAddresses(resultSet);
            } catch (SQLException ex) {
                //Error on executing query
                throw ex;
            }
        } catch (SQLException ex) {
            //error on prepareStatement
            throw ex;
        }

        return userShippingAddresses;

    }


    /**
     * This method builds a {@link List<ShippingAddress> shippingAddresses list}
     *
     * @param resultSet resultSet
     * @return a list that contains all the shipping addresses present in the {@link ResultSet resultSet}, or
     *          an empty collection if {@link ResultSet#next()} is false
     * @throws SQLException if an error occurred while interacting with the db
     */
    private List<ShippingAddress> buildUserAddresses(ResultSet resultSet) throws SQLException {

        List<ShippingAddress> userShippingAddresses = new ArrayList<>();
        ShippingAddress tmp = new ShippingAddress();


        while (resultSet.next()) {
            tmp = new ShippingAddress();
            tmp.setUserId(resultSet.getInt("userId"));
            tmp.setShippingAddressId(resultSet.getInt("userAddressId"));
            tmp.setRecipient(resultSet.getString("recipient"));
            tmp.setAddress(resultSet.getString("address"));
            tmp.setCity(resultSet.getString("city"));
            tmp.setState(resultSet.getString("state"));
            tmp.setPhone(resultSet.getString("phone"));

            userShippingAddresses.add(tmp);
        }

        return userShippingAddresses;
    }


}

