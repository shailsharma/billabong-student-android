package in.securelearning.lil.android.thirdparty.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Attempt;
import in.securelearning.lil.android.base.dataobjects.Credentials;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.view.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.ThirdPartyMapping;
import in.securelearning.lil.android.syncadapter.dataobjects.WikiHowParent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.thirdparty.InjectorThirdParty;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsLoginResponseData;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsLoginResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestion;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionAttemptResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionData;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionParent;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsQuestionResult;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsTextImageObject;
import in.securelearning.lil.android.thirdparty.dataobjects.LogiqidsWorksheetResult;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkLoginRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestion;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionChoice;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionParent;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionSubmit;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionTypeBlank;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionTypeDropdown;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkQuestionTypeMCQ;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkTopicListRequest;
import in.securelearning.lil.android.thirdparty.dataobjects.MindSparkTopicResult;
import in.securelearning.lil.android.thirdparty.dataobjects.TPCurriculumResponse;
import in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants;
import in.securelearning.lil.android.thirdparty.utils.ThirdPartyPrefs;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MS_RESULT_CODE_FAILURE;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MS_RESULT_CODE_JWT_EXPIRED;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MS_RESULT_CODE_SUCCESS;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MS_RESULT_CODE_UNAUTHORIZED;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.MS_VENDOR;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.TYPE_BLANK;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.TYPE_DROPDOWN;
import static in.securelearning.lil.android.thirdparty.utils.ThirdPartyConstants.TYPE_MCQ;

public class ThirdPartyModel {

    @Inject
    NetworkModel mNetworkModel;

    @Inject
    Context mContext;

    @Inject
    AppUserModel mAppUserModel;

    public ThirdPartyModel() {
        InjectorThirdParty.INSTANCE.getComponent().inject(this);
    }

    /*To check occurrence of word in a string.*/
    public int checkWordOccurrence(String fullString, String occurrenceWord) {
        int i = 0;
        Pattern p = Pattern.compile(occurrenceWord);
        Matcher m = p.matcher(fullString);
        while (m.find()) {
            i++;
        }
        return i;
    }

    /*To get list of iFrame from a string*/
    public ArrayList<String> getIFrameListFromString(int iFrameCount, String string) {
        ArrayList<String> iFrameList = new ArrayList<>();
        for (int i = 0; i < iFrameCount; i++) {
            String iFrameSubString = string.substring(string.indexOf("<iframe"), string.indexOf("</iframe>") + 9);
            iFrameList.add(iFrameSubString);
            string = string.replace(iFrameSubString, "");
        }

        return iFrameList;
    }

    /*To get list of iFrame from a string*/
    public ArrayList<String> getImageUrlListFromString(int iFrameCount, String string) {
        ArrayList<String> iFrameList = new ArrayList<>();
        for (int i = 0; i < iFrameCount; i++) {
            String iFrameSubString = string.substring(string.indexOf("<img src"), string.indexOf(">"));
            iFrameList.add(iFrameSubString);
            string = string.replace(iFrameSubString, "");
        }

        return iFrameList;
    }

    /*To remove [blank_"number"] from blank type question text body.*/
    public String removeBlankFromQuestion(String blankTypeQuestionBody) {
        String finalString = blankTypeQuestionBody.replaceAll("\\[blank_(.*?)]", "");
        Log.e("Remove--Blank", finalString);
        return finalString;
    }

    /*To remove [dropdown_"number"] from dropdown type question text body.*/
    public String removeDropdownFromQuestion(String blankTypeQuestionBody) {
        String finalString = blankTypeQuestionBody.replaceAll("\\[dropdown_(.*?)]", "");
        Log.e("Remove--Dropdown", finalString);
        return finalString;
    }

    /**
     * check if the question response is correct or not
     */
    public boolean checkCorrectness(Question question, Attempt attempt) {
        boolean isCorrect = true;
        int correctChoicesCount = 0;
        //Count number of correct choices for question
        for (QuestionChoice questionChoice : question.getQuestionChoices()) {
            if (questionChoice.isChoiceCorrect())
                correctChoicesCount++;
        }

        if (correctChoicesCount == attempt.getSubmittedAnswer().size()) {
            for (String s : attempt.getSubmittedAnswer()) {
                isCorrect = isCorrect && question.getQuestionChoices().get(Integer.valueOf(s)).isChoiceCorrect();
            }
        } else
            isCorrect = false;

        return isCorrect;

    }

