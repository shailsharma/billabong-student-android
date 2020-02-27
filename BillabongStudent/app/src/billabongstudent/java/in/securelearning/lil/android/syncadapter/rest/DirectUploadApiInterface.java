package in.securelearning.lil.android.syncadapter.rest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Json Upload Interface
 */
public interface DirectUploadApiInterface {

    @POST("QuizResponses")
    Call<ResponseBody> uploadQuizResponse(@Body RequestBody request);

    @POST("Annotations/create")
    Call<ResponseBody> uploadAnnotation(@Body RequestBody bookAnnotation);

    @POST("popupactivities/save")
    Call<ResponseBody> savePopupActivity(@Body RequestBody requestBody);

    @POST("QuizResponses/getReportByQuizId")
    Call<ResponseBody> getReportByQuizId(@Body RequestBody requestBody);

    @POST("dictionaries/search")
    Call<ResponseBody> dictionariesSearch(@Body RequestBody requestBody);

    @POST("Utilities/getAflAolCourseConfig")
    Call<ResponseBody> getAflAolConfiguration(@Body RequestBody requestBody);

    @POST("courseprogresses/create")
    Call<ResponseBody> saveCourseProgress(@Body RequestBody request);

    @PUT("Annotations/")
    Call<ResponseBody> saveBookmark(@Body RequestBody requestBody);

    @POST("UserTimeSpents/create")
    Call<ResponseBody> saveUserTimeSpentProgress(@Body RequestBody requestBody);

    @POST("GlobalConfigs/fetchConfig")
    Call<ResponseBody> fetchGlobalConfigs(@Body RequestBody requestBody);

    @POST("search/khan-academy/getVideoDetail")
    Call<ResponseBody> fetchKhanAcademyVideoDetail(@Body RequestBody requestBody);

    @POST("QuestionFeedbacks/save-question-feedback")
    Call<ResponseBody> saveQuestionFeedback(@Body RequestBody requestBody);
}
