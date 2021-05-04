package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
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

public class ProductsDAO {

    private Connection connection;

    public ProductsDAO(ServletContext servletContext) throws UnavailableException {
        connection = ConnectionFactory.getConnection(servletContext);
    }


    public List<SupplierProduct> getProductsBySupplier(Integer idSupplier) throws SQLException {

        List<SupplierProduct> productsList = new ArrayList<>();

        String query = "SELECT * FROM productsCatalogue WHERE supplierId = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idSupplier);
            try (ResultSet result = preparedStatement.executeQuery();) {
                SupplierProduct supplierProduct = null;
                while (result.next()) {
                    supplierProduct = new SupplierProduct();
                    supplierProduct.setSupplierId(result.getInt("supplierId"));
                    supplierProduct.setProductId(result.getInt("productId"));
                    supplierProduct.setPrice(result.getFloat("productCost"));
                    productsList.add(supplierProduct);
                }
            }
        }

        return productsList;
    }

    /**
     * Returns the entire catalogue
     *
     * @return catalogue containing every product in the marketplace
     */
    public ProductsCatalogue getCatalogue() {

        List<SupplierProduct> productsList = new ArrayList<>();

        String query = "SELECT " +
                "products.productId," +
                "products.productName," +
                "products.productDescription," +
                "productsCategory.categoryName," +
                "productsCatalogue.supplierId," +
                "suppliers.supplierName," +
                "productsCatalogue.productCost " +
                "FROM products " +
                "JOIN productsCatalogue ON products.productId=productsCatalogue.productId " +
                "JOIN productsCategory ON products.categoryId= productsCategory.categoryId " +
                "JOIN suppliers ON productsCatalogue.supplierId=suppliers.supplierId;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {

            try (ResultSet result = preparedStatement.executeQuery();) {
                SupplierProduct supplierProduct = null;
                while (result.next()) {
                    supplierProduct = new SupplierProduct();
                    supplierProduct.setProductId(result.getInt("productId"));
                    supplierProduct.setProductName(result.getString("productName"));
                    supplierProduct.setProductDescription(result.getString("productDescription"));
                    supplierProduct.setProductCategory(result.getString("categoryName"));
                    supplierProduct.setSupplierId(result.getInt("supplierId"));
                    supplierProduct.setSupplierName(result.getString("supplierName"));
                    supplierProduct.setPrice(result.getFloat("productCost"));

                    productsList.add(supplierProduct);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        ProductsCatalogue catalogue = new ProductsCatalogue();
        catalogue.setSupplierProductList(productsList);
        return catalogue;

    }
}
