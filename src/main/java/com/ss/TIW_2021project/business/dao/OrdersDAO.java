package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.entities.*;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

import javax.servlet.ServletContext;
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

    private Connection connection;

    /**
     * Instantiates a new Orders dao.
     *
     * @param servletContext the servlet context
     * @throws UnavailableException the unavailable exception
     */
    public OrdersDAO(ServletContext servletContext) throws UtilityException {
        connection = ConnectionFactory.getConnection(servletContext);
    }


    /**
     * Gets order.
     *
     * @param orderId the order id
     * @return the order
     */
    public Order getOrder(Integer orderId) {
        //TODO
        return null;
    }

    /**
     * Gets all user orders
     *
     * @param userId the user id
     * @return all user orders or an empty collection it there isn't any
     * @throws SQLException the sql exception
     */
    public List<Order> retrieveUserOrders(Integer userId) throws DAOException {

        List<Order> orders;

        String ordersListQuery = "SELECT * " +
                "FROM orders ord " +
                "WHERE userId = ? " +
                "ORDER BY ord.orderPlacementDate desc";

        try (PreparedStatement ordersListStm = connection.prepareStatement(ordersListQuery);) {
            ordersListStm.setInt(1, userId);
            try (ResultSet ordersRS = ordersListStm.executeQuery();) {
                orders = buildOrdersList(ordersRS);

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException | DAOException exception) {
            throw new DAOException(DAOException._MALFORMED_QUERY);
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

        int localOrderId = 0;


        //codice non verificato
        String insertInOrdersTable = "INSERT INTO orders (supplierId, userId, userAddressId, orderAmount, shippingFees, deliveryDate) " +
                "VALUES(?, ?, ?, ?, ?, ?) ";

        String newOrderId = "SELECT MAX(orderId) lastOrderId FROM orders";

        String insertInOrderProducts = "INSERT INTO orderProducts (orderId, supplierId, productId, unitCost, quantity)" +
                "VALUES(?, ?, ?, ?, ?)";

        try (
                PreparedStatement orderTableUpdate = connection.prepareStatement(insertInOrdersTable);
                PreparedStatement getOrderId = connection.prepareStatement(newOrderId);
                PreparedStatement orderProductsTableUpdate = connection.prepareStatement(insertInOrderProducts);
        ) {

            //autocommit false
            connection.setAutoCommit(false);
            //inserisco l'ordine nella table order
            orderTableUpdate.setInt(1, newOrder.getOrderSupplier().getSupplierId());
            orderTableUpdate.setInt(2, newOrder.getUser().getUserId());
            orderTableUpdate.setInt(3, newOrder.getShippingAddress().getShippingAddressId());
            orderTableUpdate.setFloat(4, newOrder.getOrderAmount());
            orderTableUpdate.setFloat(5, newOrder.getOrderShippingFees());
            orderTableUpdate.setObject(6, newOrder.getDeliveryDate());
            orderTableUpdate.executeUpdate();

            //prendo l'id dell'ordine appena inserito
            ResultSet rs = getOrderId.executeQuery();
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
            connection.commit();

        } catch (SQLException e) {
            //errore da loggare
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    //errore da loggare
                }
            }
            throw new DAOException(DAOException._FAIL_TO_INSERT);
        }


    }

    /**
     * Build an {@link List<Order> ordersList} given the ResultSet from query
     *
     * @param resultSet from query
     * @return an {@link List<Order> ordersList} if there's at least one entry in the resultSet
     * @throws SQLException
     */
    private List<Order> buildOrdersList(ResultSet resultSet) throws SQLException {

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
            try (PreparedStatement orderProductsListQueryStm = connection.prepareStatement(orderProductsListQuery);)
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

