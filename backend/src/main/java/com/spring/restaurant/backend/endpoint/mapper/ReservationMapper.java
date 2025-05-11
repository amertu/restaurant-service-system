package com.spring.restaurant.backend.endpoint.mapper;

import com.spring.restaurant.backend.endpoint.dto.ReservationDto;
import com.spring.restaurant.backend.entity.Reservation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationDto reservationToReservationDto(Reservation reservation);

    List<ReservationDto> reservationToReservationDto(List<Reservation> reservations);

    Reservation reservationDtoToReservation(ReservationDto reservationDto);

}
