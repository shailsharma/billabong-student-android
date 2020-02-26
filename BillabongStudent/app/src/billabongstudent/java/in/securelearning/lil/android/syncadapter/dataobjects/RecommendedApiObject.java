package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.utils.ArrayList;

/**
 * Created by Prabodh Dhabaria on 18-04-2017.
 */

public class RecommendedApiObject {
    @SerializedName("subject")
    @Expose
    private Subject mSubject = new Subject();

    @SerializedName("value")
    @Expose
    private ArrayList<AboutCourse> mAboutCourses = new ArrayList<>();

    public Subject getSubject() {
        return mSubject;
    }

    public void setSubject(Subject subject) {
        mSubject = subject;
    }

    public ArrayList<AboutCourse> getAboutCourses() {
        return mAboutCourses;
    }

    public void setAboutCourses(ArrayList<AboutCourse> aboutCourses) {
        mAboutCourses = aboutCourses;
    }
}
