package in.securelearning.lil.android.profile.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 27-Jul-19
 */
public class TeacherAchievementRewards implements Serializable {

    @SerializedName("training")
    @Expose
    private float mTraining;

    @SerializedName("etat")
    @Expose
    private float mEtat;

    @SerializedName("lessonPlanReflection")
    @Expose
    private float mLessonPlanReflection;

    @SerializedName("contribution")
    @Expose
    private float mContribution;

    @SerializedName("classRoomObservation")
    @Expose
    private float mClassRoomObservation;

    @SerializedName("totalReward")
    @Expose
    private float mTotalReward;

    public float getTraining() {
        return mTraining;
    }

    public float getEtat() {
        return mEtat;
    }

    public float getLessonPlanReflection() {
        return mLessonPlanReflection;
    }

    public float getContribution() {
        return mContribution;
    }

    public float getClassRoomObservation() {
        return mClassRoomObservation;
    }

    public float getTotalReward() {
        return mTotalReward;
    }
}
