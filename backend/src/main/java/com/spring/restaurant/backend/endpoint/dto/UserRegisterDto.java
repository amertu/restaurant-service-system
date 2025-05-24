package com.spring.restaurant.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class UserRegisterDto {
    @NotNull(message = "Id must not be null")
    private Long id;

    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    @NotNull(message = "Password must not be null")
    private String password;

    @NotNull(message = "FirstName must not be null")
    private String firstName;

    @NotNull(message = "LastName must not be null")
    private String lastName;

    @NotNull(message = "Ssnr must not be null")
    private Long ssnr;

    private Boolean admin;

    private Boolean blocked;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public Long getSsnr() { return ssnr; }

    public void setSsnr(Long ssnr) { this.ssnr = ssnr; }

    public Boolean getAdmin() { return admin; }

    public void setAdmin(Boolean admin) { this.admin = admin; }

    public Boolean getBlocked() { return blocked; }

    public void setBlocked(Boolean blocked) { this.blocked = blocked; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegisterDto that = (UserRegisterDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(email, that.email) &&
            Objects.equals(password, that.password) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(ssnr, that.ssnr) &&
            Objects.equals(admin, that.admin) &&
            Objects.equals(blocked, that.blocked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, firstName, lastName, ssnr, admin, blocked);
    }

    @Override
    public String toString() {
        return "UserRegisterDto{" +
            "id=" + id +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", ssnr=" + ssnr +
            ", admin=" + admin +
            ", blocked=" + blocked +
            '}';
    }
}
