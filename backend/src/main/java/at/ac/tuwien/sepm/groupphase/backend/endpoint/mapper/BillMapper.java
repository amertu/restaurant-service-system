package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BillDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Bill;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BillMapper {

    Bill billDtoToBill(BillDto billDto);

    BillDto billToBillDto(Bill bill);

    List<BillDto> billsToBillDtos(List<Bill> billList);
}
