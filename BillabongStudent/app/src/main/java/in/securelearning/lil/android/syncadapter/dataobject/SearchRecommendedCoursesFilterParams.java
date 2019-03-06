package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchRecommendedCoursesFilterParams {
    @SerializedName("sortBy")
    @Expose
    private String mSortBy = "popular";
    @SerializedName("filterByLLids")
    @Expose
    private ArrayList<String> mLearningLevelIds;
    @SerializedName("filterByCourseType")
    @Expose
    private ArrayList<String> mCourseTypes;

    public ArrayList<String> getCourseTypes() {
        return mCourseTypes;
    }

    public void setCourseTypes(ArrayList<String> courseTypes) {
        mCourseTypes = courseTypes;
    }

    public String getSortBy() {
        return mSortBy;
    }

    public void setSortBy(String sortBy) {
        mSortBy = sortBy;
    }

    public ArrayList<String> getLearningLevelIds() {
        return mLearningLevelIds;
    }

    public void setLearningLevelIds(ArrayList<String> learningLevelIds) {
        mLearningLevelIds = learningLevelIds;
    }
}
