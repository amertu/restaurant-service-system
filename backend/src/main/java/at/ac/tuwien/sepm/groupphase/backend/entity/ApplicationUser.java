package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Objects;

@Entity
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean admin;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private Long ssnr;

    @Email
    @Column(unique = true)
    private String email;

    private String password;

    private boolean blocked;

    public ApplicationUser() {
    }

    public ApplicationUser(Long id, boolean admin, String email, String password, String firstName, String lastName, Long ssnr, boolean blocked) {
        this.id = id;
        this.admin = admin;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssnr = ssnr;
        this.blocked = blocked;

    }

    public boolean isAdmin() {
        return admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getSsnr() {
        return ssnr;
    }

    public void setSsnr(Long ssnr) {
        this.ssnr = ssnr;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean getBlocked() {
        return blocked;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationUser that = (ApplicationUser) o;
        return admin == that.admin &&
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(ssnr, that.ssnr) &&
            Objects.equals(email, that.email) &&
            Objects.equals(password, that.password) &&
            Objects.equals(blocked, that.blocked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, admin, firstName, lastName, ssnr, email, password, blocked);
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
            "id=" + id +
            ", admin=" + admin +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", ssnr=" + ssnr +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", blocked='" + blocked + '\'' +
            '}';
    }

    public String toJson() {
        return "{\"id\": " + id +
            ",\"admin\": " + admin +
            ",\"firstName\": " + "\"" + firstName + "\"" +
            ",\"lastName\": " + "\"" + lastName + "\"" +
            ",\"ssnr\": " + ssnr +
            ",\"email\": " + "\"" + email + "\"" +
            ",\"password\": " + "\"" + password + "\"" +
            ",\"blocked\": " + blocked + "}";
    }

    public static final class ApplicationUserBuilder {
        private Long id;
        private boolean admin;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private Long ssnr;
        private boolean blocked;


        public ApplicationUserBuilder() {
            // Empty constructor
        }

        public static ApplicationUserBuilder aUser() {
            return new ApplicationUserBuilder();
        }


        public ApplicationUserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ApplicationUserBuilder withAdmin(boolean admin) {
            this.admin = admin;
            return this;
        }

        public ApplicationUserBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }


        public ApplicationUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }


        public ApplicationUserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }


        public ApplicationUserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public ApplicationUserBuilder withSsnr(Long ssnr) {
            this.ssnr = ssnr;
            return this;
        }

        public ApplicationUserBuilder withBlocked(boolean blocked) {
            this.blocked = blocked;
            return this;
        }


        public ApplicationUser buildApplicationUser() {
            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setId(id);
            applicationUser.setEmail(email);
            applicationUser.setPassword(password);
            applicationUser.setFirstName(firstName);
            applicationUser.setLastName(lastName);
            applicationUser.setAdmin(admin);
            applicationUser.setSsnr(ssnr);
            applicationUser.setBlocked(blocked);
            return applicationUser;
        }
    }


}
