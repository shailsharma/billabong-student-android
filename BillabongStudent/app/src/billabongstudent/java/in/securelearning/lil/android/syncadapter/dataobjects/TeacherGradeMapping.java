package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;

/**
 * Created by Secure on 20-07-2017.
 */

public class TeacherGradeMapping extends BaseDataObject{
    @SerializedName("mapping")
    @Expose
    ArrayList<CuratorMapping> mCuratorMappings = new ArrayList<>();


    public ArrayList<CuratorMapping> getCuratorMappings() {
        return mCuratorMappings;
    }

    public void setCuratorMappings(ArrayList<CuratorMapping> curatorMappings) {
        mCuratorMappings = curatorMappings;
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
