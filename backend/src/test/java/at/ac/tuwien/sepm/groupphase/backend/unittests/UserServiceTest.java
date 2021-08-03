package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserExistException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
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

    @Autowired
    private UserService customUserDetailService;


    @Test
    public void whenSaveUser_withOutPassword_gives() {

        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail(TEST_USER_EMAIL)
            .withPassword("")
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();

        assertThrows(ValidationException.class, () -> customUserDetailService.registerNewUser(applicationUser));
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

        assertThrows(ValidationException.class, () -> customUserDetailService.registerNewUser(applicationUser));
    }

    @Test
    public void whenGetEmailByeUser_NotFound_ShouldThrowNotFoundException() throws NotFoundException, ValidationException {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();

        customUserDetailService.registerNewUser(applicationUser);
        assertAll(
            () -> assertThrows(UserExistException.class, () -> customUserDetailService.registerNewUser(applicationUser)),
            () -> assertThrows(NotFoundException.class, () -> customUserDetailService.findApplicationUserByEmail(TEST_USER_INVALID_EMAIL))
        );
    }

    @Test
    public void WhenDeleteAdmin_AsSameAdmin_ShouldThrowValidationException() throws ValidationException {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail(ADMIN_USER)
            .withPassword(TEST_USER_PASSWORD)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .buildApplicationUser();
        customUserDetailService.registerNewUser(applicationUser);
        assertThrows(ValidationException.class, () -> customUserDetailService.deleteAsAdminUser(ADMIN_USER,ADMIN_USER));
    }

}
