package in.securelearning.lil.android.syncadapter.rest;


import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Results;
import in.securelearning.lil.android.syncadapter.dataobjects.CloudinaryFileInner;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * File Upload Interface
 */
public interface UploadFilesApiInterface {

    @POST("Resources/uploadResource")
    Call<Results> uploadFileUsingJson(@Body Resource resource);

    @Multipart
    @POST("resources/uploadResource")
    Call<CloudinaryFileInner> upload(@Header("Authorization") String authorization,
                                     @Part MultipartBody.Part file, @Part MultipartBody.Part fileType, @Part MultipartBody.Part save, @Part MultipartBody.Part isPrivate
    );

    @Multipart
    @POST("resources/uploadResource")
    Call<CloudinaryFileInner> uploadVideo(@Header("Authorization") String authorization,
                                          @Part MultipartBody.Part file, @Part MultipartBody.Part fileType, @Part MultipartBody.Part save, @Part MultipartBody.Part isPrivate
    );

}
