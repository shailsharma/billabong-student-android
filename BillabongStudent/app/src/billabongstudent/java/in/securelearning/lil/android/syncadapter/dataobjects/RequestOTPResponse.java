package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chaitendra on 20-Jul-17.
 */

public class RequestOTPResponse implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @SerializedName("message")
    @Expose
    private String onError;

    @SerializedName("mobile")
    @Expose
    private String mMobile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOnError() {
        return onError;
    }

    public void setOnError(String onError) {
        this.onError = onError;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String mobile) {
        mMobile = mobile;
    }
}
