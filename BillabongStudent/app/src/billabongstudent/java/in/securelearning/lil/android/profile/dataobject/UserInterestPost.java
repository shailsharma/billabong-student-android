package in.securelearning.lil.android.profile.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rajat Jain on 28/8/19.
 * <p>
 * To send user selected goal and interest to server
 */
public class UserInterestPost implements Serializable {

    @SerializedName("id")
    @Expose
    private String mUserInterestId;

    @SerializedName("dailyTarget")
    @Expose
    private Integer mDailyTarget;

    @SerializedName("subjectIds")
    @Expose
    private ArrayList<String> mAcademicSubjectIds;

    @SerializedName("coCurricularActivities")
    @Expose
    private ArrayList<String> mCoCurricularActivityIds;

    @SerializedName("hobbies")
    @Expose
    private ArrayList<String> mHobbyIds;

    public String getUserInterestId() {
        return mUserInterestId;
    }

    public void setUserInterestId(String userInterestId) {
        mUserInterestId = userInterestId;
    }

    public Integer getDailyTarget() {
        return mDailyTarget;
    }

    public void setDailyTarget(Integer dailyTarget) {
        mDailyTarget = dailyTarget;
    }

    public ArrayList<String> getAcademicSubjectIds() {
        return mAcademicSubjectIds;
    }

    public void setAcademicSubjectIds(ArrayList<String> academicSubjectIds) {
        mAcademicSubjectIds = academicSubjectIds;
    }

    public ArrayList<String> getCoCurricularActivityIds() {
        return mCoCurricularActivityIds;
    }

    public void setCoCurricularActivityIds(ArrayList<String> coCurricularActivityIds) {
        mCoCurricularActivityIds = coCurricularActivityIds;
    }

    public ArrayList<String> getHobbyIds() {
        return mHobbyIds;
    }

    public void setHobbyIds(ArrayList<String> hobbyIds) {
        mHobbyIds = hobbyIds;
    }
}
