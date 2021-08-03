package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.validation.ReservationSameDayConstraint;
import at.ac.tuwien.sepm.groupphase.backend.validation.ReservationStartBeforeEndDateConstraint;
import at.ac.tuwien.sepm.groupphase.backend.validation.ReservationSufficientSeatCapacityAtSelectedTablesConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@ReservationStartBeforeEndDateConstraint
@ReservationSameDayConstraint
@ReservationSufficientSeatCapacityAtSelectedTablesConstraint
public class Reservation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The name of the guest must not be blank.")
    @Column(nullable = false, length = 100)
    @Size(min = 1, max=99, message = "The length of the guest name must be between 1 and 99 characters.")
    private String guestName;

    @Min(value = 1, message="At least 1 guest is required.")
    @NotNull(message = "The number of guests must be specified.")
    private Integer numberOfGuests;

    @Size(min=0, max=254, message="The contact information must not exceed 254 characters.")
    @Column(length = 255)
    private String contactInformation;

    @Size(min=0, max=254, message="The contact information must not exceed 254 characters.")
    @Column(length = 255)
    private String comment;

    @NotNull( message = "The start date must not be null.")
    // @FutureOrPresent( message = "The start date must be in the present or future.")
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @NotNull( message = "The end date must not be null.")
    // @FutureOrPresent( message = "The end date must be in the present or future.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;


    @NotEmpty( message = "The reservation must contain tables.")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "contains_tables",
        joinColumns = @JoinColumn(name = "RESERVATION_ID"),
        inverseJoinColumns = @JoinColumn(name = "RESTAURANT_TABLE_ID"))
    Set<RestaurantTable> restaurantTables;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @Override
    public String toString() {
        return this.toStringForLogging();
    }

    public String toStringForLogging() {

        return "Reservation{" +
            "id=" + id +
            ", guestName='" + guestName + '\'' +
            ", numberOfGuests=" + numberOfGuests +
            ", contactInformation='" + contactInformation + '\'' +
            ", comment='" + comment + '\'' +
            ", startDateTime=" + startDateTime +
            ", endDateTime=" + endDateTime +
            ", restaurantTables=" + restaurantTables +
            ", createdAt=" + createdAt +
            '}';
    }


    public String toUserFriendlyString(){

        String tablesAsString = null;

        if(null != restaurantTables) {
            List<String> tables = new ArrayList<String>();
            for (RestaurantTable t : restaurantTables) {
                tables.add(" " + t.getTableNum() + " ");
            }
            tablesAsString = String.join(",", tables);
        }

        return "[" +
            "guestName='" + guestName + '\'' +
            ", numberOfGuests=" + numberOfGuests +
            ", begin=" + startDateTime +
            ", end=" + endDateTime +
            ", contactInformation='" + contactInformation + '\'' +
            ", comment='" + comment + '\'' +
            ", tables=(" + tablesAsString + ")" +
            "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(guestName, that.guestName) &&
            Objects.equals(numberOfGuests, that.numberOfGuests) &&
            Objects.equals(contactInformation, that.contactInformation) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(startDateTime, that.startDateTime) &&
            Objects.equals(endDateTime, that.endDateTime) &&
            Objects.equals(restaurantTables, that.restaurantTables) &&
            Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guestName, numberOfGuests, contactInformation, comment, startDateTime, endDateTime, restaurantTables, createdAt);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<RestaurantTable> getRestaurantTables() {
        return restaurantTables;
    }

    public void setRestaurantTables(Set<RestaurantTable> restaurantTables) {
        this.restaurantTables = restaurantTables;
    }



    public static final class ReservationBuilder {
        private Long id;
        String guestName;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        // TODO a field like 'finalizationDateTime' (Zeitpunkt der Abrechnung) might be required to handle the case, that people reserved a table from time X to time Y, but are still sitting at the table

        private Integer numberOfGuests;

        private String contactInformation;

        private String comment;

        Set<RestaurantTable> restaurantTables;

        private ReservationBuilder() {
        }

        public static Reservation.ReservationBuilder aReservation() {
            return new Reservation.ReservationBuilder();
        }

        public Reservation.ReservationBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Reservation.ReservationBuilder withGuestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public Reservation.ReservationBuilder withNumberOfGuests(Integer numberOfGuests){
            this.numberOfGuests = numberOfGuests;
            return this;
        }

        public Reservation.ReservationBuilder withContactInformation(String contactInformation){
            this.contactInformation = contactInformation;
            return this;
        }

        public Reservation.ReservationBuilder withComment(String comment){
            this.comment = comment;
            return this;
        }

        public Reservation.ReservationBuilder withStartDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public Reservation.ReservationBuilder withEndDateTime(LocalDateTime endDateTime) {
            this.endDateTime = endDateTime;
            return this;
        }

        public Reservation.ReservationBuilder withTables(Set<RestaurantTable> restaurantTables){
            this.restaurantTables = restaurantTables;
            return this;
        }

        public Reservation build() {
            Reservation reservation = new Reservation();
            reservation.setId(id);
            reservation.setGuestName(guestName);
            reservation.setNumberOfGuests(numberOfGuests);
            reservation.setContactInformation(contactInformation);
            reservation.setComment(comment);
            reservation.setStartDateTime(startDateTime);
            reservation.setEndDateTime(endDateTime);
            reservation.setRestaurantTables(restaurantTables);
            return reservation;
        }
    }


}
