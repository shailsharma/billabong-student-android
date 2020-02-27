package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Secure on 12-05-2017.
 */

public class MessageResponse {
    @SerializedName("message_id")
    @Expose
    private String mMessage;

    @SerializedName("error")
    @Expose
    private String mError;


    public void setMessage(String message) {
        mMessage = message;
    }

    public String getError() {
        return mError;
    }

    public void setError(String error) {
        mError = error;
    }

    public String getMessage() {
        return mMessage;
    }
}
