package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.LocationCourse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;

/**
 * Created by Chaitendra on 13-Nov-17.
 */

public class UserMinimal implements Serializable {

    @SerializedName(value = "id", alternate = "_id")
    @Expose
    private String mId;

    @SerializedName("firstName")
    @Expose
    private String mFirstName;

    @SerializedName("lastName")
    @Expose
    private String mLastName;

    @SerializedName("email")
    @Expose
    private String mEmail;

    @SerializedName("businessName")
    @Expose
    private String mBusinessName;

    @SerializedName("mobile")
    @Expose
    private String mMobileNumber;

    @SerializedName("gender")
    @Expose
    private String mGender;

    @SerializedName("address")
    @Expose
    private String mAddress;

    @SerializedName("location")
    @Expose
    private LocationCourse mLocationCourse;

    @SerializedName(value = "thumbnail", alternate = "userThumbnail")
    @Expose
    private Thumbnail mThumbnail;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mFirstName + " " + mLastName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getBusinessName() {
        return mBusinessName;
    }

    public void setBusinessName(String businessName) {
        mBusinessName = businessName;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        mMobileNumber = mobileNumber;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public LocationCourse getLocationCourse() {
        return mLocationCourse;
    }

    public void setLocationCourse(LocationCourse locationCourse) {
        mLocationCourse = locationCourse;
    }

    public Thumbnail getThumbnail() {
        return mThumbnail;
    }
}
