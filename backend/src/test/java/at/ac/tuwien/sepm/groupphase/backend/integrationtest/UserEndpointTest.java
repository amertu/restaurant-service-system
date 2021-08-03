package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserService userService;

    private final String endpoint = "/api/v1/users";

    private ApplicationUser user = ApplicationUser.ApplicationUserBuilder.aUser()
        .withAdmin(true)
        .withEmail(TEST_USER_EMAIL)
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withSsnr(TEST_USER_SSNR)
        .withPassword(TEST_USER_PASSWORD)
        .withBlocked(TEST_USER_BLOCKED)
        .buildApplicationUser();

    private UserLoginDto userLoginDto = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto()
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withBlocked(TEST_USER_BLOCKED)
        .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        user = ApplicationUser.ApplicationUserBuilder.aUser()
            .withAdmin(true)
            .withEmail(TEST_USER_EMAIL)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withSsnr(TEST_USER_SSNR)
            .withPassword(TEST_USER_PASSWORD)
            .withBlocked(TEST_USER_BLOCKED)
            .buildApplicationUser();
    }


    @Test
    public void getListOfWaiters_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(endpoint+"/waiters")
            .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void blockWithBlockedFalse_ShouldReturn1() throws Exception {
        userRepository.save(user);
        ApplicationUser savedUser = userRepository.getUserByEmail(TEST_USER_EMAIL);
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put(endpoint+"/changeUserBlockedValue/"+savedUser.getId())
            .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(savedUser.toJson());
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content()
            .string("1"))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void blockWithBlockedFalse_ShouldThrowBadRequest() throws Exception {
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put(endpoint+"/changeUserBlockedValue/"+user.getId())
                .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"test\": 1}");
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void blockWithBlockedFalse_ShouldThrowNotFound() throws Exception {
        userRepository.save(user);
        ApplicationUser savedUser = userRepository.getUserByEmail(TEST_USER_EMAIL);
        savedUser.setId(-1L);
        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put(endpoint+"/changeUserBlockedValue/"+savedUser.getId())
                .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(savedUser.toJson());
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void creatingUser_then201() throws Exception {

        UserRegisterDto userRegisterDto = userMapper.applicationUserToUserRegisterDto(user);
        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void creatingUserWithInvalidParams_then404() throws Exception {

        UserRegisterDto userRegisterDto = new UserRegisterDto();
        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(USER_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenNoOneLoggedIn_post_then401() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_BASE_URI))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void givenAdminLoggedIn_putUserPassword_then200() throws Exception {
        user.setPassword("oldPassword");
        userRepository.save(user);
        ApplicationUser savedUser = userRepository.getUserByEmail(user.getEmail());

        //construct dummy userRegisterDto to pass validation
        UserRegisterDto userRegisterDto = userMapper.applicationUserToUserRegisterDto(user);
        userRegisterDto.setPassword("newPassword");
        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI + "/" + savedUser.getId() + "/passwordReset")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void givenUserLoggedIn_putUserPassword_then403() throws Exception {
        user.setPassword("oldPassword");
        userRepository.save(user);
        ApplicationUser savedUser = userRepository.getUserByEmail(user.getEmail());
        Long userId = savedUser.getId();
        UserRegisterDto userRegisterDto = userMapper.applicationUserToUserRegisterDto(user);
        userRegisterDto.setPassword("newPassword");
        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI + "/" + userId + "/passwordReset")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void givenAdminLoggedIn_whenPutPasswordByNonExistingId_then404() throws Exception {
        user.setPassword("oldPassword");
        userRepository.save(user);
        ApplicationUser savedUser = userRepository.getUserByEmail(user.getEmail());
        Long nonExistingUserId = savedUser.getId() + 111;

        //construct dummy userRegisterDto to pass validation
        UserRegisterDto userRegisterDto = userMapper.applicationUserToUserRegisterDto(user);
        userRegisterDto.setPassword("newPassword");
        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = this.mockMvc.perform(put(USER_BASE_URI + "/" + nonExistingUserId + "/passwordReset")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void whenPasswordChanged_loginWithOldPassword_then401() throws Exception {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        user.setPassword(oldPassword);
        userLoginDto.setPassword(user.getPassword());
        userRepository.save(user);
        ApplicationUser savedUser = userRepository.getUserByEmail(user.getEmail());

        String loginBody = objectMapper.writeValueAsString(userLoginDto);

        UserRegisterDto userRegisterDto = userMapper.applicationUserToUserRegisterDto(user);
        userRegisterDto.setPassword(newPassword);
        String changePwBody = objectMapper.writeValueAsString(userRegisterDto);

        this.mockMvc.perform(put(USER_BASE_URI + "/" + savedUser.getId() + "/passwordReset")
            .contentType(MediaType.APPLICATION_JSON)
            .content(changePwBody)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)));

        MvcResult mvcResult = this.mockMvc.perform(post(AUTHENTICATION_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginBody))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
}