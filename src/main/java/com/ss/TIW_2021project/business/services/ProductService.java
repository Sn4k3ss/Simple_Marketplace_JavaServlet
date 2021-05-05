package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.ProductsDAO;
import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
     * Return a list of {@link List<Product> SupplierProduct} that contains the given {@link String keyword} in {@link Product#productName} or {@link Product#productDescription}
     *
     * @param keyword the keyword that needs to be in name or description
     * @return a list of relevant products
     * @throws UnavailableException can't get the products
     */
    public List<SupplierProduct> getRelevantProducts(String keyword) throws UnavailableException {

        ProductsDAO productsDAO = new ProductsDAO(servletContext);
        List<SupplierProduct> retrievedProducts = productsDAO.getCatalogue().getSupplierProductList();

        retrievedProducts.removeIf(
                supplierProduct -> (!supplierProduct.getProductDescription().contains(keyword) && !supplierProduct.getProductName().contains(keyword) ));

        return retrievedProducts;
    }

    /**
     * Gets the 5 most recent seen {@link List<Product> products} by the user.
     *
     * If there aren't at least 5 products then 5 products from a random category are returned
     *
     * @param userId the user id
     * @return the last last products seen by the user.
     */
    public List<Product> getLastUserProducts(Integer userId) throws UnavailableException {

        ProductsDAO productsDAO = new ProductsDAO(this.servletContext);
        List<Product> mostRecentProducts = productsDAO.getLastUserProduct();

        if (mostRecentProducts.size() < 5)
            return productsDAO.getRandomDiscountedProducts();
        else
            return mostRecentProducts;

    }
}
