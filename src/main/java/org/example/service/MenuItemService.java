package org.example.service;


import org.example.entity.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuItemService {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);
    private List<MenuItem> menuItems = List.of(
            new MenuItem(0L, -1L, "master", "System", null, null, null, null, null, true),
            new MenuItem(1L, 0L, "master.customers", "News", null, "newsService", "customerGridDef", null, "news_16x16.png", false)
            );

    public MenuItem getMenuItemRoot() {
        logger.info("Getting menu item root");
        return menuItems.get(0);
    }

    public List<MenuItem> getAllMenuItems() {
        logger.info("Getting all menu items");
        Iterable<MenuItem> it = menuItems;
        List<MenuItem> result = new ArrayList<>();
        it.forEach(result::add);
        return result;
    }

    public List<MenuItem> getMenuItemsByParent(Long parent) {
        logger.info("Getting menu items by parent {}", parent);
        Iterable<MenuItem> it = menuItems;
        List<MenuItem> result = new ArrayList<>();
        it.forEach(result::add);
        return result;
    }
}
