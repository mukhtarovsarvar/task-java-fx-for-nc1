package org.example.app.controller;

import org.example.entity.SimpleEntity;

/**
 * Interface for CRUD operations in the application.
 * Defines methods for adding, rendering, and saving entities.
 */
public interface CrudController {

    /**
     * Method to add a new entity.
     */
    void add();

    /**
     * Method to render an entity by its ID.
     *
     * @param entity The ID of the entity to render.
     */
    void render(SimpleEntity entity);

    /**
     * Method to save the current state of the entity.
     */
    void save();
}
