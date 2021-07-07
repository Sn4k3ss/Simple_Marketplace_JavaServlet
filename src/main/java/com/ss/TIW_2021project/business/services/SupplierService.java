package com.ss.TIW_2021project.business.services;

import com.ss.TIW_2021project.business.Exceptions.DAOException;
import com.ss.TIW_2021project.business.Exceptions.ServiceException;
import com.ss.TIW_2021project.business.dao.SuppliersDAO;
import com.ss.TIW_2021project.business.entities.Order;
import com.ss.TIW_2021project.business.entities.ProductsCatalogue;
import com.ss.TIW_2021project.business.entities.ShippingAddress;
import com.ss.TIW_2021project.business.entities.product.ShoppingCartProduct;
import com.ss.TIW_2021project.business.entities.supplier.ItemRangeCost;
import com.ss.TIW_2021project.business.entities.supplier.Supplier;
import com.ss.TIW_2021project.business.entities.product.SupplierProduct;
import com.ss.TIW_2021project.business.entities.supplier.ShippingPolicy;

import javax.servlet.UnavailableException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SupplierService {

    public SupplierService() {

    }


    /**
     * TODO
     * @param supplierId
     * @return
     * @throws ServiceException
     */
    public Supplier getSupplierById(Integer supplierId) throws ServiceException {

        try {
            SuppliersDAO suppliersDAO = new SuppliersDAO();
            return suppliersDAO.getSupplierById(supplierId);
        } catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_SUPPLIERS_INFO);
        }
    }


    /**
     * Given a {@link ProductsCatalogue catalogue} this method provides the {@link Supplier supplier entity} to every products inside of it
     * @param catalogue the catalogue
     * @throws UnavailableException
     */
    public void setSuppliersToProductsInCatalogue(ProductsCatalogue catalogue) throws ServiceException {
        List<Supplier> suppliers;

        if (!catalogue.containsAtLeast(1))
            return;

        try {
            SuppliersDAO suppliersDAO = new SuppliersDAO();
            suppliers = suppliersDAO.retrieveSuppliersInfo();

        } catch (DAOException e) {
            throw new ServiceException(ServiceException._FAILED_TO_RETRIEVE_SUPPLIERS_INFO);
        }

        for(Integer prodId : catalogue.getSupplierProductMap().keySet() ) {
            List<SupplierProduct> prodsSameId = new ArrayList<>(List.copyOf(catalogue.getSupplierProductMap().get(prodId)));

            for ( SupplierProduct prod : prodsSameId) {

                Supplier s = suppliers.stream()
                        .filter(sup -> prod.getSupplierId().equals(sup.getSupplierId()))
                        .findAny()
                        .orElse(null);

                prod.setSupplier(s);
            }
        }
    }

    /**
     * This method compute the shipping fees based on {@link ShippingPolicy supplier's policy}
     *
     * @param productsFromSupplier the products list
     * @param supplierId the supplier Id
     * @param totalAmountAtSupplier total amount
     * @return the shipping cost
     * @throws UnavailableException
     */
    public Float computeShippingFees(List<ShoppingCartProduct> productsFromSupplier,
                                     Integer supplierId,
                                     Float totalAmountAtSupplier)
            throws ServiceException {

        Supplier supplier = getSupplierById(supplierId);


        if (totalAmountAtSupplier >= supplier.getFreeShippingMinAmount() && supplier.getFreeShippingMinAmount() != 0 )
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
     *  This method compute the delivery date based on {@link Supplier}
     *
     * @param shippingAddress
     * @param supplierId
     * @return
     */
    public LocalDate computeDeliveryDate(ShippingAddress shippingAddress, Integer supplierId) throws ServiceException {
        Supplier supplier = getSupplierById(supplierId);

        //TODO da implementare, per il momento la data di spedizione è hard-coded
        // a 5 giorni di distanza dalla data di effettuazione dell'ordine

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

    public void setSupplierInfoOnOrders(List<Order> orders) throws ServiceException {



        for(Order order : orders) {
            Integer supplierId = order.getOrderSupplier().getSupplierId();
            Supplier orderSupplier = getSupplierById(supplierId);
            order.setOrderSupplier(orderSupplier);
        }

    }
}
