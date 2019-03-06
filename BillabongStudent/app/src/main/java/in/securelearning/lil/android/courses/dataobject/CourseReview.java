package in.securelearning.lil.android.courses.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.UserRating;

/**
 * Created by Prabodh Dhabaria on 21-11-2016.
 */
public class CourseReview extends UserRating implements Serializable {
    @SerializedName("courseType")
    @Expose
    public String mCourseType = "";

    public CourseReview() {
    }

    public CourseReview(UserRating userRating, String courseType) {
        setComment(userRating.getComment());
        setDate(userRating.getDate());
        setName(userRating.getName());
        setRating(userRating.getRating());
        setUserId(userRating.getUserId());
        this.mCourseType = courseType;
    }

    public String getCourseType() {
        return mCourseType;
    }

    public void setCourseType(String courseType) {
        mCourseType = courseType;
    }
}
