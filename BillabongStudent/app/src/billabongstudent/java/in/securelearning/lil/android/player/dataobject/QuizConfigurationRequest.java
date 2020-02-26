package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuizConfigurationRequest implements Serializable {


    @SerializedName("courseType")
    @Expose
    private String mCourseType;

    @SerializedName("id")
    @Expose
    private String mId;

    public QuizConfigurationRequest(String courseId, String courseType) {
        mId = courseId;
        mCourseType = courseType;
    }
}
