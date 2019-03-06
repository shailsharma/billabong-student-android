package in.securelearning.lil.android.syncadapter.events;

/**
 * Created by Prabodh Dhabaria on 16-03-2017.
 */

public class SearchSubmitEvent {
    private final String mQueryText;

    public SearchSubmitEvent(String queryText) {
        mQueryText = queryText;
    }

    public String getQueryText() {
        return mQueryText;
    }
}
