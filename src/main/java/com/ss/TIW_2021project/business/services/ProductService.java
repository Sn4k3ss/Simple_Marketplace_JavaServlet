package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.ProductsDAO;
import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProductService {

    private ServletContext servletContext;

    public ProductService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    public List<SupplierProduct> getProductsBySupplier(Integer idSupplier) throws UnavailableException, SQLException {

        ProductsDAO productsDAO = new ProductsDAO(servletContext);

        List<SupplierProduct> products = productsDAO.getProductsBySupplier(idSupplier);

        if(products != null)
            return products;
        else
            return new ArrayList<>();
    }

    public List<Product> getProductUnderX(Float maxAmount) {

        //TODO

        return null;
    }

    /**
     * Return a list of {@link List<Product> SupplierProduct} that contains the given {@link String keyword} in {@link Product#getProductName()} or {@link Product#getProductDescription()}
     *
     * @param keyword the keyword that needs to be in name or description
     * @return a list of relevant products
     */
    public ProductsCatalogue getRelevantProducts(String keyword) throws UnavailableException {

        try {
            ProductsDAO productsDAO = new ProductsDAO(servletContext);

            //List<SupplierProduct> retrievedProducts = productsDAO.getCatalogue().getSupplierProductList();
            ProductsCatalogue retrievedProducts = productsDAO.getProductsMatching(keyword);

            /*
            retrievedProducts.removeIf(
                    supplierProduct -> (!supplierProduct.getProductDescription().contains(keyword) && !supplierProduct.getProductName().contains(keyword)));

             */

            return retrievedProducts;
        } catch (SQLException e) {
            throw  new UnavailableException("Error while retrieving from databse");
        }
    }

    /**
     * Gets the 5 most recent seen {@link List<Product> products} by the user.
     *
     * If there aren't at least 5 products then 5 products from a random category are returned
     *
     * @param userId the user id
     * @return the last last products seen by the user.
     */
    public ProductsCatalogue getLastUserProducts(Integer userId) throws UnavailableException {


        ProductsDAO productsDAO = null;
        productsDAO = new ProductsDAO(this.servletContext);

        ProductsCatalogue mostRecentProducts = null;

        try {
            mostRecentProducts = productsDAO.getLastUserProduct(userId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            //Ma facciamo continuare l'esecuzione per provare a prendere almeno i 5 prodotti in offerta da una categoria casuale
        }

        if (mostRecentProducts != null && mostRecentProducts.containsAtLeast(5)) {
            return mostRecentProducts;
        }



        ProductsCatalogue randomDiscountedProducts = null;
        try {
            randomDiscountedProducts = productsDAO.getRandomDiscountedProducts();
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new UnavailableException("Error while retrieving the last five products seen by the user");
        }

        return randomDiscountedProducts;

    }



    public void setProductDisplayed(Integer userId, int productId) throws SQLException, UnavailableException {

        try {
            ProductsDAO productsDAO = new ProductsDAO(servletContext);
            productsDAO.setProductDisplayed(userId, productId);

        } catch (SQLException | UnavailableException ex) {
            //se arriva una sqlexception allora il problema è nella query
            //se è una unavailable exception allora il problema sta nella connessione al db
            //exception.printStackTrace();
            throw ex;
        }

    }


        //Data una lista conntenente diversi prodotti venduti da diversi venditori, raggruppa per prodotti lasciando il venditore col prezzo più basso
        private List<SupplierProduct> getMinPriceProducts(List<SupplierProduct> productsList) {
        return new ArrayList<>(productsList.stream()
                .collect(Collectors.toMap(Product::getProductId,
                                            Function.identity(),
                                            (p1, p2) -> p1.getOriginalProductCost()<= p2.getOriginalProductCost()? p1 : p2))
                .values());
    }
}
