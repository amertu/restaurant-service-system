package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.BillDto;
import com.spring.restaurant.backend.endpoint.dto.PurchaseDto;
import com.spring.restaurant.backend.endpoint.mapper.BillMapper;
import com.spring.restaurant.backend.endpoint.mapper.DishMapper;
import com.spring.restaurant.backend.endpoint.mapper.PurchaseMapper;
import com.spring.restaurant.backend.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/v1/bills")
@Tag(name = "Bills")
@Secured("ROLE_ADMIN")
@Slf4j
public class BillEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BillService billService;
    private final PurchaseMapper purchaseMapper;
    private final DishMapper dishMapper;
    private final BillMapper billMapper;

    @Autowired
    public BillEndpoint(BillService billService, PurchaseMapper purchaseMapper, DishMapper dishMapper, BillMapper billMapper) {
        this.billService = billService;
        this.purchaseMapper = purchaseMapper;
        this.dishMapper = dishMapper;
        this.billMapper = billMapper;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{invoiceNumber}")
    @Operation(summary = "Buy Dishes with invoiceNumber")
    public List<PurchaseDto> buyDishes(@RequestBody BillDto billDto, @PathVariable("invoiceNumber") String invoiceId) {
        LOGGER.info("PUT /api/v1/bills/{}", invoiceId);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        String reservationStartedAt = null;
        String servedTables = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        try {
            return purchaseMapper.purchasesToPurchasesDto(billService.buyDishes(username, billMapper.billDtoToBill(billDto)));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "Get all Bills")
    public List<BillDto> getAllBills() {
        LOGGER.info("GET /api/v1/bills");
        LOGGER.info("" + billMapper.billsToBillDtos(billService.getAllBills()));
        return billMapper.billsToBillDtos(billService.getAllBills());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{invoiceNumber}")
    @Operation(summary = "Get bill by invoice number")
    public BillDto getBillByInvoiceNumber(@PathVariable("invoiceNumber") Long invoiceId) {
        LOGGER.info("GET /api/v1/bills/{}", invoiceId);
        return billMapper.billToBillDto(billService.getBillByInvoiceId(invoiceId));
    }
}
