package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chaitendra on 13-Nov-17.
 */

public class IdNameObject implements Serializable {

    @SerializedName(value = "id", alternate = "_id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mName;

    public IdNameObject() {
    }

    public IdNameObject(String id) {
        mId = id;
    }

    public IdNameObject(String id, String name) {
        mId = id;
        mName = name;
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
