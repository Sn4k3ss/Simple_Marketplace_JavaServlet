package com.ss.TIW_2021project.business.utils;

import com.ss.TIW_2021project.business.Exceptions.UtilityException;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.http.HttpServletRequest;

/**
 * An utility calss for other servlets.
 */
public class ServletUtility {

    private ServletUtility() {

    }

    /**
     * Creats a 'light' version of {@link SupplierProduct supplierProduct} based on the parameters sent into the request
     *
     *
     * @param req from which to get the parameter to build a 'light' version of a {@link SupplierProduct supplierProduct}
     * @return the newly created product
     * @throws UtilityException if error while getting parameter from req
     */
    public static SupplierProduct buildProductFromRequest(HttpServletRequest req) throws UtilityException {

        SupplierProduct newProduct = new SupplierProduct();

        newProduct.setProductId(Integer.parseInt(req.getParameter("productId")));
        newProduct.setSupplierId(Integer.parseInt(req.getParameter("supplierId")));
        newProduct.setSupplierProductCost(Float.parseFloat(req.getParameter("supplierProductCost")));

        if (newProduct.getProductId() == null
                || newProduct.getSupplierId() == null
                || newProduct.getSupplierProductCost() == null)
            throw new UtilityException(UtilityException._ERROR_GETTING_PROD_FROM_REQ);

        return newProduct;

    }

    public static String getImage(HttpServletRequest req, String bucketKey) {

        String s3bucket = req.getServletContext().getInitParameter("s3tiwBucket");

        return s3bucket.concat(bucketKey);

    }

}
