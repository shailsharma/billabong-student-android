package in.securelearning.lil.android.syncadapter.rest;

import org.json.JSONObject;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.syncadapter.dataobject.RefreshFCMToken;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @POST("BlogComments")
    Call<BlogComment> uploadBlogComment(@Body BlogComment blogComment);

    @PUT("users")
    Call<ResponseBody> updateUserProfile(@Body UserProfile userProfile);

    @PUT("userdevices/upsert")
    Call<ResponseBody> sendRegistrationToServer(@Body RefreshFCMToken refreshFCMToken);

    @DELETE("Annotations/{id}")
    Call<ResponseBody> deleteAnnotation(@Path("id") String id);
}
