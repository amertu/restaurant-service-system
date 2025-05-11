package com.spring.restaurant.backend.endpoint.dto;

import com.spring.restaurant.backend.entity.ApplicationUser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BillDto {
    private Long id;

    private Long invoiceId;

    private byte[] pdf;

    private LocalDateTime paidAt;

    private double totalCost;

    private ApplicationUser applicationUser;

    private List<DishDto> dishes;

    private String reservationStartedAt;

    private String servedTables;

    public BillDto() {
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

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    public List<DishDto> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishDto> dishes) {
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
        BillDto billDto = (BillDto) o;
        return Double.compare(billDto.totalCost, totalCost) == 0 &&
            Objects.equals(id, billDto.id) &&
            Objects.equals(invoiceId, billDto.invoiceId) &&
            Arrays.equals(pdf, billDto.pdf) &&
            Objects.equals(paidAt, billDto.paidAt) &&
            Objects.equals(applicationUser, billDto.applicationUser) &&
            Objects.equals(reservationStartedAt, billDto.reservationStartedAt) &&
            Objects.equals(servedTables, billDto.servedTables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, invoiceId, Arrays.hashCode(pdf), paidAt, totalCost, applicationUser, reservationStartedAt, servedTables);
    }

    @Override
    public String toString() {
        return "BillDto{" +
            "id=" + id +
            ", invoiceId=" + invoiceId +
            ", pdf='" + Arrays.toString(pdf) + '\'' +
            ", paidAt=" + paidAt +
            ", totalCost=" + totalCost +
            ", applicationUser=" + applicationUser +
            ", reservationStartedAt='" + reservationStartedAt + '\'' +
            ", servedTables='" + servedTables + '\'' +
            '}';
    }
}
