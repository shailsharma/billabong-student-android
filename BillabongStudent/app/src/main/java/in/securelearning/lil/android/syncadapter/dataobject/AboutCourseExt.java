package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;

/**
 * Created by Prabodh Dhabaria on 29-05-2017.
 */

public class AboutCourseExt extends AboutCourse {

    @SerializedName("course_type")
    @Expose
    private String mCourseTypeNew = "";

    @Override
    public String getCourseType() {
        return this.getCourseTypeNew();
    }

    @Override
    public String getMicroCourseType() {
        return this.getCourseTypeNew();
    }

    public String getCourseTypeNew() {
        return mCourseTypeNew;
    }

    public void setCourseTypeNew(String courseTypeNew) {
        mCourseTypeNew = courseTypeNew;
    }


}