    /*Method to save Mind Spark Json Web Token to shared preference*/
    private void saveMindSparkJwt(String mindSparkAuthToken) {
        if (!TextUtils.isEmpty(mindSparkAuthToken)) {
            ThirdPartyPrefs.setMindSparkJsonWebToken(mContext, mindSparkAuthToken);
        }
    }

    /*Here saving Logiqids sessionToken and userId to shared*/
    private void saveLogiqidsCredentials(LogiqidsLoginResult body) {
        if (body != null && body.getData() != null) {
            LogiqidsLoginResponseData data = body.getData();
            ThirdPartyPrefs.setLogiqidsSessionToken(mContext, data.getSessionToken());
            ThirdPartyPrefs.setLogiqidsUserId(mContext, data.getUserId());
            Log.e("LogiqidsUserId--", String.valueOf(data.getUserId()));
            Log.e("LogiqidsSessionToken--", String.valueOf(data.getSessionToken()));
        }
    }

    /*To start video player activity*/
    public void startVideoPlayer(Context context, String url) {
        Resource item = new Resource();
        item.setType(context.getString(R.string.typeVideo));
        item.setUrlMain(url);
        context.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
    }

    /*To start youtube player activity*/
    public void startYoutubePlayer(Context context, String url) {
        if (url.contains("http:") || url.contains("https:")) {

            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
            if (matcher.find()) {
                String videoId = matcher.group();
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(videoId);
                favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
                context.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
            }

        } else {
            FavouriteResource favouriteResource = new FavouriteResource();
            favouriteResource.setName(url);
            favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
            context.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
        }
    }

    /*To start vimeo player activity*/
    public void startVimeoPlayer(Context context, String url) {
        context.startActivity(PlayVimeoFullScreenActivity.getStartIntent(context, ConstantUtil.BLANK, ConstantUtil.BLANK, ConstantUtil.BLANK, url));
    }

    /*Here converting Mind Spark question schema to LIL Question schema*/
    public Question convertMSQuestionToLILQuestion(int questionOrder, String contentId, MindSparkQuestion mindSparkQuestion) {
        Question question = new Question();
        question.setOrder(questionOrder);
        question.setUidQuestion(contentId);
        question.setQuestionText(mindSparkQuestion.getQuestionTextBody());
        question.setExplanation(mindSparkQuestion.getQuestionExplanation());
        question.setProgressionRule(mindSparkQuestion.getRevisionNo());
        question.setQuestionHints(getQuestionHints(mindSparkQuestion.getQuestionHints()));
        question.setQuestionChoices(getQuestionChoices(mindSparkQuestion.getQuestionResponse()));
        question.setQuestionType(getQuestionType(mindSparkQuestion.getQuestionResponse()));

        return question;
    }

    /*Get question type*/
    private String getQuestionType(Map<String, Object> questionResponse) {
        for (String key : questionResponse.keySet()) {
            if (key.contains(TYPE_MCQ.toLowerCase())) {
                return Question.TYPE_DISPLAY_RADIO;
            } else if (key.contains(TYPE_BLANK.toLowerCase())) {
                return ThirdPartyConstants.TYPE_BLANK;
            } else if (key.contains(TYPE_DROPDOWN.toLowerCase())) {
                return TYPE_DROPDOWN;
            }
        }
        return null;
    }

    /*Set question hints from mind spark schema to lil schema*/
    private ArrayList<QuestionHint> getQuestionHints(ArrayList<String> mindSparkQuestionHints) {
        ArrayList<QuestionHint> questionHints = new ArrayList<>();
        for (int i = 0; i < mindSparkQuestionHints.size(); i++) {
            questionHints.add(new QuestionHint(i, mindSparkQuestionHints.get(i)));
        }
        return questionHints;
    }

