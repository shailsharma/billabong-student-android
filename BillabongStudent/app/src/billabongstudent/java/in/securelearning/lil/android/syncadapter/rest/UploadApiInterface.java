package in.securelearning.lil.android.syncadapter.rest;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.UserCourseProgress;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.player.dataobject.QuestionFeedback;
import in.securelearning.lil.android.syncadapter.dataobjects.RefreshFCMToken;
import in.securelearning.lil.android.syncadapter.dataobjects.UserTimeSpent;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Json Upload Interface
 */
public interface UploadApiInterface {

    @POST("AssignmentResponses/updateAssignmentResponse/")
    Call<AssignmentResponse> uploadNewAssignmentResponse(@Body AssignmentResponse assignmentResponse);

    @PUT("AssignmentResponses/{objectId}")
    Call<AssignmentResponse> uploadAssignmentResponse(@Body AssignmentResponse assignmentResponse, @Path(value = "objectId") String objectId);

    @PUT("users")
    Call<ResponseBody> updateUserProfile(@Body UserProfile userProfile);

    @PUT("userdevices/upsert")
    Call<ResponseBody> sendRegistrationToServer(@Body RefreshFCMToken refreshFCMToken);

    @DELETE("Annotations/{id}")
    Call<ResponseBody> deleteAnnotation(@Path("id") String id);

    @POST("courseprogresses/create")
    Call<ResponseBody> uploadUserCourseProgress(@Body UserCourseProgress userCourseProgress);

    /*Api to post question feedback*/
    @POST("QuestionFeedbacks/save-question-feedback")
    Call<ResponseBody> postQuestionFeedback(@Body QuestionFeedback questionFeedback);

    /*Api to upload user time spent on activity*/
    @POST("UserTimeSpents/create")
    Call<ResponseBody> uploadUserTimeSpent(@Body UserTimeSpent userTimeSpent);

    /*Api to upload video watch start info*/
    @GET("userlogs/userStatus/videoStart/{videoId}/{moduleId}")
    Call<ResponseBody> uploadVideoWatchStarted(@Path("videoId") String resourceId, @Path("moduleId") String moduleId);

    /*Api to upload video watch end info*/
    @GET("userlogs/userStatus/videoEnd/{videoId}/{moduleId}")
    Call<ResponseBody> uploadVideoWatchEnded(@Path("videoId") String resourceId, @Path("moduleId") String moduleId);
}
