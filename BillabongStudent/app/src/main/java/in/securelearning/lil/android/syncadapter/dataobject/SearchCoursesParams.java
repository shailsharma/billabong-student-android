package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchCoursesParams {
    @SerializedName("filter")
    @Expose
    private SearchParams mSearchParams = new SearchParams();

    @SerializedName("isResource")
    @Expose
    private boolean isResource = false;

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

    public SearchParams getSearchParams() {
        return mSearchParams;
    }

    public void setSearchParams(SearchParams searchParams) {
        mSearchParams = searchParams;
    }

    public boolean isResource() {
        return isResource;
    }

    public void setResource(boolean resource) {
        isResource = resource;
    }
}
