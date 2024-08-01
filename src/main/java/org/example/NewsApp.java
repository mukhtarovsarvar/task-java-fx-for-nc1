package org.example;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.example.app.controller.MainController;
import org.example.app.views.MainView;
import org.example.jfxsupport.AbstractFxmlView;
import org.example.jfxsupport.GUIState;
import org.example.jfxsupport.PropertyReaderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the NewsApp JavaFX application.
 * Uses Spring Boot for application context and scheduling support.
 *
 * Author: Mukhtarov Sarvarbek
 * Project: task-java-fx-for-nc1
 * Contact: @sarvargo
 */
@SpringBootApplication
@EnableScheduling
public class NewsApp extends javafx.application.Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsApp.class);
    private static ConfigurableApplicationContext applicationContext;
    private static List<Image> icons = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        // Set the primary stage and host services for the application
        GUIState.setStage(stage);
        GUIState.setHostServices(this.getHostServices());
        // Show the initial view
        showInitialView();
    }

    @Override
    public void init() {
        // Initialize Spring application context
        SpringApplicationBuilder builder = new SpringApplicationBuilder(NewsApp.class);
        applicationContext = builder.run(getParameters().getRaw().toArray(new String[0]));

        // Load application icons from properties or use default icons
        final List<String> fsImages = PropertyReaderHelper.get(applicationContext.getEnvironment(), "javafx.appicons");
        if (!fsImages.isEmpty()) {
            fsImages.forEach((s) -> icons.add(new Image(getClass().getResource(s).toExternalForm())));
        } else {
            icons.add(new Image(getClass().getResource("/images/gear_16x16.png").toExternalForm()));
            icons.add(new Image(getClass().getResource("/images/gear_24x24.png").toExternalForm()));
            icons.add(new Image(getClass().getResource("/images/gear_36x36.png").toExternalForm()));
            icons.add(new Image(getClass().getResource("/images/gear_42x42.png").toExternalForm()));
            icons.add(new Image(getClass().getResource("/images/gear_64x64.png").toExternalForm()));
        }
    }

    private void showInitialView() {
        // Set stage style from properties or default to DECORATED
        final String stageStyle = applicationContext.getEnvironment().getProperty("javafx.stage.style");
        if (stageStyle != null) {
            GUIState.getStage().initStyle(StageStyle.valueOf(stageStyle.toUpperCase()));
        } else {
            GUIState.getStage().initStyle(StageStyle.DECORATED);
        }
        // Show the main view
        showView(MainView.class);
    }

    public static void showView(final Class<? extends AbstractFxmlView> newView) {
        try {
            // Get the view bean from the application context
            final AbstractFxmlView view = applicationContext.getBean(newView);

            // Set the scene or update the root if scene already exists
            if (GUIState.getScene() == null) {
                GUIState.setScene(new Scene(view.getView()));
            } else {
                GUIState.getScene().setRoot(view.getView());
            }
            GUIState.getStage().setScene(GUIState.getScene());
            applyEnvPropsToView();

            // Add event handler for when the window is shown
            GUIState.getStage()
                    .addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
                        if (view.getFxmlLoader().getController() instanceof MainController) {
                            MainController mainController = view.getFxmlLoader().getController();
                            mainController.onWindowShownEvent();
                        }
                        LOGGER.debug("Stage view shown: {} ", view.getClass());
                    });

            // Show the stage
            GUIState.getStage().show();
        } catch (Throwable t) {
            LOGGER.error("Failed to load application: ", t);
            showErrorAlert(t);
        }
    }

    private static void applyEnvPropsToView() {
        // Set various stage properties from the environment
        setApplyEnv(applicationContext);
    }

    public static void setApplyEnv(ConfigurableApplicationContext applicationContext) {
        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.title", String.class, GUIState.getStage()::setTitle);
        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.stage.width", Double.class, GUIState.getStage()::setWidth);
        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.stage.height", Double.class, GUIState.getStage()::setHeight);
        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.stage.resizable", Boolean.class, GUIState.getStage()::setResizable);
    }

    private static void showErrorAlert(Throwable throwable) {
        // Show an error alert dialog in case of an unrecoverable error
        Alert alert = new Alert(Alert.AlertType.ERROR, "Oops! An unrecoverable error occurred.\n" +
                "Please contact your software vendor.\n\n" +
                "The application will stop now.");
        alert.showAndWait().ifPresent(response -> Platform.exit());
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}