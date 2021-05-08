package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;
import org.checkerframework.checker.units.qual.C;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Date;

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
                    "JOIN suppliers ON productsCatalogue.supplierId=suppliers.supplierId " +
                "ORDER BY products.productId";

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

        ProductsCatalogue catalogue = new ProductsCatalogue(productsList);
        return catalogue;

    }

    /**
     * Gets last 5 user products seen ordered chronologically.
     *
     * @return the last 5 user product
     */
    public ProductsCatalogue getLastUserProduct(Integer userId) throws SQLException {

        List<SupplierProduct> productsList;

        String query = "SELECT " +
                "   p.productId, p.productName, p.productDescription, p.photoPath," +
                "   C.categoryId, C.categoryName," +
                "   pc.supplierId, pc.productCost, pc.onDiscount, pc.originalProductCost, " +
                "   s.supplierName, s.supplierRating, s.freeShippingMin, " +
                "   b.timestamp " +
                "FROM productsCatalogue AS pc " +
                "   INNER JOIN ( " +
                "       SELECT ph.userId, ph.productId, ph.timestamp " +
                "       FROM productsHistory AS ph" +
                "       ) b  ON pc.productId = b.productId AND b.userId = ? " +
                "JOIN products p ON p.productId = pc.productId " +
                "JOIN suppliers s ON pc.supplierId = s.supplierId " +
                "JOIN productsCategory C on C.categoryId = p.categoryId " +
                "ORDER BY b.timestamp desc";


        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                productsList = buildProductsList(resultSet);
                }

        } catch (SQLException ex) {
            throw ex;
        }

        ProductsCatalogue recentCatalogue = new ProductsCatalogue(productsList);

        return recentCatalogue;

    }

    /**
     * Gets 5 random discounted products from a random category.
     *
     *
     *
     * @return the random products
     * @throws SQLException if an error appears while interacting with the db
     */
    public ProductsCatalogue getRandomDiscountedProducts() throws SQLException {

        Integer categoryId;
        List<SupplierProduct> productsList = new ArrayList<>();

        //TODO
        //How many categories are there?
        //Here is hard-coded but it'll be better to access db to get the correct number
        //int howManyCategories = getHowManyCategories();
        //categoryId = new Random().nextInt(howManyCategories);
        categoryId = 1;


        //query inside the INNER JOIN returns a list of max 5 product which is on sale on discount by at least one supplier
        //outer query gets all the suppliers of the returned products
        //if a product has more supplier than they are sort by supplierId
        String query = "SELECT " +
                "   prod.productId, prod.productName, prod.productDescription, prod.photoPath, prod.categoryId, prod.categoryName, " +
                "   pc1.supplierId, pc1.productCost, pc1.onDiscount, pc1.originalProductCost, " +
                "   s.supplierName, s.supplierRating, s.freeShippingMin " +
                "FROM productsCatalogue as pc1 " +
                "   INNER JOIN (" +
                "       SELECT pc2.productId, C.categoryId, C.categoryName, " +
                "               productName, productDescription, photoPath " +
                "       FROM productsCatalogue AS pc2 " +
                "           JOIN products p on p.productId = pc2.productId " +
                "           JOIN productsCategory C on C.categoryId = p.categoryId " +
                "       WHERE p.categoryId = ? AND pc2.onDiscount" +
                "       GROUP BY pc2.productId " +
                "       ORDER BY RAND() " +
                "       LIMIT 5) prod " +
                "JOIN suppliers s on s.supplierId = pc1.supplierId " +
                "WHERE pc1.productId = prod.productId ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                productsList = buildProductsList(resultSet);
            }

        } catch (SQLException ex) {
            //if error while getting from db
            throw ex;
        }

        ProductsCatalogue catalogue = new ProductsCatalogue(productsList);

        return catalogue;
    }


    /**
     * Sets product displayed updating the database
     *
     * @param userId    the id of the user who viewd the product
     * @param productId the id of the product displayed
     * @throws SQLException the sql exception
     */
    public void setProductDisplayed(Integer userId, Integer productId) throws SQLException {

        //Query p
        String query = "INSERT INTO productsHistory (productId, userId) " +
                "VALUES(?, ?) " +
                "ON DUPLICATE KEY UPDATE timestamp = current_timestamp ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate(query);

        } catch (SQLException ex) {
            //errore during the productsHistory Table update
            throw ex;
        }


        //The procedure maxFiveLastProducts is called to have a maximum of 5 recent element by every user in the productsHistory table
        CallableStatement callableStatement = null;
        try {
            System.out.println("Executing procedure: maxFiveLastProducts(userId)...");
            callableStatement = connection.prepareCall("{call maxFiveLastProducts(?)}");
            callableStatement.setInt(1, userId);
            callableStatement.execute();

        } catch (SQLException ex) {
            //Errore durante l'esecuzione della procedura...
            //"Error while executing routine procedure on db
            throw ex;
        }



    }


    /**
     * Gets all the products that contains the keyword given as parameter inside Name or Description
     *
     * @param keyword
     * @return a list of inherent products
     * @throws SQLException if an error appears while interacting with the db
     */
    public ProductsCatalogue getProductsMatching(String keyword) throws SQLException {
        List<SupplierProduct> productsList = new ArrayList<>();
        String query = "SELECT " +
                "p.productId, p.productName, p.productDescription, p.photoPath," +
                "C.categoryName, C.categoryId , " +
                "pc.supplierId, pc.productCost, pc.onDiscount, pc.originalProductCost, " +
                "S.supplierName " +
                "FROM products as p " +
                "JOIN productsCatalogue as pc ON p.productId=pc.productId " +
                "JOIN productsCategory as C ON p.categoryId= C.categoryId " +
                "JOIN suppliers as S ON pc.supplierId=S.supplierId " +
                "WHERE REGEXP_LIKE(p.productName, '"+ keyword +"') OR REGEXP_LIKE(p.productDescription, '" + keyword + "')" +
                "ORDER BY p.productId ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                productsList = buildProductsList(resultSet);

            }
        } catch (SQLException ex) {
            throw ex;
        }

        ProductsCatalogue catalogue = new ProductsCatalogue(productsList);
        return catalogue;
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
            supplierProduct.setProductCategoryId(resultSet.getInt("categoryId"));
            supplierProduct.setSupplierProductCost(resultSet.getFloat("productCost"));
            supplierProduct.setProductCategory(resultSet.getString("categoryName"));
            supplierProduct.setProductName(resultSet.getString("productName"));
            supplierProduct.setProductDescription(resultSet.getString("productDescription"));
            supplierProduct.setSupplierName(resultSet.getString("supplierName"));
            supplierProduct.setProductImagePath(resultSet.getString("photoPath"));


            if (resultSet.getBoolean("onDiscount")) {
                supplierProduct.setOnDiscount(true);    //we know that from the query
                supplierProduct.setOriginalProductCost(resultSet.getFloat("originalProductCost"));
            }


            productsList.add(supplierProduct);
        }

        return productsList;
    }



}
