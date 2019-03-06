package in.securelearning.lil.android.syncadapter.rest;

import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkLoginRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionParent;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkQuestionSubmit;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkTopicListRequest;
import in.securelearning.lil.android.syncadapter.dataobjects.MindSparkTopicResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MindSparkLoginApiInterface {

    @POST("https://7t7rntf5va.execute-api.ap-south-1.amazonaws.com/prod")
    Call<MindSparkLoginResponse> loginUserToMindSpark(@Body MindSparkLoginRequest mindSparkLoginRequest);

    @POST("https://90lys2ljg3.execute-api.ap-south-1.amazonaws.com/prod")
    Call<MindSparkQuestionParent> getMindSparkQuestion(@Body MindSparkQuestionRequest mindSparkQuestionRequest);

    @POST("https://f1h7ilcvjj.execute-api.ap-south-1.amazonaws.com/prod")
    Call<MindSparkQuestionParent> submitAndFetchNewQuestion(@Body MindSparkQuestionSubmit mindSparkQuestionSubmit);

    @POST("https://kzeah1m51f.execute-api.ap-south-1.amazonaws.com/prod")
    Call<MindSparkTopicResult> getMindSparkTopicResult(@Body MindSparkTopicListRequest mindSparkTopicListRequest);
}
