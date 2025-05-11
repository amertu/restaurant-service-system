package com.spring.restaurant.backend.validation;

import com.spring.restaurant.backend.entity.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;

@Component
public class UserValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateRegistration(ApplicationUser applicationUser) throws ValidationException {
        if (applicationUser.getFirstName() == null || applicationUser.getFirstName().isEmpty()) {
            LOGGER.error("First Name is required!");
            throw new ValidationException("First Name is required!");
        }

        if (applicationUser.getLastName() == null || applicationUser.getLastName().isEmpty()) {
            LOGGER.error("Last Name is required!");
            throw new ValidationException("Last Name is required!");
        }

        if (applicationUser.getEmail() == null || applicationUser.getEmail().isEmpty()) {
            LOGGER.error("Email is required!");
            throw new ValidationException("Email is required!");
        }

        if (applicationUser.getPassword() == null || applicationUser.getPassword().isEmpty()) {
            LOGGER.error("Password is required!");
            throw new ValidationException("Password is required!");
        }

        if (applicationUser.getSsnr() == null || applicationUser.getSsnr().toString().isEmpty()) {
            LOGGER.error("Social Security Number is required!");
            throw new ValidationException("Social Security Number is required!");
        }
    }

}
