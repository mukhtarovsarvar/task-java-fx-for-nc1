package org.example.scheduler;

import org.example.entity.News;
import org.example.service.NewsService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.time.LocalDateTime;


/**
 * @author 'Mukhtarov Sarvarbek' on 7/30/2024
 * @project task-java-fx-for-nc1
 * @contact @sarvargo
 */
@Component
public class NewsParsingTask {
    private final NewsService newsService;

    public NewsParsingTask(NewsService newsService) {
        this.newsService = newsService;
    }

    @Scheduled(fixedRate = 1200000)
    public void fetchNews() {
        try {
            Document doc = Jsoup.connect("https://qalampir.uz/latest").get();
            Elements newsElements = doc.select("div.col-lg-4");

            for (Element element : newsElements) {
                String href = element.select("a.news-card").attr("href");
                String key = href.split("/")[2];

                if (newsService.isExists(element.select("a.news-card").attr("href"))) continue;

                Document elementDoc = Jsoup.connect("https://qalampir.uz".concat(href)).get();
                String headline = elementDoc.select("div.title").select("h1.text").text();
                String description = elementDoc.select("div.content-main-titles").text();
                LocalDateTime publicationTime = LocalDateTime.now();

                News news = new News();
                news.setHeadline(headline);
                news.setNewsKey(key);
                news.setDescription(description);
                news.setPublicationTime(publicationTime);

                newsService.saveNews(news);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
