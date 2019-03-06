package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Prabodh Dhabaria on 20-11-2017.
 */

public class ApiWrapper implements Serializable {
    @SerializedName("data")
    @Expose
    private Object mData;

    public Object getData() {
        return mData;
    }

    public void setData(Object data) {
        mData = data;
    }

    public ApiWrapper(Object data) {
        mData = data;
    }

    public ApiWrapper() {
    }
}
