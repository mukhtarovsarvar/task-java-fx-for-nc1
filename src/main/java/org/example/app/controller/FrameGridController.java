package org.example.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.app.views.def.FrameGridDef;
import org.example.entity.SimpleEntity;
import org.example.jfxsupport.AbstractFxmlView;
import org.example.jfxsupport.FXMLController;
import org.example.jfxsupport.PrototypeController;
import org.example.service.FrameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for handling the frame grid view.
 * Provides functionalities for adding, editing, deleting, and viewing information of entities.
 *
 * Author: Mukhtarov Sarvarbek
 * Project: task-java-fx-for-nc1
 * Contact: @sarvargo
 */
@FXMLController
@Scope("prototype")
public class FrameGridController implements PrototypeController {
    private static final Logger logger = LoggerFactory.getLogger(FrameGridController.class);

    @Autowired
    private ApplicationContext context;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button infoButton;

    @FXML
    private ComboBox<String> timePeriod;

    @FXML
    private TableView<SimpleEntity> frameGrid;

    private FrameService frameService;

    private FrameGridDef gridDef;

    private Scene scene;

    /**
     * Initialize method called by JavaFX after FXML is loaded.
     * Sets up button actions and initializes the time period combo box.
     */
    @FXML
    private void initialize() {
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setOnAction((event) -> addButtonHandleAction());
        editButton.setOnAction((event) -> editButtonHandleAction());
        deleteButton.setOnAction((event) -> deleteButtonHandleAction());
        infoButton.setOnAction((event) -> infoButtonHandleAction());

        if (timePeriod.getItems().isEmpty())
            timePeriod.getItems().addAll("Morning", "Day", "Evening");

        timePeriod.setOnAction(event -> loadData());
    }

    /**
     * Handler for the add button action.
     * Shows the dialog and invokes the add method on the controller.
     */
    private void addButtonHandleAction() {
        AbstractFxmlView fxmlView = showDialog();
        CrudController controller = fxmlView.getFxmlLoader().getController();
        controller.add();
    }

    /**
     * Handler for the edit button action.
     * Shows the dialog and invokes the render method on the controller with the selected entity.
     */
    private void editButtonHandleAction() {
        AbstractFxmlView fxmlView = showDialog();
        CrudController controller = fxmlView.getFxmlLoader().getController();
        SimpleEntity entity = frameGrid.getSelectionModel().getSelectedItem();
        controller.render(entity);
    }

    /**
     * Handler for the delete button action.
     * Deletes the selected entity and reloads the data.
     */
    private void deleteButtonHandleAction() {
        SimpleEntity entity = frameGrid.getSelectionModel().getSelectedItem();
        frameService.delete(entity.getId());
        loadData();
    }

    /**
     * Handler for the info button action.
     * Shows the dialog and invokes the render method on the controller with the selected entity.
     */
    private void infoButtonHandleAction() {
        AbstractFxmlView fxmlView = showDialog();
        CrudController controller = fxmlView.getFxmlLoader().getController();
        SimpleEntity entity = frameGrid.getSelectionModel().getSelectedItem();
        controller.render(entity);
    }

    /**
     * Initializes the grid with the provided frame service and grid definition.
     * Sets up the grid columns and loads the initial data.
     *
     * @param frameService The service for frame operations.
     * @param gridDef The grid definition.
     */
    public void initializeGrid(FrameService frameService, FrameGridDef gridDef) {
        this.frameService = frameService;
        this.gridDef = gridDef;
        setupGrid();
        loadData();
    }

    /**
     * Sets up the grid columns based on the grid definition.
     * Adds listeners for selection changes and key/mouse events.
     */
    private void setupGrid() {
        List<String> columnNames = gridDef.getColumnNames();
        List<String> columnDataNames = gridDef.getColumnDataName();
        List<Integer> columnSizes = gridDef.getColumnSizes();

        for (int i = 0; i < columnNames.size(); i++) {
            TableColumn<SimpleEntity, String> column = new TableColumn<>(columnNames.get(i));
            column.setCellValueFactory(new PropertyValueFactory<>(columnDataNames.get(i)));
            column.setMinWidth(columnSizes.get(i));
            frameGrid.getColumns().add(column);
        }

        frameGrid.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        frameGrid.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                editButtonHandleAction();
            }
        });

        frameGrid.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                editButtonHandleAction();
            }
        });
    }

    /**
     * Loads the data into the grid based on the selected time period.
     * Filters the data from the frame service and sets it to the grid.
     */
    private void loadData() {
        String selectedPeriod = timePeriod.getValue() == null ? "" : timePeriod.getValue();
        LocalDateTime start, end;
        LocalDateTime now = LocalDateTime.now();

        // Determine the start and end times based on the selected period
        end = switch (selectedPeriod) {
            case "Morning" -> {
                start = now.withHour(6).withMinute(0);
                yield now.withHour(12).withMinute(0);
            }
            case "Day" -> {
                start = now.withHour(12).withMinute(0);
                yield now.withHour(18).withMinute(0);
            }
            case "Evening" -> {
                start = now.withHour(18).withMinute(0);
                yield now.withHour(23).withMinute(59);
            }
            default -> {
                start = now.withHour(0).withMinute(0);
                yield now.withHour(23).withMinute(59);
            }
        };

        ObservableList<SimpleEntity> data = FXCollections.observableArrayList(frameService.getData(start, end));
        logger.debug("loadData, data size: {}", data.size());
        frameGrid.setItems(data);
    }

    /**
     * Shows a modal dialog for CRUD operations.
     * Sets up the dialog properties and shows it.
     *
     * @return The FXML view of the dialog.
     */
    private AbstractFxmlView showDialog() {
        AbstractFxmlView fxmlView = (AbstractFxmlView) context.getBean(gridDef.getCreateView());
        Stage stage = new Stage();
        scene = new Scene(fxmlView.getView());
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle(gridDef.getTitlePopups());

        stage.setOnHidden(event -> {
            stage.close();
            SimpleEntity oldSelected = frameGrid.getSelectionModel().getSelectedItem();
            loadData();
            if (oldSelected != null) frameGrid.getSelectionModel().select(oldSelected);
            else frameGrid.getSelectionModel().select(0);
        });

        stage.show();
        return fxmlView;
    }
}
