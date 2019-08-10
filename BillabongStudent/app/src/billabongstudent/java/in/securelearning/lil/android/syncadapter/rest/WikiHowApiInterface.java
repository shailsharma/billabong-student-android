package in.securelearning.lil.android.syncadapter.rest;

import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikiHowApiInterface {

    /*Api to fetch detail of wikiHow card*/
    @GET("api.php")
    Call<WikiHowParent> fetchWikiHowCardDetail(@Query("action") String action,
                                               @Query("subcmd") String subcmd,
                                               @Query("id") String wikiHowId,
                                               @Query("format") String format);

}
