package in.securelearning.lil.android.syncadapter.dataobjects;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.Location;
import in.securelearning.lil.android.base.dataobjects.Role;
import in.securelearning.lil.android.base.dataobjects.Section;
import in.securelearning.lil.android.base.dataobjects.UserProfile;

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



}