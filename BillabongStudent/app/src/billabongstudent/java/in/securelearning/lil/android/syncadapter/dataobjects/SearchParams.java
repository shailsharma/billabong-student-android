package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class SearchParams {
    @SerializedName("keyword")
    @Expose
    private String mSearchQuery = "";

    @SerializedName("subjects")
    @Expose
    private ArrayList<String> mSubjects = new ArrayList<>();

    @SerializedName("learningLevels")
    @Expose
    private ArrayList<String> mLearningLevels = new ArrayList<>();

    @SerializedName("langs")
    @Expose
    private ArrayList<String> mLangs = new ArrayList<>();

    @SerializedName("courseTypes")
    @Expose
    private ArrayList<String> mCollections = new ArrayList<>();

    @SerializedName("total")
    @Expose
    private int mTotal = 0;

    @SerializedName("grades")
    @Expose
    private ArrayList<String> mGrades = new ArrayList<>();


    public ArrayList<String> getGrades() {
        return mGrades;
    }

    public void setGrades(ArrayList<String> grades) {
        mGrades = grades;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public ArrayList<String> getLearningLevels() {
        return mLearningLevels;
    }

    public void setLearningLevels(ArrayList<String> learningLevels) {
        mLearningLevels = learningLevels;
    }

    public ArrayList<String> getLangs() {
        return mLangs;
    }

    public void setLangs(ArrayList<String> langs) {
        mLangs = langs;
    }

    public String getSearchQuery() {
        return mSearchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        mSearchQuery = searchQuery;
    }

    public ArrayList<String> getSubjects() {
        return mSubjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        mSubjects = subjects;
    }

    public ArrayList<String> getCollections() {
        return mCollections;
    }

    public void setCollections(ArrayList<String> collections) {
        mCollections = collections;
    }

//    public boolean isAndroid() {
//        return isAndroid;
//    }
//
//    public void setAndroid(boolean android) {
//        isAndroid = android;
//    }
}
