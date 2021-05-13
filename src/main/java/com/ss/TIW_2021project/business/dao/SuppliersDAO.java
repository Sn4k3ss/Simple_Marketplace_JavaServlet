package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.supplier.ItemRangeCost;
import com.ss.TIW_2021project.business.entities.supplier.ShippingPolicy;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
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
 * The type Suppliers Data Access Object
 */
public class SuppliersDAO {

    private Connection connection;

    /**
     * Instantiates a new Suppliers DAO.
     *
     * @throws UnavailableException the unavailable exception
     * @param servletContext
     */
    public SuppliersDAO(ServletContext servletContext) throws UnavailableException {
        connection = ConnectionFactory.getConnection(servletContext);
    }


    /**
     * Gets supplier by id.
     *
     * @param idSupplier the idSupplier
     * @return the supplier by id
     * @throws SQLException the sql exception
     */
    public Supplier getSupplierById(Integer idSupplier) throws SQLException, UnavailableException {

        Supplier supplier = new Supplier();

        String query = "SELECT * FROM suppliers WHERE supplierId = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idSupplier);
            try (ResultSet result = preparedStatement.executeQuery();) {
                if (result.next()) {
                    supplier.setSupplierId(result.getInt(1));
                    supplier.setSupplierName(result.getString(2));
                    supplier.setSupplierRating(result.getFloat(3));

                    //this handles the null value returned whenever the
                    Float tmp = result.getFloat(4);
                    if(result.wasNull())
                        supplier.setFreeShippingMinAmount(Float.valueOf(0f));
                    else
                        supplier.setFreeShippingMinAmount(tmp);


                }
                //inutile
                result.close();
            }
            //inutile
            preparedStatement.close();
        } finally {
            try {
                ConnectionFactory.closeConnection(this.connection);
            } catch (SQLException e) {
                throw new UnavailableException("Couldn't close the connection");
            }
        }

        return supplier;
    }

    /**
     * This method simply returns a list containg all the {@link Supplier suppliers} with their respective infos
     *
     */
    public List<Supplier> retrieveSuppliersInfo() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();

        String query = "SELECT  sup.supplierId, sup.supplierName, sup.supplierRating, sup.freeShippingMin," +
                "               sP.`range`, sP.minItem, sP.maxItem, sP.price" +
                "       FROM suppliers as sup " +
                "           JOIN shippingPolicy sP on sup.supplierId = sP.supplierId";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                suppliers = buildSuppliersList(resultSet);
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
