package in.securelearning.lil.android.syncadapter.fcmservices;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Secure on 12-05-2017.
 */

public class Message {
    @SerializedName("to")
    @Expose
    private final String mTo;
    @SerializedName("notification")
    @Expose
    private final MessageData mNotification;
    @SerializedName("data")
    @Expose
    private final MessageDataPayload mData;

    @SerializedName("time_to_live")
    @Expose
    private int mTTL = 3600;

    public int getTTL() {
        return mTTL;
    }

    public void setTTL(int TTL) {
        mTTL = TTL;
    }

    public Message(String to, MessageData notification, MessageDataPayload data) {
        mTo = to;
        mNotification = notification;
        mData = data;
    }

    public String getTo() {
        return mTo;
    }

    public MessageData getNotification() {
        return mNotification;
    }

    public MessageDataPayload getData() {
        return mData;
    }
}
