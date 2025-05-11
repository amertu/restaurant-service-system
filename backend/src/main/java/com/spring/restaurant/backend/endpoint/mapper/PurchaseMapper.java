package com.spring.restaurant.backend.endpoint.mapper;

import com.spring.restaurant.backend.endpoint.dto.PurchaseDto;
import com.spring.restaurant.backend.entity.Purchase;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    PurchaseDto purchaseToPurchaseDto(Purchase purchase);

    List<PurchaseDto> purchasesToPurchasesDto(List<Purchase> purchases);
}
