package in.securelearning.lil.android.syncadapter.rest;

import org.apache.cordova.LOG;

import in.securelearning.lil.android.syncadapter.fcmservices.Message;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageMultipleTopic;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Secure on 15-05-2017.
 */

public interface FCMApiInterface {

    @POST("send")
    Call<MessageResponse> send(@Body Message message);


    @POST("send")
    Call<MessageResponse> send(@Body MessageMultipleTopic message);

}
