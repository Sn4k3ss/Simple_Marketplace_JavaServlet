package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.dao.SuppliersDAO;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.ShoppingCartProduct;
import com.ss.TIW_2021project.business.entities.supplier.ItemRangeCost;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.entities.supplier.SupplierProduct;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SupplierService {

    private ServletContext servletContext;

    public SupplierService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    /**
     * TODO
     * @param supplierId
     * @return
     * @throws UnavailableException
     * @throws SQLException
     */
    public Supplier getSupplierById(Integer supplierId) throws UnavailableException, SQLException {
        SuppliersDAO suppliersDAO = new SuppliersDAO(servletContext);
        return suppliersDAO.getSupplierById(supplierId);
    }

    /**
     * TODO
     * @return
     */
    public List<Supplier> getSuppliersHaveFreeShipping() {

        //TODO connessione alla base di dati e ...

        return null;
    }


    /**
     * Given a {@link ProductsCatalogue catalogue} this method
     * @param retrievedProducts
     * @throws UnavailableException
     */
    public void setSuppliersToProducts(ProductsCatalogue retrievedProducts) throws UnavailableException {

        SuppliersDAO suppliersDAO = new SuppliersDAO(servletContext);
        List<Supplier> suppliers;

        try {
            suppliers = suppliersDAO.retrieveSuppliersInfo();

        } catch (SQLException ex) {
            throw  new UnavailableException("Error while retrieving info about the suppliers from db");
        }

        for(Integer prodId : retrievedProducts.getSupplierProductMultiMap().keySet()) {
            List<SupplierProduct> prodsSameId = new ArrayList<>(List.copyOf(retrievedProducts.getSupplierProductMultiMap().get(prodId)));

            for ( SupplierProduct prod : prodsSameId) {

                Supplier s = suppliers.stream()
                        .filter(sup -> prod.getSupplierId().equals(sup.getSupplierId()))
                        .findAny()
                        .orElse(null);

                prod.setSupplier(s);
            }
        }


    }

    public Float computeShippingFees(List<ShoppingCartProduct> productsFromSupplier, Integer supplierId, Float totalAmountAtSupplier) throws UnavailableException {

        Supplier supplier;
        try {
            supplier = getSupplierById(supplierId);
        } catch (UnavailableException | SQLException e) {
            throw new UnavailableException("error wile getting the supplier info");
            //TODO to be handled
        }

        if (totalAmountAtSupplier >= supplier.getFreeShippingMinAmount())
            return 0f;

        Integer totalItemNum = 0;

        for (ShoppingCartProduct prod: productsFromSupplier)
            totalItemNum += prod.getHowMany();

        List<ItemRangeCost> ranges = supplier.getSupplierShippingPolicy().getRanges();

        for(ItemRangeCost range : ranges){
            if(totalItemNum >= range.getMinAmount() && totalItemNum <= range.getMaxAmount())
                return range.getCost();
        }

        return ranges.get(ranges.size() - 1).getCost();
    }

    /**
     *  This method is used to get the delivery date when submitting an order from a supplier
     *
     * @param shippingAddress
     * @param supplierId
     * @return
     */
    public LocalDate computeDeliveryDate(ShippingAddress shippingAddress, Integer supplierId) throws UnavailableException {

        SuppliersDAO suppliersDAO = new SuppliersDAO(servletContext);
        try {
            Supplier supplier = suppliersDAO.getSupplierById(supplierId);
        } catch (SQLException exception) {
            throw new UnavailableException("Error while getting the supplier");
        }

        //TODO da implementare, per il momento la data di spedizione è hard-coded
        // a 5 giorni di distanza dalla data di effettuamento dell'ordine

        //setting deliveryDate
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        Date fiveDaysLater = calendar.getTime();
        Instant instant = fiveDaysLater.toInstant();
        ZoneId zoneId = ZoneId.of("UTC");
        LocalDate fiveDaysLaterDate = LocalDate.ofInstant(instant, zoneId);
        return fiveDaysLaterDate;

    }

    public void setSupplierInfoOnOrders(List<Order> orders) throws UnavailableException {

        Integer supplierId = orders.get(0).getOrderSupplier().getSupplierId();
        Supplier orderSupplier = new Supplier();

        SuppliersDAO suppliersDAO = new SuppliersDAO(servletContext);

        try {
            orderSupplier = suppliersDAO.getSupplierById(supplierId);
        } catch (SQLException exception) {
            throw new UnavailableException("Error while getting info about supplier");
        }

        for(Order order : orders) {
            order.setOrderSupplier(orderSupplier);
        }

    }
}
