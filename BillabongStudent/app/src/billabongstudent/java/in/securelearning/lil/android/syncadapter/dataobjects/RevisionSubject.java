package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rajat Jain on 15/11/19.
 */
public class RevisionSubject implements Serializable {

    @SerializedName("subjectName")
    @Expose
    private String mSubjectName;

    @SerializedName("subjectColorCode")
    @Expose
    private String mSubjectColorCode;

    @SerializedName("topicLength")
    @Expose
    private String mTopicLength;

    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;


    public String getSubjectName() {
        return mSubjectName;
    }

    public String getSubjectColorCode() {
        return mSubjectColorCode;
    }

    public String getTopicLength() {
        return mTopicLength;
    }

    public String getSubjectId() {
        return mSubjectId;
    }
}
