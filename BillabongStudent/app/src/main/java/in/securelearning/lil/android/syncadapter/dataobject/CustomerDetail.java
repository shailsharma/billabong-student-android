package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.LocationCourse;

/**
 * Created by Chaitendra on 15-Nov-17.
 */

public class CustomerDetail implements Serializable {

    @SerializedName(value = "id", alternate = "_id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("location")
    @Expose
    private LocationCourse mLocation;

    @SerializedName("mobile")
    @Expose
    private String mMobileNumber;

    @SerializedName("email")
    @Expose
    private String mEmail;

    @SerializedName("businessName")
    @Expose
    private String mBusinessName;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public LocationCourse getLocation() {
        return mLocation;
    }

    public void setLocation(LocationCourse location) {
        mLocation = location;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        mMobileNumber = mobileNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getBusinessName() {
        return mBusinessName;
    }

    public void setBusinessName(String businessName) {
        mBusinessName = businessName;
    }
}
