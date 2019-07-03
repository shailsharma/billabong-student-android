package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.syncadapter.dataobject.IdNameObject;

public class LessonPlanGroupDetails implements Serializable {

    @SerializedName("grade")
    @Expose
    private IdNameObject mGrade;

    @SerializedName("section")
    @Expose
    private IdNameObject mSection;

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    public IdNameObject getGrade() {
        return mGrade;
    }

    public void setGrade(IdNameObject grade) {
        mGrade = grade;
    }

    public IdNameObject getSection() {
        return mSection;
    }

    public void setSection(IdNameObject section) {
        mSection = section;
    }

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
}
