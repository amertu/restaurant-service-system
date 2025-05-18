package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.DetailedMessageDto;
import com.spring.restaurant.backend.endpoint.dto.MessageInquiryDto;
import com.spring.restaurant.backend.endpoint.dto.SimpleMessageDto;
import com.spring.restaurant.backend.endpoint.mapper.MessageMapper;
import com.spring.restaurant.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/messages")
@Tag(name = "Messages")
@Secured("ROLE_ADMIN")
@Slf4j
public class MessageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageEndpoint(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @GetMapping
    @Operation(summary = "Get list of messages without details")
    public List<SimpleMessageDto> findAll() {
        LOGGER.info("GET /api/v1/messages");
        return messageMapper.messageToSimpleMessageDto(messageService.findAll());
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new message")
    public DetailedMessageDto create(@Valid @RequestBody MessageInquiryDto messageDto) {
        LOGGER.info("POST /api/v1/messages body: {}", messageDto);
        return messageMapper.messageToDetailedMessageDto(
            messageService.publishMessage(messageMapper.messageInquiryDtoToMessage(messageDto)));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific message")
    public DetailedMessageDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/messages/{}", id);
        return messageMapper.messageToDetailedMessageDto(messageService.findOne(id));
    }
}
