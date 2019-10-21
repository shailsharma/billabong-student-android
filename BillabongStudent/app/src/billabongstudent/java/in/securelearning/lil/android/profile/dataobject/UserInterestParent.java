package in.securelearning.lil.android.profile.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rajat Jain on 28/8/19.
 * <p>
 * User interest and goal in profile
 */
public class UserInterestParent implements Serializable {

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("coCurricularActivities")
    @Expose
    private ArrayList<UserInterest> mCoCurricularActivities;

    @SerializedName("hobbies")
    @Expose
    private ArrayList<UserInterest> mHobbies;

    @SerializedName("academicSubjects")
    @Expose
    private ArrayList<UserInterest> mAcademicSubjects;

    @SerializedName("dailyTarget")
    @Expose
    private int mDailyTarget;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public ArrayList<UserInterest> getCoCurricularActivities() {
        return mCoCurricularActivities;
    }

    public void setCoCurricularActivities(ArrayList<UserInterest> coCurricularActivities) {
        mCoCurricularActivities = coCurricularActivities;
    }

    public ArrayList<UserInterest> getHobbies() {
        return mHobbies;
    }

    public void setHobbies(ArrayList<UserInterest> hobbies) {
        mHobbies = hobbies;
    }

    public ArrayList<UserInterest> getAcademicSubjects() {
        return mAcademicSubjects;
    }

    public void setAcademicSubjects(ArrayList<UserInterest> academicSubjects) {
        mAcademicSubjects = academicSubjects;
    }

    public int getDailyTarget() {
        return mDailyTarget;
    }

    public void setDailyTarget(int dailyTarget) {
        mDailyTarget = dailyTarget;
    }
}