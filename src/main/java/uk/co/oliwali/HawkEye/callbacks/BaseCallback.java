package uk.co.oliwali.HawkEye.callbacks;

import java.util.List;

import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.entry.DataEntry;

/**
 * Abstract class representing a callback for use in {@SearchQuery}
 * @author oliverw92
 */
public abstract class BaseCallback {

	/**
	 * Contains results of the {@SearchQuery}
	 */
	public List<DataEntry> results = null;

	/**
	 * Called when the {@SearchQuery} is complete
	 */
	public abstract void execute();

	/**
	 * Called if an error occurs during the {@SearchQuery}
	 * @param error {@SearchError} that has occurred
	 * @param message error message with more detail
	 */
	public abstract void error(SearchError error, String message);

}
