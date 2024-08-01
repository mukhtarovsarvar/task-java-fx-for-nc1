package org.example.app.views.def;

import org.example.app.views.NewsModalView;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Defines the configuration for the customer grid.
 * Implements the FrameGridDef interface to provide column definitions and related settings.
 *
 * Author: Mukhtarov Sarvarbek
 * Date: 7/31/2024
 * Project: task-java-fx-for-nc1
 * Contact: @sarvargo
 */
@Component
public class CustomerGridDef implements FrameGridDef {

	// Column names to be displayed in the grid
	public static final String[] COLUMN_NAMES = { "Id", "Headline", "Description", "Key" };

	// Corresponding data fields in the entity for the grid columns
	public static final String[] COLUMN_DATA_NAMES = { "id", "headline", "description", "newsKey" };

	// Column sizes for each column in the grid
	public static final Integer[] COLUMN_SIZES = { 40, 400, 700, 100 };

	// Title for popup windows related to this grid
	public static final String TITLE_POPUPS = "News";

	/**
	 * Returns the list of column names for the grid.
	 *
	 * @return List of column names.
	 */
	@Override
	public List<String> getColumnNames() {
		return Arrays.asList(COLUMN_NAMES);
	}

	/**
	 * Returns the list of column sizes for the grid.
	 *
	 * @return List of column sizes.
	 */
	@Override
	public List<Integer> getColumnSizes() {
		return Arrays.asList(COLUMN_SIZES);
	}

	/**
	 * Returns the list of data field names corresponding to the columns.
	 *
	 * @return List of column data names.
	 */
	@Override
	public List<String> getColumnDataName() {
		return Arrays.asList(COLUMN_DATA_NAMES);
	}

	/**
	 * Returns the class type of the view to be used for creating new entities.
	 *
	 * @return Class type of the create view.
	 */
	@Override
	public Class<?> getCreateView() {
		return NewsModalView.class;
	}

	/**
	 * Returns the title to be used for popup windows.
	 *
	 * @return Title of the popups.
	 */
	@Override
	public String getTitlePopups() {
		return TITLE_POPUPS;
	}
}
