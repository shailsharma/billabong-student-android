package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.securelearning.lil.android.base.dataobjects.PostResponse;

/**
 * Created by Chaitendra on 17-May-17.
 */

public class PostResponseFCM extends PostResponse {

    @SerializedName("groupId")
    @Expose
    private String mGroupId = "";

    public PostResponseFCM(String groupId) {
        mGroupId = groupId;
    }

    public PostResponseFCM() {
    }

    public String getGroupId() {

        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }
}
