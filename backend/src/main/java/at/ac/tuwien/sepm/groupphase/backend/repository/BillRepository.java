package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Bill;
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
