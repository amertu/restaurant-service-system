package com.spring.restaurant.backend.endpoint.mapper;

import com.spring.restaurant.backend.endpoint.dto.BillDto;
import com.spring.restaurant.backend.entity.Bill;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BillMapper {

    Bill billDtoToBill(BillDto billDto);

    BillDto billToBillDto(Bill bill);

    List<BillDto> billsToBillDtos(List<Bill> billList);
}
