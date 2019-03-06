package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chaitendra on 13-Nov-17.
 */

public class SignUpValidation implements Serializable {

    @SerializedName("userObj")
    @Expose
    private UserMinimal mUserMinimal;

    @SerializedName("validationForm")
    @Expose
    private SignUpValidationForm mSignUpValidationForm;

    public UserMinimal getUserMinimal() {
        return mUserMinimal;
    }

    public void setUserMinimal(UserMinimal userMinimal) {
        mUserMinimal = userMinimal;
    }

    public SignUpValidationForm getSignUpValidationForm() {
        return mSignUpValidationForm;
    }

    public void setSignUpValidationForm(SignUpValidationForm signUpValidationForm) {
        mSignUpValidationForm = signUpValidationForm;
    }
}
