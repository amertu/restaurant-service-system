package com.spring.restaurant.backend.service;

import com.spring.restaurant.backend.entity.ApplicationUser;
import com.spring.restaurant.backend.entity.Bill;
import com.spring.restaurant.backend.entity.Purchase;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface BillService {

    /**
     * Buy dishes by a user
     * @param email email of the user
        // TODO
     * @return a list of paid dishes
     * @throws ValidationException
     */
    List<Purchase> buyDishes(String email, Bill bill) throws ValidationException;

    /**
     * Create a pdf bill
     * @param purchases of a bill
     * @param invoiceId number of the invoice
     * @param paidAt LocalDateTime, at which a bill is paid
     * @param user user who creates a bill
     */
    void createPdfOfBill(List<Purchase> purchases, Long invoiceId, LocalDateTime paidAt, String reservationStartedAt, String servedTables, ApplicationUser user);

    /**
     * save a bill
     * @param filename the filename of the bill
     * @param invoiceId of the bill
     * @param paidAt the date, at which the bill is paid
     * @param price the total price of the bill
     */
    void saveBill(String filename, Long invoiceId, LocalDateTime paidAt, double price, String reservationStartedAt, String servedTables) throws IOException;

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
