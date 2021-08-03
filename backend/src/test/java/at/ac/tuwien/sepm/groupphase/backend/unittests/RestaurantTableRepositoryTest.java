package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.RestaurantTable;
import at.ac.tuwien.sepm.groupphase.backend.repository.RestaurantTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class RestaurantTableRepositoryTest {

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Test
    void givenNothing_whenSave_thenFindAllSizeEqualsOneAndFindByIdNotNull() {
        RestaurantTable table = RestaurantTable.RestaurantTableBuilder.aTable()
            .withTableNum(101L)
            .withSeatCount(4)
            .withActive(false)
            .withPosDescription("somewhere over the rainbow")
            .build();

        restaurantTableRepository.save(table);

        assertEquals(restaurantTableRepository.findAll().size(), 1);
        assertNotNull(restaurantTableRepository.findById(1L));
    }
}