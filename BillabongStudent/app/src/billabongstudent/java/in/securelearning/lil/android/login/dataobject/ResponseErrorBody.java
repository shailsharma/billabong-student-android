package in.securelearning.lil.android.login.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 08-06-2017.
 */

public class ResponseErrorBody {

    @SerializedName("message")
    @Expose
    private String mMessage;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
