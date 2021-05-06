package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.nio.file.Path;
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
    public List<SupplierProduct> getLastUserProduct(Integer userId) {

        List<SupplierProduct> productsList;

        String query = "SELECT " +
                "p.productId, " +
                "p.productName, " +
                "p.productDescription, " +
                "pC.categoryName, " +
                "s.supplierName, " +
                "s.supplierRating," +
                "pc1.supplierId, " +
                "pc1.productCost, " +
                "ph.timestamp\n" +
                "FROM products AS p " +
                    "JOIN productsCatalogue AS pc1 ON p.productId = pc1.productId " +
                    "JOIN productsHistory AS ph ON p.productId = ph.productId " +
                    "JOIN productsCategory pC on p.categoryId = pC.categoryId " +
                    "JOIN suppliers s on pc1.supplierId = s.supplierId " +

                "WHERE ph.userId = ? AND pc1.productCost = (select min(pc2.productCost) " +
                                                            "FROM productsCatalogue AS pc2 " +
                                                            "WHERE pc2.productId = p.productId) " +
                "ORDER BY ph.timestamp desc " +
                "LIMIT 5" ;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                productsList = buildProductsList(resultSet);
                }

        } catch (SQLException ex) {
            //if error while getting from db
            //an empty list is returned
            ex.printStackTrace();
            return new ArrayList<>(Collections.emptyList());
        }

        //FIXME
        //in questa lista ci stanno prodotti ripetuti venduti da diversi venditori (a prezzi diversi)
        return productsList;

    }

    /**
     * Gets 5 random discounted products from a random category.
     *
     *
     * @return the random products
     */
    public List<SupplierProduct> getRandomDiscountedProducts() {

        Integer categoryId;
        List<SupplierProduct> randomDiscountedProducts = new ArrayList<>();

        //FIXME
        //How many categories are there?
        //Here is hard-coded but it'll be better to access db to get the correct number
        //int howManyCategories = getHowManyCategories();
        //categoryId = new Random().nextInt(howManyCategories);
        categoryId = 1;

        String query =
                " SELECT " +
                    "products.productId, productName, productDescription, photoPath, " +
                    "productsCategory.categoryId, categoryName, " +
                    "productsCatalogue.supplierId, productCost, onDiscount, originalProductCost, " +
                    "suppliers.supplierName, supplierRating, freeShippingMin " +
                "FROM productsCatalogue " +
                    "JOIN products ON productsCatalogue.productId = products.productId " +
                    "JOIN productsCategory ON products.categoryId = productsCategory.categoryId " +
                    "JOIN suppliers ON productsCatalogue.supplierId = suppliers.supplierId " +
                "WHERE productsCatalogue.onDiscount = true AND products.categoryId = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                randomDiscountedProducts = buildProductsList(resultSet);
            }

        } catch (SQLException ex) {
            //if error while getting from db
            //an empty list is returned
            ex.printStackTrace();
            return new ArrayList<>(Collections.emptyList());
        }

        return randomDiscountedProducts;
    }




    /**
     * Build the {@link List<Product> productsList} from the {@link ResultSet resultSet}
     *
     *
     *
     * @param resultSet - the resultSet containing the product info
     * @return a {@link List<Product> products list} containing every product in {@link ResultSet resultSet}
     * @throws SQLException if error while reading from columns
     */
    private List<SupplierProduct> buildProductsList(ResultSet resultSet) throws SQLException {
        List<SupplierProduct> productsList = new ArrayList<>();

        SupplierProduct supplierProduct;

        while (resultSet.next()) {
            supplierProduct = new SupplierProduct();
            supplierProduct.setSupplierId(resultSet.getInt("supplierId"));
            supplierProduct.setProductId(resultSet.getInt("productId"));
            supplierProduct.setSupplierProductCost(resultSet.getFloat("productCost"));
            supplierProduct.setProductCategory(resultSet.getString("categoryName"));
            supplierProduct.setProductName(resultSet.getString("productName"));
            supplierProduct.setProductDescription(resultSet.getString("productDescription"));
            supplierProduct.setSupplierName(resultSet.getString("supplierName"));
            supplierProduct.setProductImagePath(resultSet.getString("photoPath"));


            //FIXME
            if (resultSet.getBoolean("onDiscount")) {
                supplierProduct.setOnDiscount(true);    //we know that from the query
                supplierProduct.setDiscountedCost(resultSet.getFloat("discountedProductCost"));
            } else {
                supplierProduct.setOnDiscount(false);
                supplierProduct.setDiscountedCost(0f);
            }

            productsList.add(supplierProduct);
        }

        return productsList;
    }



}
