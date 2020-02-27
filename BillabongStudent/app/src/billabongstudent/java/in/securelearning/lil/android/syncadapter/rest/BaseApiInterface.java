package in.securelearning.lil.android.syncadapter.rest;

import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.syncadapter.dataobjects.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobjects.RefreshToken;
import in.securelearning.lil.android.syncadapter.dataobjects.RequestOTP;
import in.securelearning.lil.android.syncadapter.dataobjects.RequestOTPResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.Token;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Base Interface
 */
public interface BaseApiInterface {

    @GET("Quizzes/{objectId}/fetchAllDetails")
    Call<Quiz> getQuiz(@Path("objectId") String objectId);

    @POST("users")
    Call<ResponseBody> login(@Body UserProfile userProfile);

    @POST("users/resetPassword")
    Call<ResponseBody> resetPassword(@Query("email") String email);

    @POST("users/login")
    Call<AuthToken> loginNew(@Body Credentials params);

    @POST("auth/auth/sendSMSCode")
    Call<RequestOTPResponse> requestOTP(@Body RequestOTP params);

    @POST("auth/auth/verifySMSCode")
    Call<AuthToken> verifyOTP(@Body RequestOTP params);

    @POST("auth/auth/genToken")
    Call<AuthToken> refreshToken(@Body RefreshToken params);

    @POST("auth/auth/login")
    Call<AuthToken> authLogin(@Body Credentials params);

    @POST("auth/auth/logout")
    Call<ResponseBody> authLogout(@Body Token params);

    @POST("auth/auth/invalidateOtherLogin")
    Call<ResponseBody> invalidateOtherLogin(@Body Token request);

}
