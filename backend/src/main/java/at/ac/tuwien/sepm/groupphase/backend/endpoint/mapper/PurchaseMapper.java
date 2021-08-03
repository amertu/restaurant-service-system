package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Purchase;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    /**
     * This Method converts a List of purchases entities in a List of purchasesDto.
     *
     * @param purchases the  List of purchases entities.
     * @return the List of purchases DTOs.
     */
    @IterableMapping(qualifiedByName = "purchases")
    List<PurchaseDto> purchasesToPurchasesDto(List<Purchase> purchases);
}
