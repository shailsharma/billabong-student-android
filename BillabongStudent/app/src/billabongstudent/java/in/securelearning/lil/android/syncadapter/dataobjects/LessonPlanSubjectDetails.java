package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LessonPlanSubjectDetails implements Serializable {

    @SerializedName("subjectDetail")
    @Expose
    private LessonPlanSubject mSubject;

    @SerializedName("topicDetail")
    @Expose
    private LessonPlanChapter mTopic;

    @SerializedName("GroupDetail")
    @Expose
    private LessonPlanGroupDetails mGroup;

    public LessonPlanSubject getSubject() {
        return mSubject;
    }

    public void setSubject(LessonPlanSubject subject) {
        mSubject = subject;
    }

    public LessonPlanChapter getTopic() {
        return mTopic;
    }

    public void setTopic(LessonPlanChapter topic) {
        mTopic = topic;
    }

    public LessonPlanGroupDetails getGroup() {
        return mGroup;
    }

    public void setGroup(LessonPlanGroupDetails group) {
        mGroup = group;
    }
}
