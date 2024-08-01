package org.example.app.controller;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import org.example.app.config.PropertiesConfig;
import org.example.app.views.FrameGridView;
import org.example.app.views.def.FrameGridDef;
import org.example.jfxsupport.FXMLController;
import org.example.service.FrameService;
import org.example.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the main application window.
 * Manages the main panel, tree view, and tab pane for displaying content.
 */
@FXMLController
public class MainController {

    private static final String ADDITIONAL_TAB_TITLE = "     ";

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PropertiesConfig config;

    @Autowired
    private MenuItemService menuItemService;

    @FXML
    private VBox mainPanel;

    @FXML
    private SplitPane mainSplitPanel;

    @FXML
    private TabPane tabPane;

    private TreeView<org.example.entity.MenuItem> treeView;

    /**
     * Initialize method called by JavaFX after FXML is loaded.
     * Sets up the main panel, toolbar, and tree view based on configuration.
     */
    @FXML
    private void initialize() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        mainPanel.setPrefWidth(primaryScreenBounds.getWidth() * 0.8);
        mainPanel.setPrefHeight(primaryScreenBounds.getHeight() * 0.8);

        if (config.getJavafxMainToolbar()) {
            ToolBar toolbar = buildToolBar();
            mainPanel.getChildren().add(1, toolbar);
        }

        if (config.getJavafxMainTree()) {
            org.example.entity.MenuItem rootMenuItem = menuItemService.getMenuItemRoot();
            TreeItem<org.example.entity.MenuItem> rootNode = new TreeItem<>(rootMenuItem);
            rootNode.setExpanded(rootMenuItem.getExpanded());
            treeView = new TreeView<>(rootNode);
            buildTreeItems(rootNode);
            mainSplitPanel.getItems().add(0, treeView);

            treeView.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    onSelectItemAction(treeView);
                }
            });

            treeView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    onSelectItemAction(treeView);
                }
            });
        }
    }

    /**
     * Event handler for when the window is shown.
     * Selects the root item in the tree view and sets focus.
     */
    public void onWindowShownEvent() {
        if (treeView != null) {
            MultipleSelectionModel<TreeItem<org.example.entity.MenuItem>> msm = treeView.getSelectionModel();
            msm.select(treeView.getRoot());
            treeView.requestFocus();
        }
    }

    /**
     * Builds the tree items for the given parent node.
     * Recursively adds child items to the parent node.
     *
     * @param parentNode The parent node to add child items to.
     */
    private void buildTreeItems(TreeItem<org.example.entity.MenuItem> parentNode) {
        Long parentId = parentNode.getValue() != null ? parentNode.getValue().getId() : -1L;
        List<org.example.entity.MenuItem> menuItems = menuItemService.getMenuItemsByParent(parentId);

        for (org.example.entity.MenuItem item : menuItems) {
            TreeItem<org.example.entity.MenuItem> itemNode = new TreeItem<>(item);
            itemNode.setExpanded(item.getExpanded());

            if (Optional.ofNullable(item.getImage()).isPresent()) {
                itemNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/" + item.getImage()))));
            }

            parentNode.getChildren().add(itemNode);
            // Uncomment the following line to build tree items recursively
            // buildTreeItems(itemNode);
        }
    }

    /**
     * Handles the selection of an item in the tree view.
     * Opens the corresponding tab or selects it if already open.
     *
     * @param treeView The tree view containing the selected item.
     */
    private void onSelectItemAction(TreeView<org.example.entity.MenuItem> treeView) {
        TreeItem<org.example.entity.MenuItem> item = treeView.getSelectionModel().getSelectedItem();
        if (item == null) return;

        int tabIndex = findTabIndex(item.getValue().getValue());
        if (tabIndex == -1) {
            showItemContent(item.getValue());
        } else {
            tabPane.getSelectionModel().select(tabIndex);
        }
    }

    /**
     * Displays the content of the selected menu item in a new tab.
     *
     * @param menuItem The selected menu item.
     */
    private void showItemContent(org.example.entity.MenuItem menuItem) {
        if (menuItem.getService() == null) return;

        FrameGridView gridView = context.getBean(FrameGridView.class);
        FrameGridController controller = context.getBean(FrameGridController.class);
        gridView.setController(controller);

        Tab tab = new Tab(menuItem.getValue() + ADDITIONAL_TAB_TITLE);
        tab.setContent(gridView.getView());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);

        FrameService frameService = (FrameService) context.getBean(menuItem.getService());
        FrameGridDef frameGridDef = (FrameGridDef) context.getBean(menuItem.getGridDef());
        controller.initializeGrid(frameService, frameGridDef);
    }

    /**
     * Builds the toolbar with buttons for each menu item.
     *
     * @return The constructed toolbar.
     */
    private ToolBar buildToolBar() {
        ToolBar toolbar = new ToolBar();
        List<org.example.entity.MenuItem> menuItems = menuItemService.getMenuItemsByParent(0L);

        for (org.example.entity.MenuItem item : menuItems) {
            Button button = new Button();
            button.setTooltip(new Tooltip(item.getValue()));

            if (Optional.ofNullable(item.getImage()).isPresent()) {
                button.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/" + item.getImage()))));
            }

            button.setOnAction(event -> onToolBarButtonAction(item));
            toolbar.getItems().add(button);
        }

        return toolbar;
    }

    /**
     * Handles the action of toolbar buttons.
     * Opens the corresponding tab or selects it if already open.
     *
     * @param item The menu item associated with the button.
     */
    private void onToolBarButtonAction(org.example.entity.MenuItem item) {
        int tabIndex = findTabIndex(item.getValue());
        if (tabIndex == -1) {
            showItemContent(item);
        } else {
            tabPane.getSelectionModel().select(tabIndex);
        }
    }

    /**
     * Finds the index of the tab with the given title.
     *
     * @param title The title of the tab.
     * @return The index of the tab, or -1 if not found.
     */
    private int findTabIndex(String title) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            Tab tab = tabPane.getTabs().get(i);
            if (tab.getText().equals(title + ADDITIONAL_TAB_TITLE)) {
                return i;
            }
        }
        return -1;
    }
}
