package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.ShoppingCartProduct;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.utils.ConnectionHandler;

import java.sql.*;
import java.util.*;

public class ProductsDAO {

    private Connection conn;

    public ProductsDAO()  {
        super();
    }

    public List<SupplierProduct> getProductsBySupplier(Integer supplierId) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        List<SupplierProduct> productsList = new ArrayList<>();

        String query = "SELECT * FROM productsCatalogue WHERE supplierId = ? ";

        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, supplierId);
            try {
                rs = ps.executeQuery();
                SupplierProduct supplierProduct = null;
                while (rs.next()) {
                    supplierProduct = new SupplierProduct();
                    supplierProduct.setSupplierId(rs.getInt("supplierId"));
                    supplierProduct.setProductId(rs.getInt("productId"));
                    supplierProduct.setSupplierProductCost(rs.getFloat("productCost"));
                    productsList.add(supplierProduct);
                }
            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(ps);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }


        return productsList;
    }

    /**
     * Returns the entire catalogue
     *
     * @return catalogue containing every product in the marketplace
     */
    public ProductsCatalogue getCatalogue() throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

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

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(query);

            try {
                rs = ps.executeQuery();
                SupplierProduct supplierProduct = null;
                while (rs.next()) {
                    supplierProduct = new SupplierProduct();
                    supplierProduct.setProductId(rs.getInt("productId"));
                    supplierProduct.setProductName(rs.getString("productName"));
                    supplierProduct.setProductDescription(rs.getString("productDescription"));
                    supplierProduct.setProductCategory(rs.getString("categoryName"));
                    supplierProduct.setSupplierId(rs.getInt("supplierId"));
                    supplierProduct.setSupplierName(rs.getString("supplierName"));
                    supplierProduct.setSupplierProductCost(rs.getFloat("productCost"));

                    productsList.add(supplierProduct);
                }
            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(ps);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }

        ProductsCatalogue catalogue = new ProductsCatalogue(productsList);
        return catalogue;

    }

    /**
     * Gets last 5 user products seen ordered chronologically.
     *
     * @return the last 5 user product
     */
    public ProductsCatalogue getLastUserProduct(Integer userId) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

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
                "ORDER BY b.timestamp desc, pc.productCost";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            try {
                rs = ps.executeQuery();
                productsList = buildSupplierProductsList(rs);

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(ps);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }

        return new ProductsCatalogue(productsList);
    }

    /**
     * Gets 5 random discounted products from a random category.
     *
     *
     *
     * @return the random products
     * @throws SQLException if an error appears while interacting with the db
     */
    public ProductsCatalogue getRandomDiscountedProducts() throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

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
                "WHERE pc1.productId = prod.productId " +
                "ORDER BY productCost ";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, categoryId);

            try {
                rs = ps.executeQuery();
                productsList = buildSupplierProductsList(rs);
            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(ps);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }


        return new ProductsCatalogue(productsList);
    }

    /**
     * Gets all the products that contains the keyword given as parameter inside Name or Description
     *
     * @param keyword
     * @return a list of inherent products
     * @throws SQLException if an error appears while interacting with the db
     */
    public ProductsCatalogue getProductsMatching(String keyword) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

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
                "ORDER BY pc.productCost, p.productId ";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(query);

            try {
                rs = ps.executeQuery();
                productsList = buildSupplierProductsList(rs);

            } catch (SQLException exception) {
                throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
            }
        } catch (SQLException exception) {
            throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
        } finally {
            try {
                ConnectionHandler.closeQuietly(rs);
                ConnectionHandler.closeQuietly(ps);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
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
    public void setProductDisplayed(Integer userId, Integer productId) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }


        String query = "INSERT INTO productsHistory (productId, userId) " +
                "VALUES(?, ?) " +
                "ON DUPLICATE KEY UPDATE timestamp = current_timestamp ";

        PreparedStatement ps = null;
        CallableStatement cs = null;

        try {
            ps = conn.prepareStatement(query);
            cs = conn.prepareCall("{call maxFiveLastProducts(?)}");

            conn.setAutoCommit(false);

            ps.setInt(1, productId);
            ps.setInt(2, userId);
            ps.executeUpdate() ;

            cs.setInt(1, userId);
            cs.execute();

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
                ConnectionHandler.closeQuietly(ps);
                ConnectionHandler.closeQuietly(cs);
                ConnectionHandler.releaseConnectionToPool(conn);
            } catch (UtilityException e) {
                throw new DAOException(DAOException._ERROR_RELEASING_CONN);
            }
        }
    }

    public void setProductsInfo(List<Order> orders) throws DAOException {

        try {
            conn = ConnectionHandler.getConnectionFromPool();
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_GETTING_CONN);
        }

        String prodQuery = "" +
                "SELECT productName, pC.categoryId ,productDescription, categoryName, photoPath " +
                "FROM products " +
                "JOIN productsCategory pC on pC.categoryId = products.categoryId " +
                "WHERE productId = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;

        for (Order order : orders) {
            for (ShoppingCartProduct shoppingCartProduct : order.getOrderProductsList()) {

                try {
                    ps = conn.prepareStatement(prodQuery);
                    ps.setInt(1, shoppingCartProduct.getProductId());

                    try {
                        rs = ps.executeQuery();
                        buildProductInfo(rs, shoppingCartProduct);

                    } catch (SQLException exception) {
                        throw new DAOException(DAOException._FAIL_TO_RETRIEVE);
                    }
                } catch (SQLException exception) {
                    throw new DAOException(DAOException._ERROR_PREPARING_QUERY);
                }
            }
        }


        try {
            ConnectionHandler.closeQuietly(ps);
            ConnectionHandler.closeQuietly(rs);
            ConnectionHandler.releaseConnectionToPool(conn);
        } catch (UtilityException e) {
            throw new DAOException(DAOException._ERROR_RELEASING_CONN);
        }

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
    private List<SupplierProduct> buildSupplierProductsList(ResultSet resultSet) throws SQLException {
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
                supplierProduct.setOnDiscount(true);
                supplierProduct.setOriginalProductCost(resultSet.getFloat("originalProductCost"));
            }


            productsList.add(supplierProduct);
        }

        return productsList;
    }


    private void buildProductInfo(ResultSet prodRs, ShoppingCartProduct prod) throws SQLException {

        while (prodRs.next()) {
            prod.setProductName(prodRs.getString("productName"));
            prod.setProductDescription(prodRs.getString("productDescription"));
            prod.setProductCategoryId(prodRs.getInt("categoryId"));
            prod.setProductCategory(prodRs.getString("categoryName"));
            prod.setProductImagePath(prodRs.getString("photoPath"));
        }

    }
}
