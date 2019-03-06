package in.securelearning.lil.android.login.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Prabodh Dhabaria on 08-06-2017.
 */

public class SignUpError {
    @SerializedName("error")
    @Expose
    private SignUpErrorBody mErrorBody=new SignUpErrorBody();

    public SignUpErrorBody getErrorBody() {
        return mErrorBody;
    }

    public void setErrorBody(SignUpErrorBody errorBody) {
        mErrorBody = errorBody;
    }
}