    /*Set question choices from mind spark schema to lil schema*/
    private ArrayList<QuestionChoice> getQuestionChoices(Map<String, Object> questionResponse) {
        ArrayList<QuestionChoice> questionChoices = new ArrayList<>();

        int i = 0;
        for (String key : questionResponse.keySet()) {
            if (key.contains(TYPE_MCQ.toLowerCase())) {
                LinkedTreeMap<String, MindSparkQuestionTypeMCQ> mindSparkQuestionMCQPatternMap = (LinkedTreeMap) questionResponse.get(key);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.toJsonTree(mindSparkQuestionMCQPatternMap).getAsJsonObject();
                String mcq = jsonObject.toString();
                MindSparkQuestionTypeMCQ mindSparkQuestionTypeMCQ = gson.fromJson(mcq, MindSparkQuestionTypeMCQ.class);

                int correctIndex = Integer.parseInt(ThirdPartyConstants.decodeBase64String(mindSparkQuestionTypeMCQ.getCorrectAnswer()));

                for (MindSparkQuestionChoice mindSparkQuestionChoice : mindSparkQuestionTypeMCQ.getQuestionChoices()) {
                    if (correctIndex == i) {
                        questionChoices.add(new QuestionChoice(key, true, mindSparkQuestionChoice.getValue()));
                    } else {
                        questionChoices.add(new QuestionChoice(key, false, mindSparkQuestionChoice.getValue()));

                    }
                }

                i++;
            } else if (key.contains(TYPE_BLANK.toLowerCase())) {
                LinkedTreeMap<String, MindSparkQuestionTypeBlank> linkedTreeMap = (LinkedTreeMap) questionResponse.get(key);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.toJsonTree(linkedTreeMap).getAsJsonObject();
                String jsonString = jsonObject.toString();
                MindSparkQuestionTypeBlank mindSparkQuestionTypeBlank = gson.fromJson(jsonString, MindSparkQuestionTypeBlank.class);
                String correctAnswer = ThirdPartyConstants.decodeBase64String(mindSparkQuestionTypeBlank.getCorrectAnswer()).replaceAll("\\\\", "");
                questionChoices.add(new QuestionChoice(key, false, correctAnswer));
            } else if (key.contains(TYPE_DROPDOWN.toLowerCase())) {
                LinkedTreeMap<String, MindSparkQuestionTypeDropdown> linkedTreeMap = (LinkedTreeMap) questionResponse.get(key);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.toJsonTree(linkedTreeMap).getAsJsonObject();
                String jsonString = jsonObject.toString();
                MindSparkQuestionTypeDropdown mindSparkQuestionTypeDropdown = gson.fromJson(jsonString, MindSparkQuestionTypeDropdown.class);

                /*Adding correctAnswer index as string in choice text value.*/
                ArrayList<Object> values = new ArrayList<>();
                values.addAll(mindSparkQuestionTypeDropdown.getChoices());
                questionChoices.add(new QuestionChoice(key, false, String.valueOf(mindSparkQuestionTypeDropdown.getCorrectAnswerIndex()), values));

            }

        }

        return questionChoices;
    }

