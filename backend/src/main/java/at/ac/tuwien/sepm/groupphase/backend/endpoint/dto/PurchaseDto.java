package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Dish;

import java.time.LocalDateTime;
import java.util.Objects;

public class PurchaseDto {
    private Long id;
    private Dish dish;
    private boolean paid;
    private Long invoiceId;
    private LocalDateTime paidAt;

    public PurchaseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
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
        PurchaseDto that = (PurchaseDto) o;
        return paid == that.paid &&
            Objects.equals(id, that.id) &&
            Objects.equals(dish, that.dish) &&
            Objects.equals(invoiceId, that.invoiceId) &&
            Objects.equals(paidAt, that.paidAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish, paid, invoiceId, paidAt);
    }
}
