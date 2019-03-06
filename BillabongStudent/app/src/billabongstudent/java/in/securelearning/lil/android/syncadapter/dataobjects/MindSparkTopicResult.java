package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MindSparkTopicResult extends MindSparkResult implements Serializable {

    @SerializedName("topicList")
    @Expose
    private ArrayList<MindSparkContentDetails> mTopicList;

    public ArrayList<MindSparkContentDetails> getTopicList() {
        return mTopicList;
    }

    public void setTopicList(ArrayList<MindSparkContentDetails> topicList) {
        mTopicList = topicList;
    }
}
