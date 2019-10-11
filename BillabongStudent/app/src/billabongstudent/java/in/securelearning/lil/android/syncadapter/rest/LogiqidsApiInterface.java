package in.securelearning.lil.android.syncadapter.rest;

import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsLoginResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsWorksheetResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LogiqidsApiInterface {

    @POST("v2/sub-user/login/")
    Call<LogiqidsLoginResult> loginToLogiqids(@Body Credentials credentials);

    @GET("/v1/user/{user_id}/content/topic/{topic_id}/worksheet/")
    Call<LogiqidsWorksheetResult> getWorksheetList(@Path("user_id") int userId, @Path("topic_id") int topicId);

    @GET("/v1/user/{user_id}/content/topic/{topic_id}/worksheet/{worksheet_id}/online/")
    Call<LogiqidsQuestionResult> getQuestion(@Path("user_id") int userId, @Path("topic_id") int topicId, @Path("worksheet_id") int worksheetId);

    @POST("/v1/user/{user_id}/content/topic/{topic_id}/worksheet/{worksheet_id}/online/")
    Call<LogiqidsQuestionAttemptResult> submitQuestionResponse(@Path("user_id") int userId, @Path("topic_id") int topicId, @Path("worksheet_id") int worksheetId, @Body LogiqidsQuestionAttemptRequest logiqidsQuestionAttemptRequest);

}
