package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * finds all purchases of a user by email and the invoice id.
     *
     * @param invoiceId of the bill.
     * @return the found purchase list.
     */
    List<Purchase> findAllByInvoiceId(Long invoiceId);

}

