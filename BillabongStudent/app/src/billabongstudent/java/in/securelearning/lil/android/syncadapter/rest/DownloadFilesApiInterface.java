package in.securelearning.lil.android.syncadapter.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * File Download Interface
 */
public interface DownloadFilesApiInterface {

    @GET
    @Streaming
    Call<ResponseBody> downloadFileFromUrl(@Url String fileUrl);
}
