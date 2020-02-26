package in.securelearning.lil.android.syncadapter.rest;


import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.QuestionResponse;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.UserBrowseHistory;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Json Upload Interface
 */
public interface NewUploadApiInterface {

    @POST("Assignments/")
    Call<Assignment> uploadAssignment(@Body Assignment assignment);

    @POST("Quizzes/")
    Call<Quiz> uploadQuiz(@Body Quiz quiz);

    @PUT("Quizzes/{objectId}")
    Call<Quiz> updateQuiz(@Body Quiz quiz, @Path(value = "objectId") String objectId);

    @POST("LearningNetworkPosts/")
    Call<PostData> uploadPostData(@Body PostData postData);

    @POST("LearningNetworkPosts/")
    Call<java.util.ArrayList<PostData>> uploadPostData(@Body java.util.ArrayList<PostData> postData);

    @POST("CalendarEvents/upsert")
    Call<CalendarEvent> uploadCalendarEvent(@Body CalendarEvent calendarEvent);

    @POST("LearningNetworkPostResponses/")
    Call<PostResponse> uploadPostResponseCall(@Body PostResponse postResponse);

    @POST("AssignedBadges/")
    Call<AssignedBadges> uploadAssignedBadgeCall(@Body AssignedBadges assignedBadges);

    @POST("")
    Call<QuestionResponse> uploadQuestionResponseCall(@Body QuestionResponse questionResponse);

    @POST("UserBrowseHistories")
    Call<UserBrowseHistory> uploadUserBrowseHistory(@Body UserBrowseHistory userBrowseHistory);

}
