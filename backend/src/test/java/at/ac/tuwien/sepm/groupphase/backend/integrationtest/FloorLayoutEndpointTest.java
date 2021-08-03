package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FloorLayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.FloorLayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.FloorLayout;
import at.ac.tuwien.sepm.groupphase.backend.repository.FloorLayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FloorLayoutEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FloorLayoutRepository layoutRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FloorLayoutMapper layoutMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private FloorLayout layout = FloorLayout.FloorLayoutBuilder.aLayout()
        .withSerializedLayout(TEST_LAYOUT_SERIALIZATION)
        .buildFloorLayout();

    @BeforeEach
    void beforeEach() {
        layoutRepository.deleteAll();
        layout = FloorLayout.FloorLayoutBuilder.aLayout()
            .withSerializedLayout(TEST_LAYOUT_SERIALIZATION)
            .buildFloorLayout();
    }

    @Test
    void givenNonexistentId_shouldRespondWithStatusNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(FLOOR_LAYOUT_BASE_URI + "/0")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void saveLayoutWithStringValue_shouldRespondWithAllProperties() throws Exception {
        FloorLayoutDto inputDto = layoutMapper.floorLayoutEntityToFloorLayoutDto(layout);
        String body = objectMapper.writeValueAsString(inputDto);
        MvcResult mvcResult = this.mockMvc.perform(post(FLOOR_LAYOUT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        FloorLayoutDto outPutDto = objectMapper.readValue(response.getContentAsString(), FloorLayoutDto.class);

        assertAll(
            () -> assertEquals(inputDto.getSerializedLayout(), outPutDto.getSerializedLayout())
        );
    }

    @Test
    void updateSavedLayoutWithCorrectValues_shouldRespondWithAllUpdatedProperties() throws Exception {
        layoutRepository.save(layout);
        layout.setSerializedLayout(TEST_LAYOUT_SERIALIZATION_UPDATE);
        FloorLayoutDto inputDto = layoutMapper.floorLayoutEntityToFloorLayoutDto(layout);
        String body = objectMapper.writeValueAsString(inputDto);
        MvcResult mvcResult = this.mockMvc.perform(patch(FLOOR_LAYOUT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        FloorLayoutDto outPutDto = objectMapper.readValue(response.getContentAsString(), FloorLayoutDto.class);

        assertAll(
            () -> assertEquals(inputDto.getSerializedLayout(), outPutDto.getSerializedLayout())
        );
    }

}
