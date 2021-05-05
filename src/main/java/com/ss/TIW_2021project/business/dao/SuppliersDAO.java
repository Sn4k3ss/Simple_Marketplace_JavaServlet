package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        String query = "SELECT * FROM suppliers WHERE idSupplier = ? ";

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

}
