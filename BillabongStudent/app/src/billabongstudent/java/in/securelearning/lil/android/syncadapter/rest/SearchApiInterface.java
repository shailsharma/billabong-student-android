package in.securelearning.lil.android.syncadapter.rest;


import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.syncadapter.dataobjects.TeacherGradeMapping;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Json Download Interface
 */

public interface SearchApiInterface {

    @GET("search/digital-book/{objectId}/preview")
    Call<ResponseBody> getDigitalBook2(@Path("objectId") String objectId);

    @GET("search/custom-section/fetchCSWithAnnotation/{objectId}")
    Call<ResponseBody> getCustomSection(@Path("objectId") String objectId);

    @GET("search/interactive-video/fetchInteractiveVideo/{objectId}")
    Call<ResponseBody> getInteractiveVideo(@Path("objectId") String objectId);

    @GET("search/popup/fetchPopup/{objectId}")
    Call<ResponseBody> getPopUp(@Path("objectId") String objectId);

    @GET("search/interactive-image/fetchInteractiveImage/{objectId}")
    Call<ResponseBody> getInteractiveImage(@Path("objectId") String objectId);

    @GET("search/concept-map/fetch/{objectId}")
    Call<ResponseBody> getConceptMap(@Path("objectId") String objectId);

    @GET("LearningMaps/fetchByUser")
    Call<ArrayList<LearningMap>> getLearningMapList();

    @GET("search/curator-mapping/fetchCuratorGradeSection")
    Call<TeacherGradeMapping> getTeacherMapDataForMap();
}
