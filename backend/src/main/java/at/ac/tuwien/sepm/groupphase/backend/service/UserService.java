package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.BlockedUserException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <p>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     * @throws BlockedUserException is thrown if the user is blocked
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find a application user based on the email address
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * if everything is ok: send to repository
     * else throw new ServiceException
     *
     * @param user the user to be registered
     * @return the registered user
     */
    void registerNewUser(ApplicationUser user) throws ValidationException;

    /**
     * Find all users
     *
     * @return ordered list of al user entries
     */
    List<ApplicationUser> getAllUsers();

    /**
     * This method will be used for the overview of waiters
     * @return returns all saved users
     */
    List<ApplicationUser> getAllWaiters();

    /**
     * This method changes the password of the user with a specified id.
     *
     * @param id marks the user where the password should be changed.
     * @param password the new password of the user.
     * @return the user that has been changed.
     */
    int updatePassword(Long id, String password);

    /**
     * This method blocks or unblocks a user with the specified id
     *
     * @param id the user that should be blocked/unblocked
     * @param blocked the new value
     * @throws ValidationException will be thrown if a user tries to block himself.
     * @return
     */
    int updateBlockedById(Long id, Boolean blocked) throws ValidationException;

    /**
     * Deleting a user
     *
     * @param userToDelete    email of the user to be deleted
     * @param adminPerforming the admin wanting to delete the user
     * @throws ValidationException will be thrown if a user tries to delete himself
     * @return the deleted user
     */
    ApplicationUser deleteAsAdminUser(String userToDelete, String adminPerforming) throws ValidationException;

    /**
     * Update an existing user with new information
     *
     * @param user  the user with new information
     * @throws ValidationException will be thrown if an admin tries to change his own admin status
     */
    void updateUser(ApplicationUser user) throws ValidationException;

    /**
     * Find a application user based on the id
     *
     * @param id the user id
     * @return an application user
     */
    ApplicationUser findApplicationUserById(Long id);

}
