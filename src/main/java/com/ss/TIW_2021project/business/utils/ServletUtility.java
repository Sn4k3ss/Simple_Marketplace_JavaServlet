package com.ss.TIW_2021project.business.utils;

import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;

/**
 * An utility calss for other servlets.
 */
public class ServletUtility {


    /**
     * Creats a 'light' version of {@link SupplierProduct supplierProduct} based on the parameters sent into the request
     *
     *
     * @param req from which to get the parameter to build a 'light' version of a {@link SupplierProduct supplierProduct}
     * @return the newly created product
     * @throws UnavailableException if error while getting parameter from req
     */
    public static SupplierProduct buildProductFromRequest(HttpServletRequest req) throws UnavailableException {

        SupplierProduct newProduct = new SupplierProduct();

        newProduct.setProductId(Integer.parseInt(req.getParameter("productId")));
        newProduct.setSupplierId(Integer.parseInt(req.getParameter("supplierId")));
        newProduct.setSupplierProductCost(Float.parseFloat(req.getParameter("supplierProductCost")));

        if (newProduct.getProductId() == null
                || newProduct.getSupplierId() == null
                || newProduct.getSupplierProductCost() == null)
            throw new UnavailableException("Error while retrieving info for the product to be added to cart");

        return newProduct;

    }


}
