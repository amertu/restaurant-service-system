package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReservationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RestaurantTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Profile("generateData")
@Component
public class TableAndReservationDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_RESERVATIONS_TO_GENERATE = 15;
    private static final int NUMBER_OF_TABLES_TO_GENERATE = 7;
    private static final LocalDateTime TEST_START_DATE_TIME = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public TableAndReservationDataGenerator(ReservationRepository reservationRepository, RestaurantTableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
    }

    @PostConstruct
    private void generateReservations() {
        if (reservationRepository.findAll().size() > 0) {
            LOGGER.debug("reservations already generated");
        } else {
            LOGGER.debug("generating {} reservation entries", NUMBER_OF_RESERVATIONS_TO_GENERATE);
            List<RestaurantTable> createdTables = generateTables();
            for (int i = 0; i < NUMBER_OF_RESERVATIONS_TO_GENERATE; i++) {
                Reservation reservation = generateReservation(i);
                LocalDateTime start = TEST_START_DATE_TIME.withHour(12).plusDays(i / 2).plusHours((i % 2) * ((i / 3) + 2));
                if (start.isBefore(LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0))) {
                    if (LocalDateTime.now().getHour() >= 21) {
                        continue;
                    } else {
                        reservation.setStartDateTime(LocalDateTime.now().plusHours(1 + i).withMinute(0).withSecond(0).withNano(0));
                        reservation.setEndDateTime(reservation.getStartDateTime().plusHours(1));
                    }
                } else {
                    reservation.setStartDateTime(start);
                    reservation.setEndDateTime(reservation.getStartDateTime().plusHours((i % 2) == 0 ? 2 : 3));
                }
                reservation.setContactInformation(generatePhoneNumber(i));
                reservation.setComment("Comment #" + i);
                int end = 1 + (i % 2) * ((i % 5) < 2 ? 2 : 1);
                List<RestaurantTable> tables = createdTables.subList(Integer.min(i / 4, createdTables.size() - 2), Integer.min(i / 4 + end, createdTables.size() - 1));
                for (int j = tables.size() - 1; j >= 0; j--) {
                    if (!tables.get(j).getActive()) {
                        tables.remove(j);
                    }
                }
                if (tables.size() <= 0) {
                    for (int j = 0; j < NUMBER_OF_TABLES_TO_GENERATE; j++) {
                        if (tables.get(j).getActive()) {
                            tables.add(createdTables.get(j));
                            break;
                        }
                    }
                }
                int numberOfGuests = 0;
                for (int j = 0; j < tables.size(); j++) {
                    numberOfGuests += tables.get(j).getSeatCount();
                }
                reservation.setNumberOfGuests(Integer.max(1, numberOfGuests - i % 5));
                reservation.setRestaurantTables(new HashSet<>(tables));
                LOGGER.debug("saving reservation {}", reservation);
                reservationRepository.save(reservation);
            }
        }
    }

    private List<RestaurantTable> generateTables() {
        if (tableRepository.findAll().size() > 0) {
            LOGGER.debug("table of restaurant tables already generated");
        } else {
            LOGGER.debug("generating {} entries for table of restaurant tables", NUMBER_OF_TABLES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_TABLES_TO_GENERATE; i++) {
                RestaurantTable restaurantTable = generateTable(i);
                LOGGER.debug("saving restaurantTable {} to table of restaurant tables", restaurantTable);
                tableRepository.save(restaurantTable);
            }
        }
        return tableRepository.findAll();
    }

    private String generatePhoneNumber(int i) {
        String result = "+43 ";
        switch (i % 4) {
            case 0:
                result += "650 ";
                break;
            case 1:
                result += "664 ";
                break;
            case 2:
                result += "676 ";
                break;
            case 3:
                result += "699 ";
                break;
        }
        for (int j=1; j<=7; j++) {
            result += (i % 9 + j * j) % 10;
        }
        return result;
    }

    private Reservation generateReservation(int i) {
        switch (i) {
            case 0:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Michael Steinberger")
                    .build();
            case 1:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Stacy Baroow")
                    .build();
            case 2:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Dave Modler")
                    .build();
            case 3:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Jason Fyle")
                    .build();
            case 4:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Burnstins family")
                    .build();
            case 5:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Rebecca Polte")
                    .build();
            case 6:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Francis Rèich")
                    .build();
            case 7:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Stuart Bloom")
                    .build();
            case 8:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Kate Beckett")
                    .build();
            case 9:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Big Richard")
                    .build();
            case 10:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Blues Clues")
                    .build();
            case 11:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Kollins o Navelin")
                    .build();
            case 12:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Eric Zolt")
                    .build();
            case 13:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Pippi Longsocks")
                    .build();
            case 14:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Terminator")
                    .build();
            default:
                return Reservation.ReservationBuilder.aReservation()
                    .withGuestName("Default")
                    .build();
        }
    }

    private RestaurantTable generateTable(int i) {
        switch (i) {
            case 0:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(100L)
                    .withPosDescription("Near the entrance")
                    .withSeatCount(4)
                    .withActive(true)
                    .build();
            case 1:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(101L)
                    .withPosDescription("Besides the left window")
                    .withSeatCount(2)
                    .withActive(true)
                    .build();
            case 2:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(102L)
                    .withPosDescription("On the right")
                    .withSeatCount(8)
                    .withActive(true)
                    .build();
            case 3:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(103L)
                    .withPosDescription("On the right corner")
                    .withSeatCount(6)
                    .withActive(true)
                    .build();
            case 4:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(104L)
                    .withPosDescription("In the VIP room")
                    .withSeatCount(15)
                    .withActive(false)
                    .build();
            case 5:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(200L)
                    .withPosDescription("In the middle")
                    .withSeatCount(8)
                    .withActive(true)
                    .build();
            case 6:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(201L)
                    .withPosDescription("Next to the counter")
                    .withSeatCount(4)
                    .withActive(true)
                    .build();
            case 7:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(202L)
                    .withPosDescription("Between the counter and the garden")
                    .withSeatCount(3)
                    .withActive(false)
                    .build();
            case 8:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(300L)
                    .withPosDescription("In the garden front")
                    .withSeatCount(8)
                    .withActive(false)
                    .build();
            case 9:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(301L)
                    .withPosDescription("In the back of the garden")
                    .withSeatCount(8)
                    .withActive(false)
                    .build();
            default:
                return RestaurantTable.RestaurantTableBuilder.aTable()
                    .withTableNum(0L)
                    .withPosDescription("default")
                    .withSeatCount(0)
                    .withActive(false)
                    .build();
        }
    }
}
