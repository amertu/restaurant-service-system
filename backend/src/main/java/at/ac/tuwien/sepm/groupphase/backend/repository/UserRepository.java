package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    /**
     * This method will be used for login. It will find an user by it E-Mail address.
     *
     * @param email will be used to search for user.
     * @return the searched user.
     */
    @Query("SELECT a FROM ApplicationUser a WHERE a.email = :email")
    ApplicationUser getUserByEmail(@Param("email") String email);

    /**
     * This method find a user by Social Security Number.
     * @param ssnr will be used to search for user.
     * @return the searched user.
     */
    @Query("SELECT a FROM ApplicationUser a WHERE a.ssnr = :ssnr")
    ApplicationUser getUserBySsnr(@Param("ssnr") Long ssnr);

    @Query("SELECT a FROM ApplicationUser a WHERE a.id = :id")
    ApplicationUser getUserById(@Param("id") Long id);

    /**
     * This method will be used for the overview of waiters
     * @return returns all saved users
     */
    List<ApplicationUser> getAllByAdminFalse();

    /**
     * This method will update a user's password.
     * @param id the id of the user that will be changed.
     * @param password the new password of the user.
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ApplicationUser user SET user.password = :password WHERE user.id = :id")
    int updateUserPasswordById(@Param("id") Long id, @Param("password") String password);

    /**
     * This method will set a user's blocked value to the specified value
     * @param id the id of a specific user
     * @param blocked the new value of blocked
     * @return should be 1 if executed correctly
     *
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ApplicationUser user SET user.blocked = :blocked WHERE user.id = :id")
    int updateBlockedById(@Param("id") Long id, @Param("blocked") boolean blocked);

    /**
     * This method will update an existing user with new information.
     *
     * @param id     of the existing user in the database.
     * @param firstName new firstName.
     * @param lastName  new lastName.
     * @param ssnr    new ssnr.
     */
    @Modifying
    @Transactional
    @Query("Update ApplicationUser user SET user.firstName = :firstName, user.lastName = :lastName, " +
        "user.ssnr = :ssnr, user.admin = :admin  WHERE user.id = :id")
    void updateUser(@Param("id") Long id, @Param("firstName") String firstName,
                    @Param("lastName") String lastName, @Param("ssnr") Long ssnr, @Param("admin") boolean admin);

}
