package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RolePermissions implements Serializable {
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("instituteId")
    @Expose
    private String mInstituteId;
    @SerializedName("permissions")
    @Expose
    private Permission[] mPermissions;

    public RolePermissions() {
    }

    public RolePermissions(String name, String instituteId, Permission[] permissions) {
        mName = name;
        mInstituteId = instituteId;
        mPermissions = permissions;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getInstituteId() {
        return mInstituteId;
    }

    public void setInstituteId(String instituteId) {
        mInstituteId = instituteId;
    }

    public Permission[] getPermissions() {
        return mPermissions;
    }

    public void setPermissions(Permission[] permissions) {
        mPermissions = permissions;
    }
}