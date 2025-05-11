package com.spring.restaurant.backend.endpoint.mapper;

import com.spring.restaurant.backend.endpoint.dto.DetailedMessageDto;
import com.spring.restaurant.backend.endpoint.dto.MessageInquiryDto;
import com.spring.restaurant.backend.endpoint.dto.SimpleMessageDto;
import com.spring.restaurant.backend.entity.Message;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Named("simpleMessage")
    SimpleMessageDto messageToSimpleMessageDto(Message message);

    /**
     * This is necessary since the SimpleMessageDto misses the text property and the collection mapper can't handle
     * missing fields
     **/
    @IterableMapping(qualifiedByName = "simpleMessage")
    List<SimpleMessageDto> messageToSimpleMessageDto(List<Message> message);

    DetailedMessageDto messageToDetailedMessageDto(Message message);

    Message detailedMessageDtoToMessage(DetailedMessageDto detailedMessageDto);

    Message messageInquiryDtoToMessage(MessageInquiryDto messageInquiryDto);

    MessageInquiryDto messageToMessageInquiryDto(Message message);
}

