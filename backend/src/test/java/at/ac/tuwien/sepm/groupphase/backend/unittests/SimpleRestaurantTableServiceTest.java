package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.repository.RestaurantTableRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleRestaurantTableService;
import at.ac.tuwien.sepm.groupphase.backend.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

class SimpleRestaurantTableServiceTest {

    private SimpleRestaurantTableService restaurantTableService;

    @Mock
    private RestaurantTableRepository  restaurantTableRepository;

    @Mock
    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        restaurantTableService = new SimpleRestaurantTableService(restaurantTableRepository, validator);
    }

    @Test
    void givenOneTableinRepo_whenfindAll_thenTableListReturnedContainsOneElement() {
        RestaurantTable table = new RestaurantTable();
        LinkedList<RestaurantTable> tablesData = new LinkedList<>();
        tablesData.add(table);

        when(restaurantTableRepository.findAll()).thenReturn(tablesData);

        List<RestaurantTable> tables = restaurantTableService.findAll();

        assertEquals(tables.size(), 1);
        verify(restaurantTableRepository, times(1)).findAll();//make sure findAll() was called only once
    }

    @Test
    void givenOneTableinRepo_whenfindOneById_thenTableReturnedEqualToTableFromRepo() {
        RestaurantTable tableData = new RestaurantTable();

        when(restaurantTableRepository.findById(1L)).thenReturn(Optional.of(tableData));

        RestaurantTable table = restaurantTableService.findOne(1L);

        assertEquals(table, tableData);
        verify(restaurantTableRepository, times(1)).findById(1L);
    }

    @Test
    void givenNothing_whenAddOneTable_thenTableReturnedEqualToTableGiven() {
        RestaurantTable tableInput = RestaurantTable.RestaurantTableBuilder.aTable()
            .withId(1L)
            .withTableNum(100L)
            .withSeatCount(2)
            .withPosDescription("somewhere")
            .withActive(false)
            .build();

        when(restaurantTableRepository.save(tableInput)).thenReturn(tableInput);

        RestaurantTable tableOutput = restaurantTableService.add(tableInput);

        assertEquals(tableInput, tableOutput);
        verify(restaurantTableRepository, times(1)).save(tableInput);
    }

    @Test
    void givenOneTable_WhenUpdateWithAllValuesChanged_ReturnedTableValuesAllChanged() {
        RestaurantTable tableInput = RestaurantTable.RestaurantTableBuilder.aTable()
            .withId(1L)
            .withTableNum(100L)
            .withSeatCount(2)
            .withPosDescription("somewhere")
            .withActive(false)
            .build();

        RestaurantTable inputCopy = new RestaurantTable(tableInput);
        when(restaurantTableRepository.getOne(1L)).thenReturn(inputCopy);
        RestaurantTable tableUpdate = RestaurantTable.RestaurantTableBuilder.aTable()
            .withId(1L)
            .withTableNum(200L)
            .withSeatCount(4)
            .withPosDescription("nowhere")
            .withActive(true)
            .build();
        when(restaurantTableRepository.save(tableUpdate)).thenReturn(tableUpdate);

        RestaurantTable tableOutput = restaurantTableService.update(tableUpdate);
        assertNotEquals(tableInput, tableOutput);
        assertEquals(tableUpdate, tableOutput);
    }
}