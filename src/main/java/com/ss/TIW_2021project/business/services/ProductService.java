package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.ProductsDAO;
import com.ss.TIW_2021project.business.dao.SuppliersDAO;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.ShoppingCart;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
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

            ProductsCatalogue retrievedProducts = productsDAO.getProductsMatching(keyword);

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
            throw new UnavailableException("Error while retrieving five random products");
        }

        return randomDiscountedProducts;

    }



    public void setProductDisplayed(Integer userId, int productId) throws UnavailableException {

        try {
            ProductsDAO productsDAO = new ProductsDAO(servletContext);
            productsDAO.setProductDisplayed(userId, productId);

        } catch (SQLException ex) {
            //se arriva una sqlexception allora il problema è nella query
            //se è una unavailable exception allora il problema sta nella connessione al db
            //exception.printStackTrace();
            throw new UnavailableException("Error while interacting with database");
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


    /**
     * This method takes a list of {@link ProductsCatalogue catalogues} and a {@link SupplierProduct supplierProduct} and look for
     * the supplierProduct inside of the catalogues
     *
     * @param catalogues a list of catalogue
     * @param paramProd the product we're looking for
     * @return the product if is found in the catalogues, null otherwise
     */
    public SupplierProduct lookForProduct(List<ProductsCatalogue> catalogues, SupplierProduct paramProd) {

        for (ProductsCatalogue catalogue : catalogues) {
            for(Integer productId : catalogue.getSupplierProductMultiMap().keySet())
                for(SupplierProduct product : catalogue.getSupplierProductMultiMap().get(productId)) {
                    if (paramProd.getSupplierId().equals(product.getSupplierId())
                            && paramProd.getProductId().equals(product.getProductId())
                            && paramProd.getSupplierProductCost().equals(product.getSupplierProductCost()))
                        return product;
                }

        }

        return null;

    }

    public void setProductInfoOnOrders(List<Order> orders) throws UnavailableException {
        ProductsDAO productsDAO = new ProductsDAO(servletContext);

        try {
            productsDAO.setProductsInfo(orders);
        } catch (SQLException exception) {
            throw new UnavailableException("Error while retrieving info on products in order");

        }


    }
}
