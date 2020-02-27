package in.securelearning.lil.android.profile.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.syncadapter.dataobjects.IdNameObject;

/**
 * Created by Rajat Jain on 28/8/19.
 * <p>
 * User Interest item
 */
public class UserInterest extends IdNameObject implements Serializable {

    @SerializedName("order")
    @Expose
    private int mOrder;

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }
}
