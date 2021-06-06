package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.entities.supplier.ItemRangeCost;
import com.ss.TIW_2021project.business.entities.supplier.ShippingPolicy;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.utils.ConnectionHandler;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Suppliers Data Access Object
 */
public class SuppliersDAO {

    private Connection conn;

    /**
     * Instantiates a new Suppliers DAO.
     *
     * @throws UtilityException if it's not possible to get a connection
     */
    public SuppliersDAO() {
        super();
    }


    /**
     * Gets supplier by id.
     *
     * @param supplierId the supplierId
     * @return the supplier
     * @throws SQLException the sql exception
     */
    public Supplier getSupplierById(Integer supplierId) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        Supplier supplier = null;

        String query = "" +
                "SELECT  sup.supplierId, sup.supplierName, sup.supplierRating, sup.freeShippingMin, sup.imagePath, " +
                "        sP.`range`, sP.minItem, sP.maxItem, sP.price " +
                "FROM suppliers as sup " +
                "    JOIN shippingPolicy sP on sup.supplierId = sP.supplierId " +
                "WHERE sup.supplierId = ?";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, supplierId);
            try {
                resultSet = preparedStatement.executeQuery();
                supplier = buildSuppliersList(resultSet).get(0);

            } catch (SQLException exception) {
                //error while executing Query
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            //error while preparing the query
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(resultSet);
                ConnectionHandler.closeQuietly(preparedStatement);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }

        return supplier;
    }

    /**
     * This method simply returns a list containg all the {@link Supplier suppliers} with their respective infos
     *
     */
    public List<Supplier> retrieveSuppliersInfo() throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        List<Supplier> suppliers = null;

        String query = "SELECT  sup.supplierId, sup.supplierName, sup.supplierRating, sup.freeShippingMin, sup.imagePath, " +
                "               sP.`range`, sP.minItem, sP.maxItem, sP.price" +
                "       FROM suppliers as sup " +
                "           JOIN shippingPolicy sP on sup.supplierId = sP.supplierId";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = conn.prepareStatement(query);
            try {
                resultSet = preparedStatement.executeQuery();
                suppliers = buildSuppliersList(resultSet);

            } catch (SQLException exception) {
                //error while executing Query
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            //error while preparing the query
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(resultSet);
                ConnectionHandler.closeQuietly(preparedStatement);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }

        return suppliers;
    }

    private List<Supplier> buildSuppliersList(ResultSet resultSet) throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();

        Supplier supplier = new Supplier();
        ShippingPolicy shippingPolicy = new ShippingPolicy();
        List<ItemRangeCost> ranges = new ArrayList<>();
        int prevSup = 0;
        boolean lastRange = false;

        while (resultSet.next()) {

            if( resultSet.getInt("supplierId") != prevSup ) {
                lastRange = false;
                supplier = new Supplier();
                supplier.setSupplierId(resultSet.getInt("supplierId"));
                supplier.setSupplierName(resultSet.getString("supplierName"));
                supplier.setSupplierRating(resultSet.getFloat("supplierRating"));
                supplier.setFreeShippingMinAmount(resultSet.getFloat("freeShippingMin"));
                supplier.setImagePath(resultSet.getString("imagePath"));

                if (supplier.getFreeShippingMinAmount() > 0)
                    supplier.setHasFreeShipping(true);
            }

            if( resultSet.getInt("range") == 1 ) {
                shippingPolicy = new ShippingPolicy();
                ranges = new ArrayList<>();
                ItemRangeCost itemRangeCost = new ItemRangeCost();
                itemRangeCost.setMinAmount(resultSet.getInt("minItem"));
                itemRangeCost.setMaxAmount(resultSet.getInt("maxItem"));
                if (resultSet.wasNull())
                    lastRange = true;
                itemRangeCost.setCost(resultSet.getFloat("price"));
                ranges.add(itemRangeCost);
            } else {
                ItemRangeCost itemRangeCost = new ItemRangeCost();
                itemRangeCost.setMinAmount(resultSet.getInt("minItem"));
                itemRangeCost.setMaxAmount(resultSet.getInt("maxItem"));
                if (resultSet.wasNull())
                    lastRange = true;
                itemRangeCost.setCost(resultSet.getFloat("price"));
                ranges.add(itemRangeCost);
            }

            prevSup = supplier.getSupplierId();

            if(lastRange) {
                shippingPolicy.setRanges(ranges);
                supplier.setSupplierShippingPolicy(shippingPolicy);
                suppliers.add(supplier);
            }

        }

        return suppliers;
    }
}
