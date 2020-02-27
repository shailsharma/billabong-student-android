package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.base.dataobjects.Subject;

/**
 * Created by Secure on 20-07-2017.
 */

public class StudentGradeMapping extends BaseDataObject{
    @SerializedName("mapping")
    @Expose
    ArrayList<Subject> mSubjects = new ArrayList<>();

    public ArrayList<Subject> getSubjects() {
        return mSubjects;
    }

    public void setSubjects(ArrayList<Subject> subjects) {
        mSubjects = subjects;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public void setObjectId(String s) {

    }

    @Override
    public String thumbnailFileUrl() {
        return null;
    }
}
