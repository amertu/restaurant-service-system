package com.spring.restaurant.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long invoiceId;

    @Lob
    @Column(name = "pdf", nullable = false, columnDefinition = "bytea")
    private byte[] pdf;

    @Column
    private LocalDateTime paidAt;

    @Column
    private double totalCost;

    @Column
    private String reservationStartedAt;

    @Column
    private String servedTables;

    @Transient
    private List<Dish> dishes;

    public Bill() {
    }

    public Bill(Long invoiceId, byte[] pdf, LocalDateTime paidAt, double totalCost) {
        this.invoiceId = invoiceId;
        this.pdf = pdf;
        this.paidAt = paidAt;
        this.totalCost = totalCost;
        this.reservationStartedAt = null;
        this.servedTables = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public String getReservationStartedAt() {
        return reservationStartedAt;
    }

    public void setReservationStartedAt(String reservationStartedAt) {
        this.reservationStartedAt = reservationStartedAt;
    }

    public String getServedTables() {
        return servedTables;
    }

    public void setServedTables(String servedTables) {
        this.servedTables = servedTables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Double.compare(bill.totalCost, totalCost) == 0 &&
            Objects.equals(id, bill.id) &&
            Objects.equals(invoiceId, bill.invoiceId) &&
            Arrays.equals(pdf, bill.pdf) &&
            Objects.equals(paidAt, bill.paidAt) &&
            Objects.equals(reservationStartedAt, bill.reservationStartedAt) &&
            Objects.equals(servedTables, bill.servedTables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, invoiceId, Arrays.hashCode(pdf), paidAt, totalCost, reservationStartedAt, servedTables);
    }

    @Override
    public String toString() {
        return "Bill{" +
            "id=" + id +
            ", invoiceId=" + invoiceId +
            ", pdf='" + Arrays.toString(pdf) + '\'' +
            ", paidAt=" + paidAt +
            ", totalCost=" + totalCost +
            ", reservationStartedAt='" + reservationStartedAt + '\'' +
            ", servedTables='" + servedTables + '\'' +
            '}';
    }
}
