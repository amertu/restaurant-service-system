package com.spring.restaurant.backend.service;

import com.spring.restaurant.backend.entity.Bill;
import com.spring.restaurant.backend.entity.Purchase;
import jakarta.xml.bind.ValidationException;

import java.util.List;

public interface BillService {

    /**
     * Buy dishes by a user

     * @return a list of paid dishes
     */
    List<Purchase> buyDishes(Bill bill) throws ValidationException;

    /**
     * Create a PDF of the bill
     * @param purchases the list of purchases to be included in the bill
     * @param bill the bill object containing details for the PDF
     * @throws ValidationException if there is an error in validation
     */
    void createPdfOfBill(List<Purchase> purchases, Bill bill) throws ValidationException;

    /**
     * Get details of all saved bills
     * @return all saved bills
     */
    List<Bill> getAllBills();

    /**
     * Get a Bill by invoice id
     * @param invoiceId id of the invoice
     * @return a bill matching the parameters
     */
    Bill getBillByInvoiceId(Long invoiceId);

}
