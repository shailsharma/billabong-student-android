package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rajat Jain on 4/9/19.
 */
public class VocationalTopic implements Serializable {

    @SerializedName("topicName")
    @Expose
    private String mTopicName;

    @SerializedName("items")
    @Expose
    private ArrayList<VocationalSubTopic> mVocationalSubTopics;

    public String getTopicName() {
        return mTopicName;
    }

    public void setTopicName(String topicName) {
        mTopicName = topicName;
    }

    public ArrayList<VocationalSubTopic> getVocationalSubTopics() {
        return mVocationalSubTopics;
    }

    public void setVocationalSubTopics(ArrayList<VocationalSubTopic> vocationalSubTopics) {
        mVocationalSubTopics = vocationalSubTopics;
    }
}
