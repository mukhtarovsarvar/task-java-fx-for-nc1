package org.example.app.views.def;

import java.util.List;

/**
 * Interface defining the configuration for a frame grid.
 * This includes the column names, data field names, column sizes,
 * the view class for creating new entries, and the title for popup windows.
 */
public interface FrameGridDef {

	/**
	 * Returns the list of column names for the grid.
	 *
	 * @return List of column names.
	 */
	List<String> getColumnNames();

	/**
	 * Returns the list of data field names corresponding to the columns.
	 *
	 * @return List of column data names.
	 */
	List<String> getColumnDataName();

	/**
	 * Returns the list of column sizes for the grid.
	 *
	 * @return List of column sizes.
	 */
	List<Integer> getColumnSizes();

	/**
	 * Returns the class type of the view to be used for creating new entities.
	 *
	 * @return Class type of the create view.
	 */
	Class<?> getCreateView();

	/**
	 * Returns the title to be used for popup windows.
	 *
	 * @return Title of the popups.
	 */
	String getTitlePopups();
}
