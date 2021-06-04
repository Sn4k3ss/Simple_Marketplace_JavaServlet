package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.Exceptions.*;
import com.ss.TIW_2021project.business.dao.ProductsDAO;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProductService {


    public ProductService() {
        super();
    }

    public List<SupplierProduct> getProductsBySupplier(Integer idSupplier) throws ServiceException {
        List<SupplierProduct> products = null;

        try {
            ProductsDAO productsDAO = new ProductsDAO();
            products = productsDAO.getProductsBySupplier(idSupplier);
        } catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_PRODUCTS_INFO);
        }

        return products != null ? products : new ArrayList<>();
    }

    /**
     * Return a list of {@link List<Product> SupplierProduct} that contains the given {@link String keyword} in {@link Product#getProductName()} or {@link Product#getProductDescription()}
     *
     * @param keyword the keyword that needs to be in name or description
     * @return a list of relevant products
     */
    public ProductsCatalogue getRelevantProducts(String keyword) throws ServiceException {
        ProductsCatalogue retrievedProducts = null;

        try {
            ProductsDAO productsDAO = new ProductsDAO();
            retrievedProducts = productsDAO.getProductsMatching(keyword);
        } catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_PRODUCTS_INFO);
        }

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
    public ProductsCatalogue getLastUserProducts(Integer userId) throws ServiceException {
        ProductsDAO productsDAO = null;
        ProductsCatalogue mostRecentProducts = null;

        try {
            productsDAO = new ProductsDAO();
            mostRecentProducts = productsDAO.getLastUserProduct(userId);
        }  catch ( DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_PRODUCTS_INFO);
        }

        if (mostRecentProducts != null && mostRecentProducts.containsAtLeast(5)) {
            return mostRecentProducts;
        }

        ProductsCatalogue randomDiscountedProducts = null;
        try {
            randomDiscountedProducts = productsDAO.getRandomDiscountedProducts();
        }  catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_PRODUCTS_INFO);
        }

        return randomDiscountedProducts;
    }



    public void setProductDisplayed(Integer userId, int productId) throws ServiceException {

        try {
            ProductsDAO productsDAO = new ProductsDAO();
            productsDAO.setProductDisplayed(userId, productId);
        }  catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_UPDATE_USERS_LAST_PRODUCTS);
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
            for(Integer productId : catalogue.getSupplierProductMap().keySet())
                for(SupplierProduct product : catalogue.getSupplierProductMap().get(productId)) {
                    if (paramProd.getSupplierId().equals(product.getSupplierId())
                            && paramProd.getProductId().equals(product.getProductId())
                            && paramProd.getSupplierProductCost().equals(product.getSupplierProductCost()))
                        return product;
                }

        }

        return null;

    }

    public void setProductInfoOnOrders(List<Order> orders) throws ServiceException {

        try {
            ProductsDAO productsDAO = new ProductsDAO();
            productsDAO.setProductsInfo(orders);
        } catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_PRODUCTS_INFO);
        }
    }
}
