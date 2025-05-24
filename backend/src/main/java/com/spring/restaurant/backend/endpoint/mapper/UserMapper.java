package com.spring.restaurant.backend.endpoint.mapper;

import com.spring.restaurant.backend.endpoint.dto.ApplicationUserDto;
import com.spring.restaurant.backend.endpoint.dto.UserRegisterDto;
import com.spring.restaurant.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * This method converts an applicationUser Entity in a UserRegister DTO.
     *
     * @param applicationUser the entity which has to be converted.
     * @return the converted DTO.
     */
    UserRegisterDto applicationUserToUserRegisterDto(ApplicationUser applicationUser);

    /**
     * This method converts a userRegister DTO in an applicationUser Entity.
     *
     * @param userRegisterDto the DTO, which has to be converted.
     * @return the converted Entity.
     */
    ApplicationUser userRegisterToApplicationUser(UserRegisterDto userRegisterDto);

    /**
     * This method adds the content of a List<ApplicationUser> to a List<ApplicationUserDto>
     *
     * @param users the list of Entity.
     * @return the list of DTO.
     */
    List<ApplicationUserDto> userToApplicationUserDto(List<ApplicationUser> users);

    /**
     * This method converts an ApplicationUserDto in ApplicationUser.
     *
     * @param userDto the DTO of the User.
     * @return the user.
     */
    ApplicationUser applicationUserDtoToApplicationUser(ApplicationUserDto userDto);

    /**
     * This method converts an ApplicationUser in ApplicationUserDto.
     *
     * @param user the user.
     * @return the DTO of the user.
     */
    ApplicationUserDto applicationUserToApplicationUserDto(ApplicationUser user);

}
