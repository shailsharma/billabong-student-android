package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchPeriodicEventsParams {
    @SerializedName("filter")
    @Expose
    private SearchPeriodicEventsFilterParams mSearchParams = new SearchPeriodicEventsFilterParams();

     @SerializedName("skip")
    @Expose
    private int mSkip = 0;

     @SerializedName("limit")
    @Expose
    private int mLimit = 20;

    public int getSkip() {
        return mSkip;
    }

    public void setSkip(int skip) {
        mSkip = skip;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(int limit) {
        mLimit = limit;
    }


    public SearchPeriodicEventsFilterParams getSearchParams() {
        return mSearchParams;
    }

    public void setSearchParams(SearchPeriodicEventsFilterParams searchParams) {
        mSearchParams = searchParams;
    }
}