    /*Contains logic to login user to Mind Spark environment and
     * getting response to use it for practice. */
    public Observable<ArrayList<MindSparkLoginResponse>> loginUserToMindSpark(final ArrayList<String> thirdPartyTopicIds) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<MindSparkLoginResponse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MindSparkLoginResponse>> e) throws Exception {
                ArrayList<MindSparkLoginResponse> responseArrayList = new ArrayList<>();
                for (int i = 0; i < thirdPartyTopicIds.size(); i++) {
                    MindSparkLoginRequest mindSparkLoginRequest = new MindSparkLoginRequest();
                    mindSparkLoginRequest.setAuthToken(AppPrefs.getIdToken(mContext));
                    mindSparkLoginRequest.setUserId(mAppUserModel.getApplicationUser().getMindSparkUserName());
                    mindSparkLoginRequest.setTopicId(thirdPartyTopicIds.get(i));
                    mindSparkLoginRequest.setVendorCode(MS_VENDOR);

                    Call<MindSparkLoginResponse> call = mNetworkModel.loginUserToMindSpark(mindSparkLoginRequest);
                    Response<MindSparkLoginResponse> response = call.execute();

                    if (response != null && response.isSuccessful()) {
                        MindSparkLoginResponse mindSparkLoginResponse = response.body();

                        assert mindSparkLoginResponse != null;
                        if (!TextUtils.isEmpty(mindSparkLoginResponse.getResultCode()) && mindSparkLoginResponse.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                            Log.e("MindSparkLoginResponse", "Successful");
                            responseArrayList.add(mindSparkLoginResponse);
                        } else if (mindSparkLoginResponse.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<MindSparkLoginResponse> response2 = call.clone().execute();
                            Log.e("MindSparkLoginResponse", "Failed");
                            if (response2 != null && response2.isSuccessful()) {
                                MindSparkLoginResponse mindSparkLoginResponse2 = response2.body();

                                assert mindSparkLoginResponse2 != null;
                                if (!TextUtils.isEmpty(mindSparkLoginResponse2.getResultCode()) && mindSparkLoginResponse2.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                                    Log.e("MindSparkLoginResponse", "Successful");
                                    saveMindSparkJwt(mindSparkLoginResponse.getMindSparkAuthToken());
                                    responseArrayList.add(mindSparkLoginResponse);
                                } else if (mindSparkLoginResponse2.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED)) {
                                    Log.e("MindSparkLoginResponse", "Failed");
                                    mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                                }

                            }
                        } else {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }

                    } else {
                        Log.e("MindSparkLoginResponse", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                }
                if (!responseArrayList.isEmpty()) {
                    saveMindSparkJwt(responseArrayList.get(0).getMindSparkAuthToken());
                    e.onNext(responseArrayList);
                } else {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*Contains logic to get question from Mind Spark environment
     *to use it for practice. */
    public Observable<MindSparkQuestionParent> getMindSparkQuestion(final MindSparkQuestionRequest mindSparkQuestionRequest) {
        return Observable.create(new ObservableOnSubscribe<MindSparkQuestionParent>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkQuestionParent> e) throws Exception {
                mindSparkQuestionRequest.setJWT(ThirdPartyPrefs.getMindSparkJsonWebToken(mContext));
                Call<MindSparkQuestionParent> call = mNetworkModel.getMindSparkQuestion(mindSparkQuestionRequest);
                Response<MindSparkQuestionParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkQuestionParent mindSparkQuestionResponse = response.body();

                    assert mindSparkQuestionResponse != null;
                    if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                        Log.e("MSQuestionResponse", "Successful");
                        e.onNext(mindSparkQuestionResponse);
                    } else if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(MS_RESULT_CODE_FAILURE)) {
                        throw new Exception(mContext.getString(R.string.mindSparkNoUnitMessageList));
                    } else if ((mindSparkQuestionResponse.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED) ||
                            mindSparkQuestionResponse.getResultCode().equals(MS_RESULT_CODE_JWT_EXPIRED))
                            && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkQuestionParent> response2 = call.clone().execute();
                        Log.e("MSQuestionResponse", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkQuestionParent mindSparkQuestionResponse2 = response2.body();

                            assert mindSparkQuestionResponse2 != null;
                            if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                                Log.e("MSQuestionResponse", "Successful");
                                e.onNext(mindSparkQuestionResponse2);
                            } else if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(MS_RESULT_CODE_FAILURE)) {
                                throw new Exception(mContext.getString(R.string.mindSparkNoUnitMessageList));
                            } else if (mindSparkQuestionResponse2.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED)) {
                                Log.e("MSQuestionResponse", "Failed");
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                            }

                        }
                    } else {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }

                } else {
                    Log.e("MindSparkLoginResponse", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

//                Gson gson = new Gson();
//                MindSparkQuestionParent response = gson.fromJson(mockJson, MindSparkQuestionParent.class);
//                e.onNext(response);
//                e.onComplete();


            }
        });

    }

    /*To submit current question response and fetch new question*/
    public Observable<MindSparkQuestionParent> submitAndFetchNewQuestion(final MindSparkQuestionSubmit mindSparkQuestionSubmit) {
        return Observable.create(new ObservableOnSubscribe<MindSparkQuestionParent>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkQuestionParent> e) throws Exception {

                Call<MindSparkQuestionParent> call = mNetworkModel.submitAndFetchNewQuestion(mindSparkQuestionSubmit);
                Response<MindSparkQuestionParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkQuestionParent mindSparkQuestionResponse = response.body();

                    assert mindSparkQuestionResponse != null;
                    if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                        Log.e("MSQuestionResponse", "Successful");
                        e.onNext(mindSparkQuestionResponse);
                    } else if (mindSparkQuestionResponse.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED) && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkQuestionParent> response2 = call.clone().execute();
                        Log.e("MSQuestionResponse", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkQuestionParent mindSparkQuestionResponse2 = response2.body();

                            assert mindSparkQuestionResponse2 != null;
                            if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                                Log.e("MSQuestionResponse", "Successful");
                                e.onNext(mindSparkQuestionResponse2);
                            } else if (mindSparkQuestionResponse2.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED)) {
                                Log.e("MSQuestionResponse", "Failed");
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                            }

                        }
                    } else {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }

                } else {
                    Log.e("MindSparkLoginResponse", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    /*To fetch all topic list of mind spark*/
    public Observable<MindSparkTopicResult> getMindSparkTopicResult() {
        return Observable.create(new ObservableOnSubscribe<MindSparkTopicResult>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkTopicResult> e) throws Exception {

                Call<MindSparkTopicResult> call = mNetworkModel.getMindSparkTopicResult(new MindSparkTopicListRequest(ThirdPartyPrefs.getMindSparkJsonWebToken(mContext)));
                Response<MindSparkTopicResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkTopicResult mindSparkTopicResult = response.body();

                    assert mindSparkTopicResult != null;
                    if (!TextUtils.isEmpty(mindSparkTopicResult.getResultCode()) && mindSparkTopicResult.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                        Log.e("MSTopicResult", "Successful");
                        e.onNext(mindSparkTopicResult);
                    } else if ((mindSparkTopicResult.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED) ||
                            mindSparkTopicResult.getResultCode().equals(MS_RESULT_CODE_JWT_EXPIRED))
                            && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkTopicResult> response2 = call.clone().execute();
                        Log.e("MSTopicResult", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkTopicResult mindSparkTopicResult2 = response2.body();

                            assert mindSparkTopicResult2 != null;
                            if (!TextUtils.isEmpty(mindSparkTopicResult2.getResultCode()) && mindSparkTopicResult2.getResultCode().equals(MS_RESULT_CODE_SUCCESS)) {
                                Log.e("MSTopicResult", "Successful");
                                e.onNext(mindSparkTopicResult2);
                            } else if (mindSparkTopicResult2.getResultCode().equals(MS_RESULT_CODE_UNAUTHORIZED)) {
                                Log.e("MSTopicResult", "Failed");
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));

                            }

                        }
                    } else {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }

                } else {
                    Log.e("MindSparkLoginResponse", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*Method to login student to logiqids*/
    public Observable<LogiqidsLoginResult> loginToLogiqids() {
        return Observable.create(new ObservableOnSubscribe<LogiqidsLoginResult>() {
            @Override
            public void subscribe(ObservableEmitter<LogiqidsLoginResult> e) throws Exception {

                Credentials credentials = new Credentials();
                credentials.setUserName("euroschool@dummy.com");
                credentials.setPassword("logic123");

                Call<LogiqidsLoginResult> call = mNetworkModel.loginToLogiqids(credentials);
                Response<LogiqidsLoginResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    LogiqidsLoginResult body = response.body();
                    e.onNext(body);
                    saveLogiqidsCredentials(body);
                    Log.e("LogiqidsLoginResult", "Success");
                } else {
                    Log.e("LogiqidsLoginResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*To fetch worksheet list from Logiqids server*/
    public Observable<LogiqidsWorksheetResult> getWorksheetList(final int userId, final int topicId) {
        return Observable.create(new ObservableOnSubscribe<LogiqidsWorksheetResult>() {
            @Override
            public void subscribe(ObservableEmitter<LogiqidsWorksheetResult> e) throws Exception {

                Call<LogiqidsWorksheetResult> call = mNetworkModel.getWorksheetList(userId, topicId);
                Response<LogiqidsWorksheetResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    LogiqidsWorksheetResult body = response.body();
                    e.onNext(body);
                    Log.e("LogiqidsWorksheetResult", "Success");
                } else {
                    Log.e("LogiqidsWorksheetResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*To fetch question from Logiqids server*/
    public Observable<Question> getQuestion(final int userId, final int topicId, final int worksheetId) {
        return Observable.create(new ObservableOnSubscribe<Question>() {
            @Override
            public void subscribe(ObservableEmitter<Question> e) throws Exception {

                Call<LogiqidsQuestionResult> call = mNetworkModel.getQuestion(userId, topicId, worksheetId);
                Response<LogiqidsQuestionResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    LogiqidsQuestionResult body = response.body();
                    if (body != null && body.getData() != null
                            && body.getData().getLogiqidsQuestionParent() != null) {
                        if (body.getData().getLogiqidsQuestionParent().getQuestion() != null) {
                            LogiqidsQuestionData logiqidsQuestionData = body.getData();
                            LogiqidsQuestionParent logiqidsQuestionParent = body.getData().getLogiqidsQuestionParent();
                            Question question = convertLogiQidsQuestionToLILQuestion(logiqidsQuestionData.getTotalQuestion(), logiqidsQuestionParent.getAnsweredList(), logiqidsQuestionParent.getQuestion());
                            e.onNext(question);
                        } else {
                            LogiqidsQuestionData logiqidsQuestionData = body.getData();
                            LogiqidsQuestionParent logiqidsQuestionParent = body.getData().getLogiqidsQuestionParent();
                            Question question = convertLogiQidsQuestionToLILQuestion(logiqidsQuestionData.getTotalQuestion(), logiqidsQuestionParent.getAnsweredList(), null);
                            e.onNext(question);
                        }


                    } else {
                        e.onError(new Throwable(mContext.getString(R.string.messageUnableToGetData)));
                    }
                    Log.e("LogiqidsQuestionResult", "Success");
                } else {
                    Log.e("LogiqidsQuestionResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }

    /*Here converting logiQids question schema to LIL question schema.*/
    private Question convertLogiQidsQuestionToLILQuestion(int totalQuestion, ArrayList<Boolean> answeredList, LogiqidsQuestion logiqidsQuestion) {
        Question question = new Question();

        if (logiqidsQuestion != null) {

            question.setQuestionText(logiqidsQuestion.getLogiqidsTextImageObject().getGroupText());
            question.setQuestionType(Question.TYPE_DISPLAY_RADIO);
            question.setUidQuestion(String.valueOf(logiqidsQuestion.getQuestionId()));
            question.setOrder(totalQuestion);//here adding totalQuestion value for ui help
            question.setComplexityLevel(String.valueOf(answeredList.size()));

            /*If answeredList size is equal to totalQuestion then sending true in setIsSelected key
             * To notify that worksheet has ended else sending false to continue attempt.*/
            if (answeredList.size() == totalQuestion) {
                question.setIsSelected(true);
            } else {
                question.setIsSelected(false);
            }

            if (!TextUtils.isEmpty(logiqidsQuestion.getLogiqidsTextImageObject().getImageUrl())) {
                Resource resource = new Resource();
                resource.setUrlMain(logiqidsQuestion.getLogiqidsTextImageObject().getImageUrl());
                question.setResources(new ArrayList<>(Collections.singleton(resource)));
            } else if (!TextUtils.isEmpty(logiqidsQuestion.getLogiqidsTextImageObject().getGroupImageUrl())) {
                Resource resource = new Resource();
                resource.setUrlMain(logiqidsQuestion.getLogiqidsTextImageObject().getGroupImageUrl());
                question.setResources(new ArrayList<>(Collections.singleton(resource)));
            }

            question.setQuestionHints(new ArrayList<QuestionHint>());

            question.setQuestionChoices(convertLogiQidsChoiceToLILChoice(logiqidsQuestion.getChoiceList()));
        } else {

            question.setQuestionText(ConstantUtil.BLANK);
            question.setQuestionType(ConstantUtil.BLANK);
            question.setUidQuestion(ConstantUtil.BLANK);
            question.setOrder(totalQuestion);//here adding totalQuestion value for ui help
            question.setComplexityLevel(String.valueOf(answeredList.size()));

            /*If answeredList size is equal to totalQuestion then sending true in setIsSelected key
             * To notify that worksheet has ended else sending false to continue attempt.*/
            if (answeredList.size() == totalQuestion) {
                question.setIsSelected(true);
            } else {
                question.setIsSelected(false);
            }

            question.setQuestionChoices(new ArrayList<QuestionChoice>());
        }

        return question;
    }

    /*Here converting logiQids choices schema to LIL choices schema.*/
    private ArrayList<QuestionChoice> convertLogiQidsChoiceToLILChoice(ArrayList<LogiqidsTextImageObject> choiceList) {
        ArrayList<QuestionChoice> questionChoices = new ArrayList<>();
        for (int i = 0; i < choiceList.size(); i++) {
            LogiqidsTextImageObject logiqidsChoice = choiceList.get(i);

            QuestionChoice questionChoice = new QuestionChoice();
            questionChoice.setChoiceText(logiqidsChoice.getText());
            String id = Character.toString((char) ('A' + i));
            questionChoice.setChoiceId(id);

            if (!TextUtils.isEmpty(logiqidsChoice.getImageUrl())) {
                Resource resource = new Resource();
                resource.setUrlMain(logiqidsChoice.getImageUrl());
                questionChoice.setChoiceResource(resource);
            } else if (!TextUtils.isEmpty(logiqidsChoice.getGroupImageUrl())) {
                Resource resource = new Resource();
                resource.setUrlMain(logiqidsChoice.getGroupImageUrl());
                questionChoice.setChoiceResource(resource);
            }

            questionChoices.add(questionChoice);
        }

        return questionChoices;
    }

    /*To submit question response to Logiqids server*/
    public Observable<LogiqidsQuestionAttemptResult> submitQuestionResponse(final int userId, final int topicId, final int worksheetId, final LogiqidsQuestionAttemptRequest logiqidsQuestionAttemptRequest) {
        return Observable.create(new ObservableOnSubscribe<LogiqidsQuestionAttemptResult>() {
            @Override
            public void subscribe(ObservableEmitter<LogiqidsQuestionAttemptResult> e) throws Exception {

                Call<LogiqidsQuestionAttemptResult> call = mNetworkModel.submitQuestionResponse(userId, topicId, worksheetId, logiqidsQuestionAttemptRequest);
                Response<LogiqidsQuestionAttemptResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    LogiqidsQuestionAttemptResult body = response.body();
                    e.onNext(body);
                    Log.e("LogiqidsAttemptResult", "Success");
                } else {
                    Log.e("LogiqidsAttemptResult", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });

    }


    /*To fetch detail of wikiHow card*/
    public Observable<WikiHowParent> fetchWikiHowCardDetail(final String wikiHowId) {

        return Observable.create(new ObservableOnSubscribe<WikiHowParent>() {
            @Override
            public void subscribe(ObservableEmitter<WikiHowParent> e) throws Exception {
                Call<WikiHowParent> call = mNetworkModel.fetchWikiHowCardDetail(wikiHowId);
                Response<WikiHowParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("WikiHowData", "Successful");
                    e.onNext(response.body());
                } else {
                    Log.e("WikiHowData", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();
            }
        });

    }


    /*To fetch Geo-Gebra card detail list for Apply*/
    public Observable<ArrayList<TPCurriculumResponse>> fetchGeoGebraCardDetail(final ThirdPartyMapping thirdPartyMapping) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<TPCurriculumResponse>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<TPCurriculumResponse>> e) throws Exception {

                        Call<ArrayList<TPCurriculumResponse>> call = mNetworkModel.fetchGeoGebraCardDetail(thirdPartyMapping);
                        Response<ArrayList<TPCurriculumResponse>> response = call.execute();

                        if (response != null && response.isSuccessful()) {
                            ArrayList<TPCurriculumResponse> list = response.body();
                            Log.e("GeoGebraCardList1--", "Successful");
                            e.onNext(list);
                        } else if (response.code() == 404) {
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<ArrayList<TPCurriculumResponse>> response2 = call.clone().execute();
                            if (response2 != null && response2.isSuccessful()) {
                                ArrayList<TPCurriculumResponse> list2 = response2.body();
                                Log.e("GeoGebraCardList2--", "Successful");
                                e.onNext(list2);
                            } else if ((response2.code() == 401)) {
                                mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                            } else if (response2.code() == 404) {
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            } else {
                                Log.e("GeoGebraCardList2--", "Failed");
                                throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                            }
                        } else {
                            Log.e("GeoGebraCardList1--", "Failed");
                            throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                        }
                        e.onComplete();
                    }
                });

    }

}
