package org.example.jfxsupport;

import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The enum {@link GUIState} stores Scene and Stage objects as singletons in
 * this VM.
 *
 * @author Felix Roske
 * @author Andreas Jay
 */
public enum GUIState {

	INSTANCE;
	private static Scene scene;

	private static Stage stage;

	private static String title;
	
	private static HostServices hostServices;

	public static String getTitle() {
		return title;
	}

	public static Scene getScene() {
		return scene;
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setScene(final Scene scene) {
		GUIState.scene = scene;
	}

	public static void setStage(final Stage stage) {
		GUIState.stage = stage;
	}

	public static void setTitle(final String title) {
		GUIState.title = title;
	}

        public static HostServices getHostServices() {
            return hostServices;
        }

        public static void setHostServices(HostServices hostServices) {
            GUIState.hostServices = hostServices;
        }
	
	

}
