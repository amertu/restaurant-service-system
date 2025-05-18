package com.spring.restaurant.backend.integrationtest;

import com.spring.restaurant.backend.basetest.TestData;
import com.spring.restaurant.backend.config.properties.SecurityProperties;
import com.spring.restaurant.backend.endpoint.dto.DishDto;
import com.spring.restaurant.backend.endpoint.mapper.DishMapper;
import com.spring.restaurant.backend.entity.Dish;
import com.spring.restaurant.backend.repository.DishRepository;
import com.spring.restaurant.backend.security.JwtTokenizer;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class DishEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Dish dish;

    @BeforeEach
    public void beforeEach() {
        dishRepository.deleteAll();
        dish = Dish.DishBuilder.aDish()
            .withName(TEST_DISH_NAME)
            .withPrice(TEST_DISH_PRICE)
            .build();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(DISH_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<DishDto> dishDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            DishDto[].class));

        assertEquals(0, dishDtos.size());
    }

    @Test
    public void givenOneDish_whenFindAll_thenListWithSizeOneAndDishWithAllProperties()
        throws Exception {
        dishRepository.save(dish);

        MvcResult mvcResult = this.mockMvc.perform(get(DISH_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<DishDto> dishDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            DishDto[].class));

        assertEquals(1, dishDtos.size());
        DishDto dishDto = dishDtos.get(0);
        assertAll(
            () -> assertEquals(dish.getId(), dishDto.getId()),
            () -> assertEquals(dish.getName(), dishDto.getName()),
            () -> assertEquals(dish.getPrice(), dishDto.getPrice())
        );
    }

    @Test
    public void givenOneDish_whenFindById_thenDishWithAllProperties() throws Exception {
        dishRepository.save(dish);

        MvcResult mvcResult = this.mockMvc.perform(get(DISH_BASE_URI + "/{id}", dish.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        DishDto dishDto = objectMapper.readValue(response.getContentAsString(),
            DishDto.class);

        assertEquals(dish, dishMapper.dishDtoToEntity(dishDto));
    }

    @Test
    public void givenOneDish_whenFindByNonExistingId_then404() throws Exception {
        dishRepository.save(dish);

        MvcResult mvcResult = this.mockMvc.perform(get(DISH_BASE_URI + "/{id}", -1)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenPost_thenDishWithAllSetProperties() throws Exception {
        DishDto dishDto = dishMapper.dishEntityToDto(dish);
        String body = objectMapper.writeValueAsString(dishDto);

        MvcResult mvcResult = this.mockMvc.perform(post(DISH_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DishDto dishDtoResponse = objectMapper.readValue(response.getContentAsString(),
            DishDto.class);

        assertNotNull(dishDtoResponse.getId());
        //Set generated properties to null to make the response comparable with the original input
        dishDtoResponse.setId(null);
        assertEquals(dish, dishMapper.dishDtoToEntity(dishDtoResponse));
    }

    @Test
    public void givenNothing_whenPostInvalid_then400() throws Exception {
        dish.setName(null);
        dish.setPrice(-1L);
        DishDto dishDto = dishMapper.dishEntityToDto(dish);
        String body = objectMapper.writeValueAsString(dishDto);

        MvcResult mvcResult = this.mockMvc.perform(post(DISH_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(2, errors.length);
            }
        );
    }

    @Test
    public void givenNothing_whenDeleteInvalid_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(DISH_BASE_URI + '/' + -1)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneDish_whenDelete_then200() throws Exception {
        dish = dishRepository.save(dish);

        MvcResult mvcResult = this.mockMvc.perform(delete(DISH_BASE_URI + '/' + dish.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    @Test
    public void givenNothing_whenPutInvalid_then404() throws Exception {
        dish.setId(ID);
        DishDto dishDto = dishMapper.dishEntityToDto(dish);
        String body = objectMapper.writeValueAsString(dishDto);

        MvcResult mvcResult = this.mockMvc.perform(put(DISH_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneDish_whenPut_thenDishWithAllUpdatedProperties() throws Exception {
        dishRepository.save(dish);

        Dish dishEdit = dish;
        dishEdit.setName(TEST_DISH_NAME_EDIT);
        dishEdit.setPrice(TEST_DISH_PRICE_EDIT);
        DishDto dishDto = dishMapper.dishEntityToDto(dishEdit);
        String body = objectMapper.writeValueAsString(dishDto);

        MvcResult mvcResult = this.mockMvc.perform(put(DISH_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DishDto dishDtoResponse = objectMapper.readValue(response.getContentAsString(),
            DishDto.class);

        assertEquals(dishEdit, dishMapper.dishDtoToEntity(dishDtoResponse));
    }
}
