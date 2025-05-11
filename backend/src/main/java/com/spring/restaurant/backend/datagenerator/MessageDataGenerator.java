package com.spring.restaurant.backend.datagenerator;

import com.spring.restaurant.backend.entity.Message;
import com.spring.restaurant.backend.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class MessageDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 7;

    private final MessageRepository messageRepository;

    public MessageDataGenerator(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (!messageRepository.findAll().isEmpty()) {
            LOGGER.debug("message already generated");
        } else {
            LOGGER.debug("generating {} message entries", NUMBER_OF_MESSAGES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
                Message message = generateMessage(i);
                LOGGER.debug("saving message {}", message);
                messageRepository.save(message);
            }
        }
    }

    private Message generateMessage(int i) {
        switch (i) {
            case 0:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Coronavirus")
                    .withSummary("Information on the Coronavirus Policy")
                    .withText("Everyone has to wear a mask. Keep a distance of at least a table between different customer groups.")
                    .withPublishedAt(LocalDateTime.of(2020, 5, 15, 8, 0))
                    .build();
            case 1:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Reopen")
                    .withSummary("The restaurant will reopen in a couple days")
                    .withText("The restaurant will be reopened starting on monday next week.")
                    .withPublishedAt(LocalDateTime.of(2020, 5, 11, 8, 0))
                    .build();
            case 2:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Temporary Closure")
                    .withSummary("The restaurant will be temporary closed")
                    .withText("The restaurant will be closed starting today and for the near future because of COVID-19. Reopen will take place when it's allowed again.")
                    .withPublishedAt(LocalDateTime.of(2020, 3, 16, 8, 0))
                    .build();
            case 3:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Birthday Party")
                    .withSummary("Upcoming birthday guest next week")
                    .withText("Next Tuesday we will host a birthday party for the Mason family. Their son Timmy will be 12 years old. They also asked for a cake and Steven should organize that.")
                    .withPublishedAt(LocalDateTime.of(2020, 2, 4, 8, 0))
                    .build();
            case 4:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Broken Dishwasher")
                    .withSummary("Broken Dishwasher")
                    .withText("One of the dishwashers is broken and the technician will come and repair it on Friday. Until then be careful with the other ones.")
                    .withPublishedAt(LocalDateTime.of(2020, 1, 30, 8, 0))
                    .build();
            case 5:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Happy New Year!")
                    .withSummary("A new year a new chance")
                    .withText("I hope you enjoyed your holidays. I expect all of you to be on time tomorrow. Let's do this!")
                    .withPublishedAt(LocalDateTime.of(2020, 1, 1, 8, 0))
                    .build();
            case 6:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Winter Holidays")
                    .withSummary("Today's the last day")
                    .withText("Merry Christmas! Today we will only be open until 4 o'clock. We had a very profitable year, so the restaurant will stay closed until Janurary 2nd. Come to me after your shift to get your individual bonus check.")
                    .withPublishedAt(LocalDateTime.of(2019, 12, 24, 8, 0))
                    .build();
            default:
                return Message.MessageBuilder.aMessage()
                    .withTitle("Default")
                    .withSummary("Default")
                    .withText("Default announcement.")
                    .withPublishedAt(LocalDateTime.now().minusMonths(i))
                    .build();
        }
    }
}
