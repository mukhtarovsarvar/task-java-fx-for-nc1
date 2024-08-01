package org.example.controller;

import org.example.entity.News;
import org.example.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for managing news entities.
 * Provides endpoints for fetching, adding, and deleting news.
 *
 * @project task-java-fx-for-nc1
 * @contact @sarvargo
 */
@RestController
@RequestMapping("/api/news")
public class NewsWebController {
    private final NewsService newsService;

    /**
     * Constructs a NewsWebController with the given NewsService.
     *
     * @param newsService the news service to be used by the controller.
     */
    public NewsWebController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Fetches news within a specified time period.
     *
     * @param start the start time of the period.
     * @param end the end time of the period.
     * @return a list of news entities within the specified time period.
     */
    @GetMapping
    public List<News> getNews(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return newsService.getNewsByTimePeriod(start, end);
    }

    /**
     * Adds a new news entity.
     *
     * @param news the news entity to be added.
     * @return the added news entity.
     */
    @PostMapping
    public News addNews(@RequestBody News news) {
        return newsService.saveNews(news);
    }

    /**
     * Deletes a news entity by its ID.
     *
     * @param id the ID of the news entity to be deleted.
     */
    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
    }
}
