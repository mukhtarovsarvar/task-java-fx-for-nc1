package org.example.service;

import org.example.entity.SimpleEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface FrameService {

    List<? extends SimpleEntity> getData(LocalDateTime start, LocalDateTime end);

    void delete(Long id);

}
