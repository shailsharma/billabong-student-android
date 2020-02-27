package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.UserProfile;

/**
 * Created by Prabodh Dhabaria on 31-03-2017.
 */
public class AppUserAuth0 implements Serializable {


    @SerializedName("user")
    @Expose
    private UserProfile userInfo = new UserProfile();

    public UserProfile getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserProfile userInfo) {
        this.userInfo = userInfo;
    }
}
