package org.example.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;
import lombok.Setter;
import org.example.jfxsupport.FXMLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FXMLController
public class ButtonBarController {

	// Logger instance for logging events
	private static final Logger logger = LoggerFactory.getLogger(ButtonBarController.class);

	// Setter for the target CrudController
	@Setter
	private CrudController target;

	// FXML injected buttons
	@FXML
	protected Button acceptButton;

	@FXML
	protected Button cancelButton;

	// Initialization method called by JavaFX after FXML is loaded
	@FXML
	private void initialize() {
		logger.debug("initialize ButtonBarController");

		// Set action handler for the accept button
		acceptButton.setOnAction((event) -> acceptButtonHandleAction());
		acceptButton.defaultButtonProperty().bind(acceptButton.focusedProperty());

		// Set action handler for the cancel button
		cancelButton.setOnAction((event) -> cancelButtonHandleAction());
		cancelButton.defaultButtonProperty().bind(cancelButton.focusedProperty());
	}

	// Handler for accept button action
	private void acceptButtonHandleAction() {
		// Get the window associated with the button
		Window stage = acceptButton.getScene().getWindow();
		// Call the save method on the target controller
		target.save();
		// Hide the window
		stage.hide();
	}

	// Handler for cancel button action
	private void cancelButtonHandleAction() {
		// Get the window associated with the button
		Window stage = cancelButton.getScene().getWindow();
		// Hide the window
		stage.hide();
	}
}
