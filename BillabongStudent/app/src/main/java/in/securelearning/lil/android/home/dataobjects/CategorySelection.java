package in.securelearning.lil.android.home.dataobjects;

import java.util.ArrayList;

/**
 * Created by Prabodh Dhabaria on 28-04-2017.
 */

public class CategorySelection {
    private Category category;
    private SubCategory subCategory;
    private SubSubCategory subSubCategory;
    private ArrayList<String> mSubjectIds;
    private ArrayList<String> mLearningLevelIds;
    private ArrayList<String> mLanguageIds;
    private ArrayList<String> mCourseType;
    private ArrayList<String> mInnerLearningLevelIds;
    private ArrayList<String> mTopicID;

    private int stage = NOTHING_SELECTED;
    public static final int NOTHING_SELECTED = 10001;
    public static final int CATEGORY_SELECTED = 10002;
    public static final int SUB_CATEGORY_SELECTED = 10003;
    public static final int SUB_SUB_CATEGORY_SELECTED = 10004;

    public ArrayList<String> getTopicID() {
        return mTopicID;
    }

    public void setTopicID(ArrayList<String> topicID) {
        mTopicID = topicID;
    }

    public ArrayList<String> getInnerLearningLevelIds() {
        return mInnerLearningLevelIds;
    }

    public void setInnerLearningLevelIds(ArrayList<String> innerLearningLevelIds) {
        mInnerLearningLevelIds = innerLearningLevelIds;
    }

    public ArrayList<String> getSubjectIds() {
        return mSubjectIds;
    }

    public void setSubjectIds(ArrayList<String> subjectIds) {
        mSubjectIds = subjectIds;
    }

    public ArrayList<String> getLearningLevelIds() {
        return mLearningLevelIds;
    }

    public void setLearningLevelIds(ArrayList<String> learningLevelIds) {
        mLearningLevelIds = learningLevelIds;
    }

    public ArrayList<String> getLanguageIds() {
        return mLanguageIds;
    }

    public void setLanguageIds(ArrayList<String> languageIds) {
        mLanguageIds = languageIds;
    }

    public ArrayList<String> getCourseType() {
        return mCourseType;
    }

    public void setCourseType(ArrayList<String> courseType) {
        mCourseType = courseType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.stage = CATEGORY_SELECTED;
    }

    public int getStage() {
        return stage;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
        this.stage = SUB_CATEGORY_SELECTED;
    }

    public SubSubCategory getSubSubCategory() {
        return subSubCategory;
    }

    public void setSubSubCategory(SubSubCategory subSubCategory) {
        this.subSubCategory = subSubCategory;
        this.stage = SUB_SUB_CATEGORY_SELECTED;
    }

    public boolean canGoBack() {
        return stage > NOTHING_SELECTED ? true : false;
    }

    public void goBack() {
        if (stage > NOTHING_SELECTED) stage = NOTHING_SELECTED;
    }

}
