package uk.co.oliwali.HawkEye.callbacks;

import java.util.List;

import uk.co.oliwali.HawkEye.database.DataEntry;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;

public abstract class BaseCallback {
	
	public List<DataEntry> results;
	
	public abstract void execute();
	
	public abstract void error(SearchError error, String message);

}
