package in.securelearning.lil.android.syncadapter.rest;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.base.dataobjects.Institution;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.syncadapter.dataobject.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobject.RefreshToken;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTP;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTPResponse;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Base Interface
 */
public interface FlavorBaseApiInterface {

    Response connectToServer();
//
//    @FormUrlEncoded
//    @POST("users/refreshToken")
//    Call<AccessToken> getAccessToken(
//            @Field("code") String code,
//            @Field("grant_type") String grantType);

//    @POST("users/refreshToken")
//    Call<AuthToken> getAccessToken(@Body Credentials credentials);

//    @POST("users/refreshToken")
//    Call<AppUser> refreshToken(@Body Credentials credentials);

    @GET("Institutes/fetchInstituteDetails")
    Call<ArrayList<Institution>> fetchInstituteList();

    @POST("users")
    Call<ResponseBody> login(@Body UserProfile userProfile);

    @GET("users/isValidEmail")
    Call<ResponseBody> checkEmailExistence(@Query("email") String email);

//    @POST("http://uat.learnindialearn.in:8000/liles/search-page/lilSearch")
//    Call<SearchResourcesResults> getResourceSearchResultsEs(@Body SearchResourceParams params);

    @POST("auth/auth/sendSMSCode")
    Call<RequestOTPResponse> requestOTP(@Body RequestOTP params);

    @POST("auth/auth/verifySMSCode")
    Call<AuthToken> verifyOTP(@Body RequestOTP params);

    @POST("auth/auth/genToken")
    Call<AuthToken> refreshToken(@Body RefreshToken params);

    @POST("auth/auth/login")
    Call<AuthToken> loginNew(@Body Credentials params);

//    @POST("http://uat.learnindialearn.in:8000/liles/recommended-page/getYoutubeResourcesBySubjectGradeTopic")
//    Call<SearchResourcesResults> getVideoData(@Body RecommendedResourceParams params);

//    @GET("http://uat.learnindialearn.in:8000/pbliles/learning-map/getLMBySubjectGradeSection/{subjectId}/{gradeId}/{sectionId}/{topicId}")
//    Call<java.util.ArrayList<HomeModel.StudentScore>> getStudentAggregateDataForMap(@Path("subjectId") String subjectId, @Path("gradeId") String gradeId,@Path("sectionId") String sectionId,@Path("topicId") String topicId);
//
//    @GET("http://uat.learnindialearn.in:8000/pbliles/search-page/coursesBySubjectGradeTopic/{subjectId/{skip}/{limit}/{gradeId}/{topicId}")
//    Call<SearchCoursesResults> getCourseBySubjectGrade(@Path("subjectId") String subjectId, @Path("skip") int skip, @Path("limit") int limit, @Path("gradeId") String gradeId, @Path("topicId") String topicId);
//
//    @GET("http://uat.learnindialearn.in:8000/pbliles/recommended-page/getYoutubeResourcesBySubjectGradeTopic")
//    Call<SearchResourcesResults> getResourceBySubjectGrade(@Body RecommendedResourceParams params);
//
//    @GET("http://uat.learnindialearn.in:8000/pbliles/learning-map/getLMByUserSubjectGradeSection/{userId}/{subjectId}/{gradeId}/{sectionId}/{topicId}")
//    Call<java.util.ArrayList<LearningMap>> getStudentDataForMap(@Path("subjectId") String subjectId, @Path("userId") String userId, @Path("gradeId") String gradeId, @Path("sectionId") String sectionId, @Path("topicId") String topicId);


}
