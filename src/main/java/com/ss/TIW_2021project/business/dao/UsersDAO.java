package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    private Connection connection;

    public UsersDAO(ServletContext servletContext) throws UnavailableException {
        connection = ConnectionFactory.getConnection(servletContext);
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
                    userRetrieved.setId(result.getInt("userId"));
                    userRetrieved.setEmail(result.getString("email"));
                    userRetrieved.setPassword(result.getString("password"));
                    userRetrieved.setName(result.getString("name"));
                    userRetrieved.setSurname(result.getString("surname"));

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
                    user.setId(result.getInt("idUser"));
                    user.setEmail(result.getString("email"));
                    user.setPassword(result.getString("password"));
                    user.setName(result.getString("name"));
                    user.setSurname(result.getString("surname"));
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
}

