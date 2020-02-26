package in.securelearning.lil.android.login.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 08-06-2017.
 */

public class ResponseError {
    @SerializedName("error")
    @Expose
    private ResponseErrorBody mErrorBody;

    public ResponseErrorBody getErrorBody() {
        return mErrorBody;
    }

    public void setErrorBody(ResponseErrorBody errorBody) {
        mErrorBody = errorBody;
    }
}
