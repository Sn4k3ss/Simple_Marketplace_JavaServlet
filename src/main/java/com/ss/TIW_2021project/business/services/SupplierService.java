package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.SuppliersDAO;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.util.List;

public class SupplierService {

    private ServletContext servletContext;

    public SupplierService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void addSupplier(Supplier supplier) {

    }

    public Supplier getSupplierNameById(Integer supplierId) throws UnavailableException, SQLException {

        SuppliersDAO suppliersDAO = new SuppliersDAO(servletContext);
        return suppliersDAO.getSupplierById(supplierId);

    }


    public List<Supplier> getSuppliersHaveFreeShipping() {

        //TODO connessione alla base di dati e ...

        return null;
    }
}
