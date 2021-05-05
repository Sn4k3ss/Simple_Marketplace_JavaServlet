package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.Product;
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
import java.util.Collections;
import java.util.List;

public class ProductsDAO {

    private Connection connection;

    public ProductsDAO(ServletContext servletContext) throws UnavailableException {
        connection = ConnectionFactory.getConnection(servletContext);
    }


    public List<SupplierProduct> getProductsBySupplier(Integer supplierId) throws SQLException {

        List<SupplierProduct> productsList = new ArrayList<>();

        String query = "SELECT * FROM productsCatalogue WHERE supplierId = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, supplierId);
            try (ResultSet result = preparedStatement.executeQuery();) {
                SupplierProduct supplierProduct = null;
                while (result.next()) {
                    supplierProduct = new SupplierProduct();
                    supplierProduct.setSupplierId(result.getInt("supplierId"));
                    supplierProduct.setProductId(result.getInt("productId"));
                    supplierProduct.setSupplierProductCost(result.getFloat("productCost"));
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
                    supplierProduct.setSupplierProductCost(result.getFloat("productCost"));

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

    /**
     * Gets last 5 user products seen ordered chronologically.
     *
     * @return the last 5 user product
     */
    public List<Product> getLastUserProduct(Integer userId) {

        //voglio che dalla table productsHistory mi prenda i 5 prodotti più recenti che l'utente abbia visualizzato
        //successivamente bisogna andare a vedere ne catalogo quali venditori lo forniscono e ritornare la lista COMPLETA


        List<Product> productsList = new ArrayList<>();

        String query = "SELECT * " +
                "FROM productsHistory " +
                "JOIN productsCatalogue ON productsHistory.productId = productsCatalogue.productId " +
                "WHERE userId = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);
            try (ResultSet result = preparedStatement.executeQuery();) {
                SupplierProduct supplierProduct = null;
                while (result.next()) {
                    supplierProduct = new SupplierProduct();
                    supplierProduct.setSupplierId(result.getInt("supplierId"));
                    supplierProduct.setProductId(result.getInt("productId"));
                    supplierProduct.setSupplierProductCost(result.getFloat("productCost"));
                    productsList.add(supplierProduct);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return productsList;

    }

    /**
     * Gets 5 random products from a random category.
     *
     *
     *
     * @return the random products
     */
    public List<Product> getRandomDiscountedProducts() {

        Integer categoryId;
        List<Product> randomDiscountedProducts = new ArrayList<>();

        //FIXME
        //How many categories are there?
        //Here is hard-coded but it'll be better to access db to get the correct number
        //categoryId = new Random().nextInt(13);


        //TODO
        //Just for now the category is fixed to the first category due to lack of products in database
        //The randomness in the products will be implemented later

        categoryId = 1;

        //query da fare
        //richiesta una join tra il catalogo e la categoria
        //facendo si che però non sia abbiano prodotti uguali



        return null;
    }
}
