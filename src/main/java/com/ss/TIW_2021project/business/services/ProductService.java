package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.ProductsDAO;
import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
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
     * Return a list of {@link List<Product> SupplierProduct} that contains the given {@link String keyword} in {@link Product#name} or {@link Product#description}
     *
     * @param keyword the keyword that needs to be in name or description
     * @return a list of relevant products
     * @throws UnavailableException can't get the products
     */
    public List<SupplierProduct> getRelevantProducts(String keyword) throws UnavailableException {

        ProductsDAO productsDAO = new ProductsDAO(servletContext);
        List<SupplierProduct> retrievedProducts = productsDAO.getCatalogue().getSupplierProductList();

        retrievedProducts.removeIf(
                supplierProduct -> (!supplierProduct.getDescription().contains(keyword) && !supplierProduct.getSupplierName().contains(keyword) ));

        return retrievedProducts;
    }
}
