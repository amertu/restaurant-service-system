package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
public class UserRepositoryTest implements TestData {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenSaveUser_thenFindUserByEmailGivesTheUserBack() {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail("new@mail.com")
            .withPassword(TEST_USER_PASSWORD)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();

        userRepository.save(applicationUser);

        assertAll(
            () -> assertEquals(applicationUser, userRepository.getUserByEmail(applicationUser.getEmail())),
            () -> assertNotNull(userRepository.getUserByEmail(applicationUser.getEmail()))
        );
    }

    @Test
    public void whenSaveUser_thenUpdateUserGivesTheUserBack() {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail("first@email.com")
            .withPassword(TEST_USER_PASSWORD)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();

        userRepository.save(applicationUser);
        userRepository.updateUser(applicationUser.getId(), TEST_USER_FIRSTNAME2, TEST_USER_LASTNAME2, TEST_USER_SSNR, TEST_USER_ADMIN);

        assertAll(
            () -> assertEquals(TEST_USER_FIRSTNAME2, userRepository.getUserByEmail(applicationUser.getEmail()).getFirstName()),
            () -> assertEquals(TEST_USER_LASTNAME2, userRepository.getUserByEmail(applicationUser.getEmail()).getLastName()),
            () -> assertEquals(TEST_USER_SSNR, userRepository.getUserByEmail(applicationUser.getEmail()).getSsnr()),
            () -> assertEquals(TEST_USER_ADMIN, userRepository.getUserByEmail(applicationUser.getEmail()).getAdmin())

        );
        userRepository.delete(applicationUser);
    }

    @Test
    public void givenNothing_whenSaveMUser_thenFindUserByEmail() {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aUser()
            .withEmail("second@email.com")
            .withPassword("")
            .withFirstName(TEST_USER_FIRSTNAME)
            .withAdmin(TEST_USER_ADMIN)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR2)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();


        int size = userRepository.findAll().size();
        userRepository.save(applicationUser);

        assertAll(
            () -> assertEquals(size + 1, userRepository.findAll().size()),
            () -> assertNotNull(userRepository.getUserByEmail(applicationUser.getEmail()))
        );
    }


}
