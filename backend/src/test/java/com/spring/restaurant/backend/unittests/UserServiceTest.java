package com.spring.restaurant.backend.unittests;

import com.spring.restaurant.backend.basetest.TestData;
import com.spring.restaurant.backend.entity.ApplicationUser;
import com.spring.restaurant.backend.exception.NotFoundException;
import com.spring.restaurant.backend.exception.UserExistException;
import com.spring.restaurant.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest implements TestData {

    @Autowired private UserService userService;

    @BeforeEach
    public void setup() {
        userService.deleteAllUsers();
    }

    @Test
    public void whenSaveUser_withOutPassword_gives() {

        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail("first@email.com")
            .withPassword("")
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();

        assertThrows(ValidationException.class, () -> userService.registerNewUser(applicationUser));
    }

    @Test
    public void whenSaveUser_withOutGiving_email() {

        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail("")
            .withPassword(TEST_USER_PASSWORD)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();

        assertThrows(ValidationException.class, () -> userService.registerNewUser(applicationUser));
    }

    @Test
    public void whenGetEmailByeUser_NotFound_ShouldThrowNotFoundException() throws NotFoundException, ValidationException {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail("second@email.com")
            .withPassword(TEST_USER_PASSWORD)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();

        userService.registerNewUser(applicationUser);
        assertAll(
            () -> assertThrows(UserExistException.class, () -> userService.registerNewUser(applicationUser)),
            () -> assertThrows(NotFoundException.class, () -> userService.findApplicationUserByEmail(TEST_USER_INVALID_EMAIL))
        );
    }

    @Test
    public void WhenDeleteAdmin_AsSameAdmin_ShouldThrowValidationException() throws ValidationException {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail(ADMIN_USER)
            .withPassword(TEST_USER_PASSWORD)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(true)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .buildApplicationUser();
        userService.registerNewUser(applicationUser);
        assertThrows(ValidationException.class, () -> userService.deleteAsAdminUser(ADMIN_USER,ADMIN_USER));
    }

}
