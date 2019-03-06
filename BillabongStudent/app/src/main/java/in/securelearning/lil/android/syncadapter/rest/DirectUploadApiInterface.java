package in.securelearning.lil.android.syncadapter.rest;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Json Upload Interface
 */
public interface DirectUploadApiInterface {

    @POST("QuizResponses")
    Call<ResponseBody> uploadQuizResponse(@Body RequestBody request);

    @POST("Annotations/create")
    Call<ResponseBody> uploadAnnotation(@Body RequestBody bookAnnotation);
}
