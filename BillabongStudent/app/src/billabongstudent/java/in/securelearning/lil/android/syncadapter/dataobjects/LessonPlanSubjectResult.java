package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LessonPlanSubjectResult implements Serializable {


    @SerializedName("results")
    @Expose
    private ArrayList<LessonPlanSubject> mLessonPlanSubjects;

    @SerializedName("total")
    @Expose
    private int mTotal;

    public ArrayList<LessonPlanSubject> getLessonPlanSubjects() {
        return mLessonPlanSubjects;
    }

    public void setLessonPlanSubjects(ArrayList<LessonPlanSubject> lessonPlanSubjects) {
        mLessonPlanSubjects = lessonPlanSubjects;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

}
