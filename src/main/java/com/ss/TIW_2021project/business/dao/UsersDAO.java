package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.User;
import com.ss.TIW_2021project.business.utils.ConnectionHandler;

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

    private Connection conn;

    /**
     * Instantiates a new Users dao.
     *
     * @throws UnavailableException the unavailable exception
     */
    public UsersDAO() {
        super();
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

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        User userRetrieved = null;
        String userQuery = "SELECT * FROM users WHERE userId = ?";
        String addressesQuery = "" +
                "SELECT sA.userId, userAddressId, recipient, address, city, state, phone " +
                "from users " +
                "    join shippingAddresses sA on users.userId = sA.userId " +
                "where sA.userId = ?";

        PreparedStatement userQueryStm = null;
        PreparedStatement addressesQueryStm = null;
        ResultSet userRs = null;
        ResultSet addrRs = null;

        try {
            userQueryStm = conn.prepareStatement(userQuery);
            addressesQueryStm = conn.prepareStatement(addressesQuery);

            userQueryStm.setInt(1, userId);
            addressesQueryStm.setInt(1, userId);
            try {
                userRs = userQueryStm.executeQuery();
                addrRs = addressesQueryStm.executeQuery();

                while (userRs.next()) {
                    userRetrieved = new User();
                    userRetrieved.setUserId(userRs.getInt("userId"));
                    userRetrieved.setEmail(userRs.getString("email"));
                    //userRetrieved.setPassword(result.getString("password"));
                    userRetrieved.setUserName(userRs.getString("userName"));
                    userRetrieved.setUserSurname(userRs.getString("userSurname"));
                }

                if (userRetrieved != null)
                    userRetrieved.setShippingAddresses(buildUserAddresses(addrRs));


            } catch (SQLException exception) {
                //error while executing Query
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }

        } catch (SQLException exception) {
            //error while preparing the query
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(userRs);
                ConnectionHandler.closeQuietly(addrRs);
                ConnectionHandler.closeQuietly(userQueryStm);
                ConnectionHandler.closeQuietly(addressesQueryStm);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }

        return userRetrieved;
    }


    public boolean emailTaken(String email) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        String query = "SELECT * FROM users WHERE email = ?";

        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);

            try {
                rs = preparedStatement.executeQuery();
                return rs.next();

            } catch (SQLException exception) {
                //error while executing Query
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            //error while preparing the query
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(preparedStatement);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }
    }

    public void registerUser(String email, String password, String firstName, String lastName,
                             String address, String addrCity, String addrState, String addrPhone) throws DAOException {


        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        int localUserId = -1;

        String recipient = firstName.concat(" ").concat(lastName);

        //codice non verificato
        String insertInUserTable = "INSERT INTO users (userName, userSurname, email, password) " +
                "VALUES(?, ?, ?, ?) ";

        String newUserId = "SELECT MAX(userId) lastUserId FROM users";

        String insertAddress = "INSERT INTO shippingAddresses (userId, userAddressId, recipient, address, city, state, phone)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement userTableUpdate = null;
        PreparedStatement getUserId = null;
        PreparedStatement userAddressUpdate = null;

        ResultSet rs = null;

        try {
            userTableUpdate = conn.prepareStatement(insertInUserTable);
            getUserId = conn.prepareStatement(newUserId);
            userAddressUpdate = conn.prepareStatement(insertAddress);

            //autocommit false
            conn.setAutoCommit(false);
            //inserisco l'utente nella tabella degli utenti
            userTableUpdate.setString(1, firstName);
            userTableUpdate.setString(2, lastName);
            userTableUpdate.setString(3, email);
            userTableUpdate.setString(4, password);
            userTableUpdate.executeUpdate();

            //prendo l'id dell'utente appena inserito
            rs = getUserId.executeQuery();
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
            conn.commit();

        } catch (SQLException e) {
            //errore da loggare
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    //errore da loggare
                    //cannot rollback
                }
            }
            throw new DAOException(DAOException._FAIL_TO_INSERT);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(userTableUpdate);
                ConnectionHandler.closeQuietly(getUserId);
                ConnectionHandler.closeQuietly(userAddressUpdate);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
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

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }


        User userRetrieved = null;

        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try {
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    userRetrieved = new User();
                    userRetrieved.setUserId(rs.getInt("userId"));
                    userRetrieved.setEmail(rs.getString("email"));
                    userRetrieved.setPassword(rs.getString("password"));
                    userRetrieved.setUserName(rs.getString("userName"));
                    userRetrieved.setUserSurname(rs.getString("userSurname"));

                }
            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(preparedStatement);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
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

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        List<User> userList = new ArrayList<>();

        String query = "SELECT * FROM users ";

        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            preparedStatement = conn.prepareStatement(query);

            try {
                rs = preparedStatement.executeQuery();
                User user = null;
                while (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("userId"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setUserName(rs.getString("userName"));
                    user.setUserSurname(rs.getString("userSurname"));
                    userList.add(user);
                }

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException | DAOException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(preparedStatement);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
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

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        List<ShippingAddress> userShippingAddresses = new ArrayList<>();

        String query = "" +
                "SELECT sA.userId, userAddressId, recipient, address, city, state, phone " +
                "FROM users as user " +
                "   JOIN shippingAddresses sA on user.userId = sA.userId " +
                "WHERE user.userId = ?";

        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            try {
                rs = preparedStatement.executeQuery();
                userShippingAddresses = buildUserAddresses(rs);

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(preparedStatement);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
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

