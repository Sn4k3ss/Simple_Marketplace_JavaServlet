package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.ShoppingCartProduct;
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

/**
 * The type Users dao.
 */
public class UsersDAO {

    private Connection connection;

    /**
     * Instantiates a new Users dao.
     *
     * @param servletContext the servlet context
     * @throws UnavailableException the unavailable exception
     */
    public UsersDAO(ServletContext servletContext) throws UtilityException {
        connection = ConnectionFactory.getConnection(servletContext);
    }


    /**
     * Return the user with the corresponding id; NO PASSWORD INCLUDED
     *
     * @param userId the user id
     * @return the user
     * @throws SQLException         the sql exception
     * @throws UnavailableException the unavailable exception
     */
    public User getUserById(Integer userId) throws DAOException {

        User userRetrieved = null;

        String userQuery = "SELECT * FROM users WHERE userId = ?";

        String addressesQuery = "" +
                "SELECT sA.userId, userAddressId, recipient, address, city, state, phone " +
                "from users " +
                "    join shippingAddresses sA on users.userId = sA.userId " +
                "where sA.userId = ?";

        try (
                PreparedStatement userQueryStm = connection.prepareStatement(userQuery);
                PreparedStatement addressesQueryStm = connection.prepareStatement(addressesQuery)
        ) {
            userQueryStm.setInt(1, userId);
            addressesQueryStm.setInt(1, userId);
            try (
                    ResultSet rs = userQueryStm.executeQuery();
                    ResultSet shipAddrRes = addressesQueryStm.executeQuery();
            ) {
                while (rs.next()) {
                    userRetrieved = new User();
                    userRetrieved.setUserId(rs.getInt("userId"));
                    userRetrieved.setEmail(rs.getString("email"));
                    //userRetrieved.setPassword(result.getString("password"));
                    userRetrieved.setUserName(rs.getString("userName"));
                    userRetrieved.setUserSurname(rs.getString("userSurname"));
                }

                if (userRetrieved != null)
                    userRetrieved.setShippingAddresses(buildUserAddresses(shipAddrRes));


            } catch (SQLException exception) {
                //error while executing Query
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }

        } catch (SQLException exception) {
            //error while preparing the query
            throw new DAOException(DAOException._MALFORMED_QUERY);
        }

        return userRetrieved;
    }


    public boolean emailTaken(String email) throws DAOException {

        String query = "SELECT * FROM users WHERE email = ?";

        try ( PreparedStatement preparedStatement = connection.prepareStatement(query); ) {
            preparedStatement.setString(1, email);

            try ( ResultSet rs = preparedStatement.executeQuery(); ) {
                return rs.next();

            } catch (SQLException exception) {
                //error while executing Query
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            //error while preparing the query
            throw new DAOException(DAOException._MALFORMED_QUERY);
        }
    }

    public void registerUser(String email, String password, String firstName, String lastName,
                             String address, String addrCity, String addrState, String addrPhone) throws DAOException {

        int localUserId = -1;

        String recipient = firstName.concat(" ").concat(lastName);

        //codice non verificato
        String insertInUserTable = "INSERT INTO users (userName, userSurname, email, password) " +
                "VALUES(?, ?, ?, ?) ";

        String newUserId = "SELECT MAX(userId) lastUserId FROM users";

        String insertAddress = "INSERT INTO shippingAddresses (userId, userAddressId, recipient, address, city, state, phone)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (
                PreparedStatement userTableUpdate = connection.prepareStatement(insertInUserTable);
                PreparedStatement getUserId = connection.prepareStatement(newUserId);
                PreparedStatement userAddressUpdate = connection.prepareStatement(insertAddress);
        ) {

            //autocommit false
            connection.setAutoCommit(false);
            //inserisco l'utente nella tabella degli utenti
            userTableUpdate.setString(1, firstName);
            userTableUpdate.setString(2, lastName);
            userTableUpdate.setString(3, email);
            userTableUpdate.setString(4, password);
            userTableUpdate.executeUpdate();

            //prendo l'id dell'utente appena inserito
            ResultSet rs = getUserId.executeQuery();
            if (rs.next())
                localUserId = rs.getInt("lastUserId");



            userAddressUpdate.setInt(1, localUserId);
            userAddressUpdate.setInt(2, 1);
            userAddressUpdate.setString(3, recipient);
            userAddressUpdate.setString(4, address);
            userAddressUpdate.setString(5, addrCity);
            userAddressUpdate.setString(6, addrState);
            userAddressUpdate.setString(7, addrPhone);

            userAddressUpdate.executeUpdate();



            //committo i cambiamenti in entrambe le tabelle
            connection.commit();

        } catch (SQLException e) {
            //errore da loggare
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    //errore da loggare
                    //cannot rollback
                }
            }
            throw new DAOException(DAOException._FAIL_TO_INSERT);
        }
    }

    /**
     * Gets user.
     *
     * @param email    the email
     * @param password the password
     * @return the user or null if user not found
     * @throws SQLException         the sql exception
     * @throws UnavailableException the unavailable exception
     */
    public User getUser(String email, String password) throws DAOException {

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

                }
            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._MALFORMED_QUERY);
        }
        return userRetrieved;
    }

    /**
     * Gets all users.
     *
     * @return the all users
     * @throws UnavailableException the unavailable exception
     * @throws SQLException         the sql exception
     */
    public List<User> getAllUsers() throws DAOException {

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

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException | DAOException exception) {
            throw new DAOException(DAOException._MALFORMED_QUERY);
        }

        return userList;
    }

    /**
     * This method builds a {@link List<ShippingAddress> shippingAddresses list} by
     * executing a query with the userId passed as parameter
     *
     * @param userId the userId which shoppingAddresses are going to be retrieved from the db
     * @return a list that contains all the user's shipping addresses, or an empty collection if none is present
     * @throws SQLException if an error occurred while interacting with the db
     */
    public List<ShippingAddress> getShippingAddresses(Integer userId) throws DAOException {

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

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._MALFORMED_QUERY);
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

