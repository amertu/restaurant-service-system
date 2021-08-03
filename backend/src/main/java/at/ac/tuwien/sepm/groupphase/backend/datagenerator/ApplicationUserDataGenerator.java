package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class ApplicationUserDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_USERS_TO_GENERATE = 10;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    private final UserRepository userRepository;

    public ApplicationUserDataGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (!userRepository.findAll().isEmpty()) {
            LOGGER.debug("users already generated");
        } else {
            LOGGER.debug("generating {} user accounts", NUMBER_OF_USERS_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_USERS_TO_GENERATE; i++) {
                ApplicationUser applicationUser = generateUser(i);
                LOGGER.debug("saving user {}", applicationUser);
                userRepository.save(applicationUser);
            }
        }
    }

    private ApplicationUser generateUser(int i) {
        switch (i) {
            case 0:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("invisible@ghost.org")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Spooky")
                    .withAdmin(true)
                    .withLastName("Ghost")
                    .withSsnr(1002010100L)
                    .withBlocked(false)
                    .buildApplicationUser();
            case 1:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("admin@mail.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Admin")
                    .withAdmin(true)
                    .withLastName("Tester")
                    .withSsnr(1234010169L)
                    .withBlocked(false)
                    .buildApplicationUser();
            case 2:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("user@mail.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("User")
                    .withAdmin(false)
                    .withLastName("Tester")
                    .withSsnr(2345010190L)
                    .withBlocked(false)
                    .buildApplicationUser();
            case 3:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("laurenEm1ly@gmail.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Emily")
                    .withAdmin(false)
                    .withLastName("Lauren")
                    .withSsnr(2468061289L)
                    .withBlocked(false)
                    .buildApplicationUser();
            case 4:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("peter1985@hotmail.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Peter")
                    .withAdmin(false)
                    .withLastName("House")
                    .withSsnr(2200221085L)
                    .withBlocked(true)
                    .buildApplicationUser();
            case 5:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("koopaling5@gmx.net")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Roy")
                    .withAdmin(false)
                    .withLastName("Koopa")
                    .withSsnr(3456140275L)
                    .withBlocked(false)
                    .buildApplicationUser();
            case 6:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("under@sea.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Patrick")
                    .withAdmin(false)
                    .withLastName("Star")
                    .withSsnr(4003190784L)
                    .withBlocked(true)
                    .buildApplicationUser();
            case 7:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("steph@imanity.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Stephanie")
                    .withAdmin(false)
                    .withLastName("Dola")
                    .withSsnr(1995040602L)
                    .withBlocked(false)
                    .buildApplicationUser();
            case 8:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("stevenoman@gmail.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Steve")
                    .withAdmin(false)
                    .withLastName("Noman")
                    .withSsnr(1335241265L)
                    .withBlocked(false)
                    .buildApplicationUser();
            case 9:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("billgates@outlook.com")
                    .withPassword(passwordEncoder.encode("password"))
                    .withFirstName("Bill")
                    .withAdmin(false)
                    .withLastName("Gates")
                    .withSsnr(1460281055L)
                    .withBlocked(true)
                    .buildApplicationUser();
            default:
                return ApplicationUser.ApplicationUserBuilder.aUser()
                    .withEmail("default@mail.com")
                    .withPassword(passwordEncoder.encode("default1"))
                    .withFirstName("default")
                    .withAdmin(false)
                    .withLastName("default")
                    .withSsnr(1234567899L)
                    .withBlocked(true)
                    .buildApplicationUser();
        }
    }
}
