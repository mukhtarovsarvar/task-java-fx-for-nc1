package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.News;
import org.example.entity.SimpleEntity;
import org.example.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 'Mukhtarov Sarvarbek' on 7/30/2024
 * @project task-java-fx-for-nc1
 * @contact @sarvargo
 */
@Service
@Slf4j
public class NewsService implements FrameService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> getNewsByTimePeriod(LocalDateTime start, LocalDateTime end) {
        return newsRepository.findByPublicationTimeBetween(start, end);
    }

    public News saveNews(News news) {
        return newsRepository.save(news);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    public boolean isExists(String href) {
        return newsRepository.existsByNewsKey(href);
    }

    @Override
    public List<? extends SimpleEntity> getData(LocalDateTime start, LocalDateTime end) {
        return newsRepository.findByPublicationTimeBetween(start, end);
    }

    @Override
    public void delete(Long id) {
        newsRepository.deleteById(id);
    }
}
