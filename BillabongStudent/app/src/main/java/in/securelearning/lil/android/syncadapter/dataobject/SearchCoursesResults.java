package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchCoursesResults {

    @SerializedName("results")
    @Expose
    ArrayList<AboutCourseExt> mList = new ArrayList<>();

    @SerializedName("subjects")
    @Expose
    ArrayList<SearchFilterId> mSubjectsList = new ArrayList<>();

    @SerializedName("courseTypes")
    @Expose
    ArrayList<SearchFilter> mCourseTypeList = new ArrayList<>();

    @SerializedName("langs")
    @Expose
    ArrayList<SearchFilterId> mLanguageList = new ArrayList<>();

    @SerializedName("learningLevels")
    @Expose
    ArrayList<LearningLevelResult> mLearningLevelList = new ArrayList<>();
//    ArrayList<SearchFilterId> mLearningLevelList = new ArrayList<>();

    @SerializedName("total")
    @Expose
    int totalResult = 0;

    public int getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public ArrayList<AboutCourseExt> getList() {
        return mList;
    }

    public void setList(ArrayList<AboutCourseExt> list) {
        mList = list;
    }

    public ArrayList<SearchFilterId> getSubjectsList() {
        return mSubjectsList;
    }

    public void setSubjectsList(ArrayList<SearchFilterId> subjectsList) {
        mSubjectsList = subjectsList;
    }

    public ArrayList<SearchFilter> getCourseTypeList() {
        return mCourseTypeList;
    }

    public void setCourseTypeList(ArrayList<SearchFilter> courseTypeList) {
        mCourseTypeList = courseTypeList;
    }

    public ArrayList<SearchFilterId> getLanguageList() {
        return mLanguageList;
    }

    public void setLanguageList(ArrayList<SearchFilterId> languageList) {
        mLanguageList = languageList;
    }

    public ArrayList<LearningLevelResult> getLearningLevelList() {
        return mLearningLevelList;
    }

    public void setLearningLevelList(ArrayList<LearningLevelResult> learningLevelList) {
        mLearningLevelList = learningLevelList;
    }
}
