package org.example.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.entity.News;
import org.example.entity.SimpleEntity;
import org.example.jfxsupport.FXMLController;
import org.example.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Controller for managing news entities.
 * Implements CRUD operations for news items.
 *
 * Author: Mukhtarov Sarvarbek
 * Date: 7/31/2024
 * Project: task-java-fx-for-nc1
 * Contact: @sarvargo
 */
@FXMLController
public class NewsController implements CrudController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final NewsService newsService;

    @FXML
    private ButtonBarController buttonbarController;

    @FXML
    private TextField headline;

    @FXML
    private TextField key;

    @FXML
    private TextArea description;

    private News news;

    /**
     * Constructor to initialize the NewsService.
     *
     * @param newsService The service used to manage news entities.
     */
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Initialize method called by JavaFX after FXML is loaded.
     * Sets the target of the button bar controller to this controller.
     */
    @FXML
    private void initialize() {
        buttonbarController.setTarget(this);
    }

    /**
     * Method to handle the addition of a new news entity.
     * Implementation is currently empty and can be expanded as needed.
     */
    @Override
    public void add() {
        // Implementation for adding a new news entity
    }

    /**
     * Renders the details of a news entity in the UI fields.
     *
     * @param id The entity to be rendered.
     */
    @Override
    public void render(SimpleEntity id) {
        news = (News) id;
        logger.info("News render called {}", news);
        headline.setText(news.getHeadline());
        description.setText(news.getDescription());
        key.setText(news.getNewsKey());
    }

    /**
     * Saves the current news entity details from the UI fields.
     * Updates the existing entity or creates a new one if none exists.
     */
    @Override
    public void save() {
        if (news == null) news = new News();
        logger.info("News save called {}", news);
        news.setNewsKey(key.getText());
        news.setHeadline(headline.getText());
        news.setDescription(description.getText());
        news.setPublicationTime(LocalDateTime.now());
        newsService.saveNews(news);
    }
}
