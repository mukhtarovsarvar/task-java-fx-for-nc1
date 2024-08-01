package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author 'Mukhtarov Sarvarbek' on 7/30/2024
 * @project task-java-fx-for-nc1
 * @contact @sarvargo
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class News extends SimpleEntity {

    private String newsKey;

    private String headline;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime publicationTime;

    @Override
    public String toString() {
        return "News{" +
                "newsKey='" + newsKey + '\'' +
                ", headline='" + headline + '\'' +
                ", description='" + description + '\'' +
                ", publicationTime=" + publicationTime +
                '}';
    }
}
