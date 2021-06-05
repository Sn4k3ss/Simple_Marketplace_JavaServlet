package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.entities.product.ShoppingCartProduct;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.utils.ConnectionHandler;

import javax.servlet.UnavailableException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Orders dao.
 */
public class OrdersDAO {

    private Connection conn;

    /**
     * Instantiates a new Orders dao.
     *
     * @throws UnavailableException the unavailable exception
     */
    public OrdersDAO() {
        super();
    }


    /**
     * Gets all user orders
     *
     * @param userId the user id
     * @return all user orders or an empty collection it there isn't any
     * @throws SQLException the sql exception
     */
    public List<Order> retrieveUserOrders(Integer userId) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        List<Order> orders;

        String ordersListQuery = "SELECT * " +
                "FROM orders ord " +
                "WHERE userId = ? " +
                "ORDER BY ord.orderPlacementDate desc";

        PreparedStatement ordersListStm = null;
        ResultSet ordersRS = null;

        try {
            ordersListStm = conn.prepareStatement(ordersListQuery);
            ordersListStm.setInt(1, userId);
            try {
                ordersRS = ordersListStm.executeQuery();
                orders = buildOrdersList(ordersRS);

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(ordersRS);
                ConnectionHandler.closeQuietly(ordersListStm);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }

        return orders;
    }

    /**
     * Place order.
     *
     * @param newOrder the new order
     * @throws SQLException the sql exception
     */
    public void placeOrder(Order newOrder) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        int localOrderId = 0;


        //codice non verificato
        String insertInOrdersTable = "INSERT INTO orders (supplierId, userId, userAddressId, orderAmount, shippingFees, deliveryDate) " +
                "VALUES(?, ?, ?, ?, ?, ?) ";

        String newOrderId = "SELECT MAX(orderId) lastOrderId FROM orders";

        String insertInOrderProducts = "INSERT INTO orderProducts (orderId, supplierId, productId, unitCost, quantity)" +
                "VALUES(?, ?, ?, ?, ?)";

        PreparedStatement orderTableUpdate = null;
        PreparedStatement getOrderId = null;
        PreparedStatement orderProductsTableUpdate = null;
        ResultSet rs = null;

        try {
            orderTableUpdate = conn.prepareStatement(insertInOrdersTable);
            getOrderId = conn.prepareStatement(newOrderId);
            orderProductsTableUpdate = conn.prepareStatement(insertInOrderProducts);

            //autocommit false
            conn.setAutoCommit(false);
            //inserisco l'ordine nella table order
            orderTableUpdate.setInt(1, newOrder.getOrderSupplier().getSupplierId());
            orderTableUpdate.setInt(2, newOrder.getUser().getUserId());
            orderTableUpdate.setInt(3, newOrder.getShippingAddress().getShippingAddressId());
            orderTableUpdate.setFloat(4, newOrder.getOrderAmount());
            orderTableUpdate.setFloat(5, newOrder.getOrderShippingFees());
            orderTableUpdate.setObject(6, newOrder.getDeliveryDate());
            orderTableUpdate.executeUpdate();

            //prendo l'id dell'ordine appena inserito
            rs = getOrderId.executeQuery();
            if (rs.next())
                localOrderId = rs.getInt("lastOrderId");

            for(ShoppingCartProduct prod : newOrder.getOrderProductsList()){
                orderProductsTableUpdate.setInt(1, localOrderId);
                orderProductsTableUpdate.setInt(2, newOrder.getOrderSupplier().getSupplierId());
                orderProductsTableUpdate.setInt(3, prod.getProductId());
                orderProductsTableUpdate.setFloat(4, prod.getSupplierProductCost());
                orderProductsTableUpdate.setInt(5, prod.getHowMany());

                orderProductsTableUpdate.executeUpdate();

            }

            //committo i cambiamenti in entrambe le tabelle
            conn.commit();

        } catch (SQLException e) {
            //errore da loggare
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    //errore da loggare
                }
            }
            throw new DAOException(DAOException._FAIL_TO_INSERT);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(orderTableUpdate);
                ConnectionHandler.closeQuietly(getOrderId);
                ConnectionHandler.closeQuietly(orderProductsTableUpdate);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }


    }

    /**
     * Build an {@link List<Order> ordersList} given the ResultSet from query
     *
     * @param resultSet from query
     * @return an {@link List<Order> ordersList} if there's at least one entry in the resultSet
     * @throws SQLException
     */
    private List<Order> buildOrdersList(ResultSet resultSet) throws SQLException, DAOException {

        List<Order> orders = new ArrayList<>();

        String orderProductsListQuery = "" +
                "SELECT oP.orderId, oP.supplierId, userId, userAddressId, " +
                "       orderAmount, shippingFees, deliveryDate, " +
                "       productId, unitCost, quantity " +
                "FROM orders ord " +
                "JOIN orderProducts oP on ord.orderId = oP.orderId " +
                "WHERE ord.orderId = ?";

        Order order;

        while (resultSet.next()) {
            order = new Order();
            order.setOrderId(resultSet.getInt("orderId"));
            order.setUser(new User(resultSet.getInt("userId")));
            order.setShippingAddress(new ShippingAddress(resultSet.getInt("userAddressId")));
            order.setOrderPlacementDate(resultSet.getObject("orderPlacementDate", Timestamp.class).toLocalDateTime());
            order.setOrderSupplier(new Supplier(resultSet.getInt("supplierId")));
            order.setOrderAmount(resultSet.getFloat("orderAmount"));
            order.setOrderShippingFees(resultSet.getFloat("shippingFees"));
            order.setDeliveryDate(resultSet.getObject("deliveryDate", LocalDate.class));

            List<ShoppingCartProduct> orderProductsList = new ArrayList<>();
            ShoppingCartProduct prod;
            try (PreparedStatement orderProductsListQueryStm = conn.prepareStatement(orderProductsListQuery);)
            {
                orderProductsListQueryStm.setInt(1, order.getOrderId());
                try (ResultSet orderProductsRS = orderProductsListQueryStm.executeQuery();) {

                    while(orderProductsRS.next()) {
                        prod = new ShoppingCartProduct();
                        prod.setProductId(orderProductsRS.getInt("productId"));
                        prod.setSupplierProductCost(orderProductsRS.getFloat("unitCost"));
                        prod.setHowMany(orderProductsRS.getInt("quantity"));
                        prod.setSupplierId(orderProductsRS.getInt("supplierId"));

                        orderProductsList.add(prod);
                    }
                }

            }

            order.setOrderProductsList(orderProductsList);

            orders.add(order);
        }

        if(orders.isEmpty())
            return new ArrayList<>(Collections.emptyList());
        else
            return orders;
    }
}

