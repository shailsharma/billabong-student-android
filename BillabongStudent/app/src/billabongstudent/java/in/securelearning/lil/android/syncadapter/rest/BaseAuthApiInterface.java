package in.securelearning.lil.android.syncadapter.rest;

import in.securelearning.lil.android.base.dataobjects.UserProfile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.OPTIONS;
import retrofit2.http.POST;

/**
 * Created by Chaitendra on 08-Nov-17.
 */

public interface BaseAuthApiInterface {

    @POST("/change_password")
    Call<ResponseBody> changePassword(@Body UserProfile userProfile);

}
