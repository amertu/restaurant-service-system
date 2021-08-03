package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserExistException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
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
    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/waiters")
    @ApiOperation(value = "Get all informations about all waiters",
        authorizations = {@Authorization(value = "apiKey")})
    public List<UserRegisterDto> getAllWaiters() {
        LOGGER.info("GET /api/v1/users/waiters");
        try {
            List<ApplicationUser> applicationUsers = userService.getAllWaiters();
            List<UserRegisterDto> userRegisterDtos = new LinkedList<>();
            for (ApplicationUser user : applicationUsers) {
                userRegisterDtos.add(userMapper.applicationUserToUserRegisterDto(user));
            }
            return userRegisterDtos;
        }
        catch (NotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/changeUserBlockedValue/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Change the blocked value of a user",
        authorizations = {@Authorization(value = "apiKey")})
    public int changeUserBlockedValue(@PathVariable Long id, @Valid @RequestBody UserRegisterDto blockedUser){
        LOGGER.info("PUT /api/v1/users/changeUserBlockedValue/{}",id);
        try {
            return userService.updateBlockedById(id, blockedUser.getBlocked());
        } catch (NotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ValidationException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new User")
    public void registerNewUser(@RequestBody UserRegisterDto userRegisterDto) {
        LOGGER.info("POST /api/v1/users body: {}", userRegisterDto);
        try {
            userService.registerNewUser(userMapper.userRegisterToApplicationUser(userRegisterDto));
        } catch (UserExistException | ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "Error during creating user with ID: " + userRegisterDto.getId() + ". " + e.getMessage());
        }

    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @ApiOperation(value = "Get all Users", authorizations = {@Authorization(value = "apiKey")})
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

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{email}")
    @ApiOperation(value = "Delete user by E-mail",
        authorizations = {@Authorization(value = "apiKey")})
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

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/{id}/passwordReset")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Reset password of a user",
        authorizations = {@Authorization(value = "apiKey")})
    public int resetUserPassword (@PathVariable("id") Long id, @Valid @RequestBody UserRegisterDto user) {
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

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @ApiOperation(value = "Update user by Id",
        authorizations = {@Authorization(value = "apiKey")})
    public void updateUser(@RequestBody ApplicationUserDto user) {
        LOGGER.info("PUT /api/v1/user" + " message body: {}", user);
        try {
            userService.updateUser(userMapper.applicationUserDtoToApplicationUser(user));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{userId}")
    @ApiOperation(value = "Get user by Id",
        authorizations = {@Authorization(value = "apiKey")})
    public ApplicationUserDto getUserById(@Valid @PathVariable("userId") Long id) {
        LOGGER.info("GET /api/v1/user/{}", id);
        ApplicationUser searchResult = userService.findApplicationUserById(id);
        return userMapper.applicationUserToApplicationUserDto(searchResult);
    }
}
