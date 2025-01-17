package org.example.jfxsupport;

import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * A default standard splash pane implementation Subclass it and override it's
 * methods to customize with your own behavior.
 *
 * @author Felix Roske
 * @author Andreas Jay
 */
public class SplashScreen {

	/** The path to the default imager absolute to classpath root. */
	private static String DEFAULT_IMAGE = "/splash/javafx.png";

	/**
	 * Override this to create your own splash pane parent node.
	 *
	 * @return A standard image
	 */
	public Parent getParent() {
		final ImageView imageView = new ImageView(getClass().getResource(getImagePath()).toExternalForm());
		final ProgressBar splashProgressBar = new ProgressBar();
		splashProgressBar.setPrefWidth(imageView.getImage().getWidth());

		final VBox vbox = new VBox();
		vbox.getChildren().addAll(imageView, splashProgressBar);

		return vbox;
	}

	/**
	 * Customize if the splash screen should be visible at all.
	 *
	 * @return true by default
	 */
	public boolean visible() {
		return true;
	}

	/**
	 * Use your own splash image instead of the default one.
	 *
	 * @return "/splash/javafx.png"
	 */
	public String getImagePath() {
		return DEFAULT_IMAGE;
	}

}
