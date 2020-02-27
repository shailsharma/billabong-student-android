package in.securelearning.lil.android.player.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionTypeMatchTheFollowing implements Serializable {

    @SerializedName("entityName")
    @Expose
    private String mEntityName;

    @SerializedName("placeHolderName")
    @Expose
    private String mPlaceHolderName;

    @SerializedName("matchingContent")
    @Expose
    private ArrayList<MatchingContent> mMatchingContentList;

    public String getEntityName() {
        return mEntityName;
    }


    public String getPlaceHolderName() {
        return mPlaceHolderName;
    }


    public ArrayList<MatchingContent> getMatchingContentList() {
        return mMatchingContentList;
    }

}
