package com.ss.TIW_2021project.business.dao;

import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;
import com.ss.TIW_2021project.business.utils.ConnectionFactory;

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
    public List<SupplierProduct> getLastUserProduct(Integer userId) throws SQLException {

        List<SupplierProduct> productsList;


        //FIXME ritorna lo stesso prodotto due volte
        String query = "SELECT " +
                "p.productId, p.productName, p.productDescription, p.photoPath, " +
                "pC.categoryId ,pC.categoryName, " +
                "s.supplierName, s.supplierRating, " +
                "pc1.supplierId, pc1.productCost, pc1.onDiscount, pc1.originalProductCost, " +
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
            throw ex;
        }


        return productsList;

    }

    /**
     * Gets 5 random discounted products from a random category.
     *
     *
     *
     * @return the random products
     * @throws SQLException if an error appears while interacting with the db
     */
    public List<SupplierProduct> getRandomDiscountedProducts() throws SQLException {

        Integer categoryId;
        List<SupplierProduct> randomDiscountedProducts = new ArrayList<>();

        //FIXME
        //How many categories are there?
        //Here is hard-coded but it'll be better to access db to get the correct number
        //int howManyCategories = getHowManyCategories();
        //categoryId = new Random().nextInt(howManyCategories);
        categoryId = 1;

        String query = " SELECT " +
                        "        p.productId, p.productName, p.productDescription, p.photoPath, " +
                        "        C.categoryId, C.categoryName, " +
                        "        pc.supplierId, pc.productCost, pc.onDiscount, pc.originalProductCost, " +
                        "        s.supplierName, s.supplierRating, s.freeShippingMin " +
                        "FROM productsCatalogue AS pc " +
                        "    INNER JOIN ( " +
                        "        SELECT pc2.productId, min(pc2.productCost) minCost " +
                        "        FROM productsCatalogue AS pc2 " +
                        "        GROUP BY productId " +
                        "        ) b  ON pc.productId = b.productId AND pc.productCost = b.minCost   -- 'b' is the alias fro each row in the inner call \n" +
                        "JOIN products p ON p.productId = pc.productId " +
                        "JOIN suppliers s ON pc.supplierId = s.supplierId " +
                        "JOIN productsCategory C on C.categoryId = p.categoryId " +
                        "WHERE p.categoryId = ?  AND pc.onDiscount = TRUE -- in questo modo si ritorna solo prodotti 'onDiscsount' ma che potrebbero avere prezzi più alti di altri venditori poichè non 'onDiscount' \n" +
                        "GROUP BY pc.productId  -- fa si che ritorna un solo prezzo per prodotto \n" +
                        "ORDER BY rand() " +
                        "LIMIT 5 " +
                        "-- ritorna il prezzo minimo al quale è venduto un prodotto 'onDiscount' (in offerta) ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                randomDiscountedProducts = buildProductsList(resultSet);
            }

        } catch (SQLException ex) {
            //if error while getting from db
            throw ex;
        }

        return randomDiscountedProducts;
    }


    public void setProductDisplayed(Integer userId, Integer productId) throws SQLException {


        String query = "INSERT INTO tiw_2021projects.productsHistory (productId, userId, timestamp) VALUES (?, ?, ?) ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, userId);


            //FIXME NOT WORKING
            ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Etc/UTC"));
            Timestamp timestamp = Timestamp.valueOf(zdt.toLocalDateTime()); //due ore indietro

            preparedStatement.setTimestamp(3, timestamp );
            preparedStatement.executeUpdate(query);

        } catch (SQLException ex) {
            //errore durante l'inserimento in productsHistory table
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
