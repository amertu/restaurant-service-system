package com.spring.restaurant.backend.repository;

import com.spring.restaurant.backend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    /**
     *
     * @param invoiceId of the bill
     * @return the founded bill with invoiceId
     */
    Bill findByInvoiceId(Long invoiceId);

}
