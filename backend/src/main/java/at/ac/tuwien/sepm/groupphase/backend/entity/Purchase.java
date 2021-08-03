package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean paid;

    @ManyToOne(fetch = FetchType.EAGER)
    private Dish dish;

    private Long invoiceId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return paid == purchase.paid &&
            Objects.equals(id, purchase.id) &&
            Objects.equals(dish, purchase.dish) &&
            Objects.equals(invoiceId, purchase.invoiceId) &&
            Objects.equals(paidAt, purchase.paidAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paid, dish, invoiceId, paidAt);
    }

    @Override
    public String toString() {
        return "Purchase{" +
            "id=" + id +
            ", paid=" + paid +
            ", dish=" + dish +
            ", invoiceId=" + invoiceId +
            ", paidAt=" + paidAt +
            '}';
    }

    public Purchase() {
    }

    public static final class PurchaseBuilder {
        private Long id;
        private Dish dish;
        private boolean paid;
        private Long invoiceId;

        public static PurchaseBuilder aPurchaseBuilder() {
            return new PurchaseBuilder();
        }


        public PurchaseBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public PurchaseBuilder withSpot(Dish dish) {
            this.dish = dish;
            return this;
        }

        public PurchaseBuilder withPaid(boolean paid) {
            this.paid = paid;
            return this;
        }

        public PurchaseBuilder withInvoiceId(Long invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Purchase buildPurchase() {
            Purchase purchase = new Purchase();
            purchase.setId(id);
            purchase.setPaid(paid);
            purchase.setDish(dish);
            purchase.setInvoiceId(invoiceId);
            return purchase;
        }
    }
}
