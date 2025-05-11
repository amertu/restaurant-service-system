package com.spring.restaurant.backend.service.impl;

import com.spring.restaurant.backend.entity.FloorLayout;
import com.spring.restaurant.backend.exception.NotFoundException;
import com.spring.restaurant.backend.repository.FloorLayoutRepository;
import com.spring.restaurant.backend.service.FloorLayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class SimpleFloorLayoutService implements FloorLayoutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final FloorLayoutRepository floorLayoutRepository;

    public SimpleFloorLayoutService (FloorLayoutRepository floorLayoutRepository) {
        this.floorLayoutRepository = floorLayoutRepository;
    }

    @Override
    public FloorLayout findLayout(Long id) {
        LOGGER.debug("Find the layout with the id {}", id);
        Optional<FloorLayout> floorLayoutOptional = floorLayoutRepository.findById(id);
        if(floorLayoutOptional.isPresent()) {
            return floorLayoutOptional.get();
        }
        else throw new NotFoundException(String.format("Could not find layout with id %s", id));
    }

    @Override
    public FloorLayout add(FloorLayout floorLayout) {
        LOGGER.debug("Save layout");
        return floorLayoutRepository.save(floorLayout);
    }

    @Override
    @Transactional
    public FloorLayout update(FloorLayout floorLayout) {
        Long id = floorLayout.getId();
        LOGGER.debug("update layout with id {}", id);
        try {
            FloorLayout layoutFound = floorLayoutRepository.getOne(id);
            layoutFound.setId(floorLayout.getId());
            layoutFound.setSerializedLayout(floorLayout.getSerializedLayout());
            return floorLayoutRepository.save(layoutFound);
        }
        catch (EntityNotFoundException e){
            throw new NotFoundException(String.format("No layout with id %s found",id));
        }

    }
}
