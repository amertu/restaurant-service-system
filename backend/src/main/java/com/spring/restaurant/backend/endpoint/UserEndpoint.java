package com.spring.restaurant.backend.endpoint;

import com.spring.restaurant.backend.endpoint.dto.ApplicationUserDto;
import com.spring.restaurant.backend.endpoint.dto.UserRegisterDto;
import com.spring.restaurant.backend.endpoint.mapper.UserMapper;
import com.spring.restaurant.backend.entity.ApplicationUser;
import com.spring.restaurant.backend.exception.NotFoundException;
import com.spring.restaurant.backend.exception.UserExistException;
import com.spring.restaurant.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
@Secured("ROLE_ADMIN")
@Slf4j
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // TODO: this should be renamed, since 'waiter' doesn't fit its purpose (--> getNonAdmins)
    @GetMapping(value = "/waiters")
    @Operation(summary = "Get all waiters")
    public List<UserRegisterDto> getAllWaiters() {
        LOGGER.info("GET /api/v1/users/waiters");
        try {
            List<ApplicationUser> applicationUsers = userService.getAllWaiters();
            List<UserRegisterDto> userRegisterDtos = new LinkedList<>();
            for (ApplicationUser user : applicationUsers) {
                userRegisterDtos.add(userMapper.applicationUserToUserRegisterDto(user));
            }
            return userRegisterDtos;
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/changeUserBlockedValue/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change the blocked flag of a user")
    public int changeUserBlockedValue(@PathVariable("id") Long id, @Valid @RequestBody UserRegisterDto blockedUser) {
        LOGGER.info("PUT /api/v1/users/changeUserBlockedValue/{}", id);
        try {
            return userService.updateBlockedById(id, blockedUser.getBlocked());
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new user")
    public void registerNewUser(@RequestBody UserRegisterDto userRegisterDto) {
        LOGGER.info("POST /api/v1/users body: {}", userRegisterDto);
        try {
            userService.registerNewUser(userMapper.userRegisterToApplicationUser(userRegisterDto));
        } catch (UserExistException | ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "Error during creating user with ID: " + userRegisterDto.getId() + ". " + e.getMessage());
        }

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "Get all users")
    public List<UserRegisterDto> getAllUsers() {
        LOGGER.info("GET /api/v1/users");
        try {
            List<ApplicationUser> applicationUsers = userService.getAllUsers();
            List<UserRegisterDto> userRegisterDtos = new LinkedList<>();
            for (ApplicationUser user : applicationUsers) {
                userRegisterDtos.add(userMapper.applicationUserToUserRegisterDto(user));
            }
            return userRegisterDtos;
        } catch (NotFoundException e) {
            throw new ResponseStatusException((HttpStatus.NOT_FOUND), e.getMessage());
        }
    }

    @DeleteMapping(value = "/{email}")
    @Operation(summary = "Delete user by E-mail")
    public UserRegisterDto deleteUserAsAdminByEmail(@Valid @PathVariable("email") String userToDelete,
                                                    @RequestParam(value = "userPerformingAction", required = false) String emailAdmin) {
        LOGGER.info("DELETE /api/v1/users/{}", userToDelete);

        try {
            try {
                return userMapper.applicationUserToUserRegisterDto(userService.deleteAsAdminUser(userToDelete, emailAdmin));
            } catch (ValidationException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            }
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Can't Delete not existing user! No User with giving email matched in database.");
        }
    }

    @PutMapping(value = "/{id}/passwordReset")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reset password of a user")
    public int resetUserPassword(@PathVariable("id") Long id, @Valid @RequestBody UserRegisterDto user) {
        LOGGER.info("PUT /api/v1/users/{}/passwordReset", id);
        LOGGER.info(user.toString());
        try {
            return userService.updatePassword(id, user.getPassword());
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Operation(summary = "Update user by Id")
    public void updateUser(@RequestBody ApplicationUserDto user) {
        LOGGER.info("PUT /api/v1/user" + " message body: {}", user);
        try {
            userService.updateUser(userMapper.applicationUserDtoToApplicationUser(user));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{userId}")
    @Operation(summary = "Get user by Id")
    public ApplicationUserDto getUserById(@Valid @PathVariable("userId") Long id) {
        LOGGER.info("GET /api/v1/user/{}", id);
        ApplicationUser searchResult = userService.findApplicationUserById(id);
        return userMapper.applicationUserToApplicationUserDto(searchResult);
    }
}
