package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductsDAO {

    private Connection connection;

    public ProductsDAO() throws UnavailableException {
        connection = ConnectionFactory.getConnection();
    }


    public List<SupplierProduct> getProductsBySupplier(Integer idSupplier) throws SQLException {

        List<SupplierProduct> productsList = new ArrayList<>();

        String query = "SELECT * FROM productsCatalogue WHERE idSupplier = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idSupplier);
            try (ResultSet result = preparedStatement.executeQuery();) {
                SupplierProduct supplierProduct = null;
                while (result.next()) {
                    supplierProduct = new SupplierProduct();
                    supplierProduct.setSupplierId(result.getInt("idSupplier"));
                    supplierProduct.setIdProduct(result.getInt("idProduct"));
                    supplierProduct.setPrice(result.getFloat("cost"));
                    productsList.add(supplierProduct);
                }
            }
        }

        return productsList;
    }
}
