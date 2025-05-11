package com.spring.restaurant.backend.service.impl;

import com.spring.restaurant.backend.entity.ApplicationUser;
import com.spring.restaurant.backend.exception.BlockedUserException;
import com.spring.restaurant.backend.exception.NotFoundException;
import com.spring.restaurant.backend.exception.UserExistException;
import com.spring.restaurant.backend.repository.UserRepository;
import com.spring.restaurant.backend.service.UserService;
import com.spring.restaurant.backend.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);
            if(applicationUser.getBlocked()){
                throw new BlockedUserException("You have been blocked by an admin!");
            }
            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin())
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            else
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException | BlockedUserException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.getUserByEmail(email);
        if (applicationUser != null) return applicationUser;
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public List<ApplicationUser> getAllWaiters() {
        LOGGER.debug("Get all waiters");
        List<ApplicationUser> applicationUsers = userRepository.getAllByAdminFalse();
        if (applicationUsers != null && applicationUsers.size() > 0) return applicationUsers;
        throw new NotFoundException("There are no waiters saved in the database!");
    }

    @Override
    public void registerNewUser(ApplicationUser user)throws ValidationException {
        LOGGER.debug("Create new user with name: {} {}", user.getFirstName(), user.getLastName());
        userValidator.validateRegistration(user);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        ApplicationUser existedUserWithEmail = userRepository.getUserByEmail(user.getEmail());
        String resultMessage = " cannot be used to register because it's already exists!";
        if (existedUserWithEmail != null){
            LOGGER.warn("This Email: " + user.getEmail() + resultMessage);
            throw new UserExistException("This Email: " + user.getEmail() + resultMessage);
        }
        ApplicationUser existedUserWithSsnr = userRepository.getUserBySsnr(user.getSsnr());
        if (existedUserWithSsnr != null){
            LOGGER.warn("This Social Security Number: " + user.getSsnr() + resultMessage);
            throw new UserExistException("This Social Security Number: " + user.getSsnr() + resultMessage);
        }else {
            try {
                userRepository.save(user);
            } catch (DataIntegrityViolationException e){
                LOGGER.warn("" + e);
                throw new UserExistException(e);
            }
        }

    }

    @Override
    public List<ApplicationUser> getAllUsers() {
        LOGGER.debug("Get all users");
        return userRepository.findAll();
    }

    @Override
    public int updatePassword(Long id, String password) {
        Optional<ApplicationUser> user = userRepository.findById(id);
        if (user.isPresent()) {
            LOGGER.debug("Change the password of a user with the id {}", id);
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            String encoded = passwordEncoder.encode(password);
            return userRepository.updateUserPasswordById(id, encoded);
        } else {
            throw new NotFoundException("Could not find user with the id {}" + id);
        }
    }

    @Override
    public int updateBlockedById(Long id, Boolean blocked) throws ValidationException {
        LOGGER.debug("Changes the blocked value of a user with the id {} to {}", id,blocked);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser user = findApplicationUserById(id);
        if (auth.getPrincipal().equals(user.getEmail())) {
            throw new ValidationException("You can not block yourself.");
        }
        int edited = userRepository.updateBlockedById(id, blocked);
        if (edited == 1) return edited;
        throw new NotFoundException("There is no user with the id " + id);
    }

    @Override
    public ApplicationUser deleteAsAdminUser(String userToDelete, String adminPerforming) throws NotFoundException, ValidationException {
        LOGGER.debug("Delete user with email: {}", userToDelete);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userToDelete.equals(adminPerforming) || auth.getPrincipal().equals(userToDelete)) {
            throw new ValidationException("You can not delete yourself.");
        }

        ApplicationUser user = userRepository.getUserByEmail(userToDelete);
        if (user == null) {
            throw new NotFoundException();
        }
        userRepository.delete(user);
        return user;
    }

    @Override
    public void updateUser(ApplicationUser user) throws ValidationException {
        Long id = user.getId();
        LOGGER.debug("Update user with id: {}", id);
        ApplicationUser tmp = userRepository.findById(id).orElse(null);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (tmp != null && auth.getPrincipal().equals(tmp.getEmail())) {
            if (user.getAdmin() != tmp.getAdmin()) {
                throw new ValidationException("You can not change your own admin status.");
            }
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            user.setFirstName(tmp.getFirstName());
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            user.setLastName(tmp.getLastName());
        }
        if (user.getSsnr() == null || user.getSsnr().toString().isEmpty()) {
            user.setSsnr(tmp.getSsnr());
        }
        // TODO: tmp != null should be checked at the beginning of ths method
        if (tmp != null) {
            userRepository.updateUser(id, user.getFirstName(), user.getLastName(), user.getSsnr(), user.getAdmin());
        } else {
            throw new NotFoundException("User with id: " + id + " doesn't exists.");
        }
    }

    @Override
    public ApplicationUser findApplicationUserById(Long id) {
        LOGGER.debug("Find user with id {}", id);
        ApplicationUser applicationUser = userRepository.findById(id).orElse(null);
        if (applicationUser != null) {
            return applicationUser;
        }
        else throw new NotFoundException(String.format("Could not find user with id %s", id));
    }
}
