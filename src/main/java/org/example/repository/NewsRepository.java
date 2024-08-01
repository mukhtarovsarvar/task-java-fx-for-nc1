package org.example.repository;

import org.example.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByPublicationTimeBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByNewsKey(String key);
}