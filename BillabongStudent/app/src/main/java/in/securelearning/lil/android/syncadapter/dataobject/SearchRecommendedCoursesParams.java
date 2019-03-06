package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchRecommendedCoursesParams {
    @SerializedName("filter")
    @Expose
    private SearchRecommendedCoursesFilterParams mSearchParams;
    @SerializedName("instituteId")
    @Expose
    private String mInstituteId;
    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;

    public String getInstituteId() {
        return mInstituteId;
    }

    public void setInstituteId(String instituteId) {
        mInstituteId = instituteId;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public SearchRecommendedCoursesFilterParams getSearchParams() {
        return mSearchParams;
    }

    public void setSearchParams(SearchRecommendedCoursesFilterParams searchParams) {
        mSearchParams = searchParams;
    }
}
