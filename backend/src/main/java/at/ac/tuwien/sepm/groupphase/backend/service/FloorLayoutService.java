package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.FloorLayout;

public interface FloorLayoutService {
    /**
     * Find a single layout in the database
     * @param id the id of the layout
     * @return the layout of the restaurant
     */
    FloorLayout findLayout(Long id);

    /**
     * Adds a layout to the database
     * @param floorLayout a new layout
     * @return added layout
     */
    FloorLayout add(FloorLayout floorLayout);

    /**
     * Updates a layout
     * @param floorLayout to update
     * @return updated layout
     */
    FloorLayout update(FloorLayout floorLayout);
}
