package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.profile.dataobject.UserInterestParent;

public class StudentProfile extends UserProfile implements Serializable {

    @SerializedName("enrollmentNumber")
    @Expose
    private String enrollmentNumber;

    @SerializedName("fatherEmail")
    @Expose
    private String fatherEmail;

    @SerializedName("fatherMobile")
    @Expose
    private String fatherMobile;

    @SerializedName("fatherName")
    @Expose
    private String fatherName;

    @SerializedName("motherEmail")
    @Expose
    private String motherEmail;

    @SerializedName("motherMobile")
    @Expose
    private String motherMobile;

    @SerializedName("motherName")
    @Expose
    private String motherName;

    @SerializedName("userInterest")
    @Expose
    private UserInterestParent mUserInterest;

    @SerializedName("admissionDate")
    @Expose
    private String mAdmissionDate;

    private Boolean mIsGood;

    public Boolean getGood() {
        return mIsGood;
    }

    public void setGood(Boolean good) {
        mIsGood = good;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getFatherEmail() {
        return fatherEmail;
    }

    public void setFatherEmail(String fatherEmail) {
        this.fatherEmail = fatherEmail;
    }

    public String getFatherMobile() {
        return fatherMobile;
    }

    public void setFatherMobile(String fatherMobile) {
        this.fatherMobile = fatherMobile;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherEmail() {
        return motherEmail;
    }

    public void setMotherEmail(String motherEmail) {
        this.motherEmail = motherEmail;
    }

    public String getMotherMobile() {
        return motherMobile;
    }

    public void setMotherMobile(String motherMobile) {
        this.motherMobile = motherMobile;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public UserInterestParent getUserInterest() {
        return mUserInterest;
    }

    public void setUserInterest(UserInterestParent userInterest) {
        mUserInterest = userInterest;
    }

    public String getAdmissionDate() {
        return mAdmissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        mAdmissionDate = admissionDate;
    }
}