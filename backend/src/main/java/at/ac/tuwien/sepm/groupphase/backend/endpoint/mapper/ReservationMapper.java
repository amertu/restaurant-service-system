package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ReservationMapper {

    ReservationDto reservationToReservationDto(Reservation reservation);

    List<ReservationDto> reservationToReservationDto(List<Reservation> reservations);

    Reservation reservationDtoToReservation(ReservationDto reservationDto);

}
