package in.securelearning.lil.android.syncadapter.rest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;

/**
 * Sync Success Interface
 */
public interface SyncSuccessApiInterface {
    @PUT("Notifications/updateSyncStatus")
    Call<ResponseBody> postSyncSuccess(@Body List<String> objectId);

    @FormUrlEncoded
    @PUT("Notifications/updateSyncStatus")
    Call<ResponseBody> postSyncSuccess(@Field("id") String objectId);



}
