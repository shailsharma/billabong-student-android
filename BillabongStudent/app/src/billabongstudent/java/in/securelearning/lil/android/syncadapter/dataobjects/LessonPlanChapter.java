package in.securelearning.lil.android.syncadapter.dataobjects;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.ChapterHeaderData.HEADER_YET_TO_START;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_COMPLETED;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_IN_PROGRESS;
import static in.securelearning.lil.android.syncadapter.dataobjects.LessonPlanChapterResult.STATUS_YET_TO_START;

public class LessonPlanChapter implements Serializable {


    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("status")
    @Expose
    private String mStatus;

    @SerializedName("startDate")
    @Expose
    private String mStartDate;

    @SerializedName("endDate")
    @Expose
    private String mEndDate;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }
}
