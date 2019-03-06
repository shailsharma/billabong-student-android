package in.securelearning.lil.android.syncadapter.fcmservices;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Secure on 12-05-2017.
 */

public class MessageDataPayload {

    @SerializedName("payload")
    @Expose
    private final Object mPayload;

    @SerializedName("type")
    @Expose
    private final String mType;

    @SerializedName("id")
    @Expose
    private final String mId;

    @SerializedName("date")
    @Expose
    private final String mDate;


    public MessageDataPayload(Object payload, String type) {
        mPayload = payload;
        mType = type;
        mId = null;
        mDate = null;
    }


    public MessageDataPayload(Object payload, String type, String objectId, String date) {
        mPayload = payload;
        mType = type;
        mId = objectId;
        mDate = date;
    }

    public Object getPayload() {
        return mPayload;
    }

    public String getType() {
        return mType;
    }

    public String getId() {
        return mId;
    }

    public String getDate() {
        return mDate;
    }
}
