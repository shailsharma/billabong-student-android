package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LessonPlanChapterResult implements Serializable {

    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_IN_PROGRESS = "today";
    public static final String STATUS_YET_TO_START = "yetToStart";
    @SerializedName("results")
    @Expose
    private ArrayList<LessonPlanChapter> mLessonPlanChapters;

    @SerializedName("total")
    @Expose
    private int mTotal;

    public ArrayList<LessonPlanChapter> getLessonPlanChapters() {
        return mLessonPlanChapters;
    }

    public void setLessonPlanChapters(ArrayList<LessonPlanChapter> lessonPlanChapters) {
        mLessonPlanChapters = lessonPlanChapters;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    //get status wise data from one arraylist to other arraylist because of get statuswise data
    public ArrayList<LessonPlanChapter> getCompletedChapters() {

        ArrayList<LessonPlanChapter> completedChapters = new ArrayList<>();
        ArrayList<LessonPlanChapter> allChapters = getLessonPlanChapters();

        for (LessonPlanChapter lessonPlanChapter : allChapters) {
            if (lessonPlanChapter.getStatus().equalsIgnoreCase(STATUS_COMPLETED)) {
                completedChapters.add(lessonPlanChapter);
            }

        }
        return completedChapters;
    }

    public ArrayList<LessonPlanChapter> getYetToStartChapters() {

        ArrayList<LessonPlanChapter> YetToStartChapters = new ArrayList<>();
        ArrayList<LessonPlanChapter> allChapters = getLessonPlanChapters();

        for (LessonPlanChapter lessonPlanChapter : allChapters) {
            if (lessonPlanChapter.getStatus().equalsIgnoreCase(STATUS_YET_TO_START)) {
                YetToStartChapters.add(lessonPlanChapter);
            }

        }
        return YetToStartChapters;
    }

    public ArrayList<LessonPlanChapter> getInProgressChapters() {

        ArrayList<LessonPlanChapter> InProgressChapters = new ArrayList<>();
        ArrayList<LessonPlanChapter> allChapters = getLessonPlanChapters();

        for (LessonPlanChapter lessonPlanChapter : allChapters) {
            if (lessonPlanChapter.getStatus().equalsIgnoreCase(STATUS_IN_PROGRESS)) {
                InProgressChapters.add(lessonPlanChapter);
            }

        }
        return InProgressChapters;
    }

}
