package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.securelearning.lil.android.app.R;

/**
 * Created by Secure on 12-05-2017.
 */

public class MessageData {
    @SerializedName("body")
    @Expose
    private final String mMessage;

   @SerializedName("title")
    @Expose
    private final String mTitle;

    @SerializedName("icon")
    @Expose
    private int mIcon = R.drawable.notification_icon;

    @SerializedName("sound")
    @Expose
    private String mSound  = "default";

    public String getTitle() {
        return mTitle;
    }

    public MessageData(String message, String title) {
        mMessage = message;
        mTitle = title;
    }

    public String getMessage() {
        return mMessage;
    }
}
