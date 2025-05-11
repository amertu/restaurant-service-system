package com.spring.restaurant.backend.endpoint.dto;

import java.util.Objects;

public class ApplicationUserDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean admin;
    private Long ssnr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isAdmin() { return admin; }

    public void setAdmin(boolean admin) { this.admin = admin; }

    public Long getSsnr() { return ssnr; }

    public void setSsnr(Long ssnr) { this.ssnr = ssnr; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationUserDto that = (ApplicationUserDto) o;
        return admin == that.admin &&
            Objects.equals(id, that.id) &&
            Objects.equals(email, that.email) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(ssnr, that.ssnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, admin, ssnr);
    }

    @Override
    public String toString() {
        return "ApplicationUserDto{" +
            "id=" + id +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", admin=" + admin +
            ", ssnr=" + ssnr +
            '}';
    }

    public ApplicationUserDto() {
        // Empty constructor
    }
}
