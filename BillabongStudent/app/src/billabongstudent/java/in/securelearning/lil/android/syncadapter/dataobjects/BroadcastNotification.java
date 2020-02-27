package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.Notification;

/**
 * Created by Prabodh Dhabaria on 21-02-2017.
 */

public class BroadcastNotification extends BaseDataObject implements Serializable {

    @SerializedName("notificationData")
    @Expose
    private List<Notification> notificationData;
    @SerializedName("timestamp")
    @Expose
    private double timestamp = 0.0D;
    @SerializedName("userId")
    @Expose
    private String userId;

    public BroadcastNotification() {

    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public void setObjectId(String s) {
    }

    @Override
    public String thumbnailFileUrl() {
        return null;
    }

    public BroadcastNotification(String userId) {
        this(userId, 0.0D);
    }

    public BroadcastNotification(String userId, double timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }


    public List<Notification> getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(List<Notification> notificationData) {
        this.notificationData = notificationData;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }
}
