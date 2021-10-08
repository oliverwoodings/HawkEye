package uk.co.oliwali.HawkEye.callbacks;

import java.util.List;
import uk.co.oliwali.HawkEye.database.SearchQuery;

public abstract class BaseCallback {

   public List results = null;


   public abstract void execute();

   public abstract void error(SearchQuery.SearchError var1, String var2);
}
