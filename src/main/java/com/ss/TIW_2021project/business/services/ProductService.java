package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.ProductsDAO;
import com.ss.TIW_2021project.business.entities.Product;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {







    public List<SupplierProduct> getProductsBySupplier(Integer idSupplier) throws UnavailableException, SQLException {

        ProductsDAO productsDAO = new ProductsDAO();

        List<SupplierProduct> products = productsDAO.getProductsBySupplier(idSupplier);

        if(products != null)
            return products;
        else
            return new ArrayList<>();
    }

    public List<Product> getProductUnderX(Float maxAmount) {

        //TODO connessione alla base di dati e recupero prodotti con target maxAmount

        return null;
    }
}
