package in.securelearning.lil.android.mindspark.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.QuestionChoice;
import in.securelearning.lil.android.base.dataobjects.QuestionHint;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.utils.MindSparkPrefs;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.mindspark.MindSparkConstants;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkLoginRequest;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkLoginResponse;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestion;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestionChoice;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestionParent;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestionRequest;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestionSubmit;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestionTypeBlank;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestionTypeDropdown;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkQuestionTypeMCQ;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkTopicListRequest;
import in.securelearning.lil.android.mindspark.dataobjects.MindSparkTopicResult;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.mindspark.MindSparkConstants.MS_VENDOR;
import static in.securelearning.lil.android.mindspark.MindSparkConstants.RESULT_CODE_FAILURE;
import static in.securelearning.lil.android.mindspark.MindSparkConstants.RESULT_CODE_JWT_EXPIRED;
import static in.securelearning.lil.android.mindspark.MindSparkConstants.RESULT_CODE_SUCCESS;
import static in.securelearning.lil.android.mindspark.MindSparkConstants.RESULT_CODE_UNAUTHORIZED;
import static in.securelearning.lil.android.mindspark.MindSparkConstants.TYPE_BLANK;
import static in.securelearning.lil.android.mindspark.MindSparkConstants.TYPE_DROPDOWN;
import static in.securelearning.lil.android.mindspark.MindSparkConstants.TYPE_MCQ;

public class MindSparkModel {

    @Inject
    FlavorNetworkModel mFlavorNetworkModel;

    @Inject
    Context mContext;

    @Inject
    AppUserModel mAppUserModel;

    public MindSparkModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
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

                    Call<MindSparkLoginResponse> call = mFlavorNetworkModel.loginUserToMindSpark(mindSparkLoginRequest);
                    Response<MindSparkLoginResponse> response = call.execute();

                    if (response != null && response.isSuccessful()) {
                        MindSparkLoginResponse mindSparkLoginResponse = response.body();

                        assert mindSparkLoginResponse != null;
                        if (!TextUtils.isEmpty(mindSparkLoginResponse.getResultCode()) && mindSparkLoginResponse.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                            Log.e("MindSparkLoginResponse", "Successful");
                            responseArrayList.add(mindSparkLoginResponse);
                        } else if (mindSparkLoginResponse.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) && SyncServiceHelper.refreshToken(mContext)) {
                            Response<MindSparkLoginResponse> response2 = call.clone().execute();
                            Log.e("MindSparkLoginResponse", "Failed");
                            if (response2 != null && response2.isSuccessful()) {
                                MindSparkLoginResponse mindSparkLoginResponse2 = response2.body();

                                assert mindSparkLoginResponse2 != null;
                                if (!TextUtils.isEmpty(mindSparkLoginResponse2.getResultCode()) && mindSparkLoginResponse2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                    Log.e("MindSparkLoginResponse", "Successful");
                                    saveMindSparkJwt(mindSparkLoginResponse.getMindSparkAuthToken());
                                    responseArrayList.add(mindSparkLoginResponse);
                                } else if (mindSparkLoginResponse2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
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

    /*Contains logic to login user to Mind Spark environment and
     * getting response to use it for practice. */
    public Observable<MindSparkQuestionParent> getMindSparkQuestion(final MindSparkQuestionRequest mindSparkQuestionRequest) {
        return Observable.create(new ObservableOnSubscribe<MindSparkQuestionParent>() {
            @Override
            public void subscribe(ObservableEmitter<MindSparkQuestionParent> e) throws Exception {
                mindSparkQuestionRequest.setJWT(MindSparkPrefs.getMindSparkJsonWebToken(mContext));
                Call<MindSparkQuestionParent> call = mFlavorNetworkModel.getMindSparkQuestion(mindSparkQuestionRequest);
                Response<MindSparkQuestionParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkQuestionParent mindSparkQuestionResponse = response.body();

                    assert mindSparkQuestionResponse != null;
                    if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                        Log.e("MSQuestionResponse", "Successful");
                        e.onNext(mindSparkQuestionResponse);
                    } else if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_FAILURE)) {
                        throw new Exception(mContext.getString(R.string.mindSparkNoUnitMessageList));
                    } else if ((mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) ||
                            mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_JWT_EXPIRED))
                            && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkQuestionParent> response2 = call.clone().execute();
                        Log.e("MSQuestionResponse", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkQuestionParent mindSparkQuestionResponse2 = response2.body();

                            assert mindSparkQuestionResponse2 != null;
                            if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                Log.e("MSQuestionResponse", "Successful");
                                e.onNext(mindSparkQuestionResponse2);
                            } else if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_FAILURE)) {
                                throw new Exception(mContext.getString(R.string.mindSparkNoUnitMessageList));
                            } else if (mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
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

//                String mockJson = "{\n" +
//                        "  \"resultCode\": \"C001\",\n" +
//                        "  \"resultMessage\": \"success\",\n" +
//                        "  \"sessionInformation\": {\n" +
//                        "    \"sessionID\": \"5cdaf391839af52ab819ad84\",\n" +
//                        "    \"sessionStartAt\": \"2019-05-14 22:27:53\",\n" +
//                        "    \"sessionType\": \"Home\"\n" +
//                        "  },\n" +
//                        "  \"permittedNavs\": {\n" +
//                        "    \"myProgress\": true,\n" +
//                        "    \"myTopics\": true,\n" +
//                        "    \"worksheets\": true,\n" +
//                        "    \"games\": true,\n" +
//                        "    \"myMindspark\": true,\n" +
//                        "    \"reward\": true,\n" +
//                        "    \"myFavourities\": true,\n" +
//                        "    \"mailbox\": true,\n" +
//                        "    \"myDetails\": true,\n" +
//                        "    \"help\": false,\n" +
//                        "    \"sparkies\": true,\n" +
//                        "    \"home\": true,\n" +
//                        "    \"message\": true,\n" +
//                        "    \"notification\": false\n" +
//                        "  },\n" +
//                        "  \"userInformation\": {\n" +
//                        "    \"name\": \"Demo 3c2\",\n" +
//                        "    \"avatar\": \"https://mindspark-rearch-assets.s3.amazonaws.com/profileImages/female.png\",\n" +
//                        "    \"section\": \"C\",\n" +
//                        "    \"grade\": 3,\n" +
//                        "    \"gender\": \"female\",\n" +
//                        "    \"language\": \"en-IN\",\n" +
//                        "    \"isRetail\": false,\n" +
//                        "    \"selectedTheme\": \"lowergrade\",\n" +
//                        "    \"groupID\": \"5b1d697a73641933a91723ce\"\n" +
//                        "  },\n" +
//                        "  \"redirectionData\": {\n" +
//                        "    \"endTopicFlag\": false,\n" +
//                        "    \"endTopicHigherLevel\": false,\n" +
//                        "    \"userTriggered\": false,\n" +
//                        "    \"sessionTimeExceededFlag\": false\n" +
//                        "  },\n" +
//                        "  \"redirectionCode\": null,\n" +
//                        "\n" +
//                        "  \"contentData\": {\n" +
//                        "    \"contentId\": \"59dc8e7a1c5edc24e4060bab\",\n" +
//                        "    \"contentType\": \"question\",\n" +
//                        "    \"contentMode\": \"regular\",\n" +
//                        "    \"contentSubMode\": \"learn\",\n" +
//                        "    \"contentSeqNum\": 2,\n" +
//                        "    \"contentParams\": [],\n" +
//                        "    \"data\": [\n" +
//                        "      {\n" +
//                        "        \"_id\": \"5c0dfc87421aa94ffd5f6c1a_en_1\",\n" +
//                        "        \"revisionNo\": \"1\",\n" +
//                        "        \"contentID\": \"5c0dfc87421aa94ffd5f6c1a\",\n" +
//                        "        \"langCode\": \"en\",\n" +
//                        "        \"questionBody\": \"<DIV>&#13;&#10;<DIV>Oxygen moves from higher concentration in the&nbsp;blood&nbsp;to lower concentration in the tissues.&nbsp;Carbon dioxide&nbsp;moves from higher concentration in the&nbsp;tissues&nbsp;to lower concentration in the blood. This occurs in the capillary bed by the process of <STRONG>diffusion</STRONG>.</DIV>&#13;&#10;<DIV>&nbsp;</DIV>&#13;&#10;<DIV>Having <STRONG>thin</STRONG> capillary walls compared to <STRONG>thick</STRONG> capillary walls will cause the diffusion to occur [dropdown_1]</DIV>&#13;&#10;<DIV>&nbsp;</DIV>&#13;&#10;<DIV>&nbsp;</DIV></DIV>\",\n" +
//                        "        \"quesVoiceover\": null,\n" +
//                        "        \"response\": {\n" +
//                        "          \"dropdown_1\": {\n" +
//                        "            \"type\": \"Dropdown\",\n" +
//                        "            \"choices\": [  \n" +
//                        "              {\n" +
//                        "                \"value\": \"at a faster rate\",\n" +
//                        "                \"displayAnswer\": null,\n" +
//                        "                \"fixed\": false,\n" +
//                        "                \"score\": 1,\n" +
//                        "                \"correct\": true\n" +
//                        "              },\n" +
//                        "              {\n" +
//                        "                \"value\": \"at a slower rate\",\n" +
//                        "                \"displayAnswer\": null,\n" +
//                        "                \"fixed\": false,\n" +
//                        "                \"score\": 0,\n" +
//                        "                \"correct\": false\n" +
//                        "              },\n" +
//                        "              {\n" +
//                        "                \"value\": \"at the same rate\",\n" +
//                        "                \"displayAnswer\": null,\n" +
//                        "                \"fixed\": false,\n" +
//                        "                \"score\": 0,\n" +
//                        "                \"correct\": false\n" +
//                        "              }\n" +
//                        "            ],\n" +
//                        "            \"correctAnswer\": 0,\n" +
//                        "            \"additionalAttributes\": {\n" +
//                        "              \"lowerBound\": 0,\n" +
//                        "              \"upperBound\": 1,\n" +
//                        "              \"defaultValue\": -1,\n" +
//                        "              \"maxChoice\": 1\n" +
//                        "            }\n" +
//                        "          },\n" +
//                        "          \"dropdown_2\": {\n" +
//                        "            \"type\": \"Dropdown\",\n" +
//                        "            \"choices\": [  \n" +
//                        "              {\n" +
//                        "                \"value\": \"at speed of 90km/h\",\n" +
//                        "                \"displayAnswer\": null,\n" +
//                        "                \"fixed\": false,\n" +
//                        "                \"score\": 1,\n" +
//                        "                \"correct\": false\n" +
//                        "              },\n" +
//                        "              {\n" +
//                        "                \"value\": \"at speed of 100km/h\",\n" +
//                        "                \"displayAnswer\": null,\n" +
//                        "                \"fixed\": false,\n" +
//                        "                \"score\": 0,\n" +
//                        "                \"correct\": true\n" +
//                        "              },\n" +
//                        "              {\n" +
//                        "                \"value\": \"at speed of 95km/h\",\n" +
//                        "                \"displayAnswer\": null,\n" +
//                        "                \"fixed\": false,\n" +
//                        "                \"score\": 0,\n" +
//                        "                \"correct\": false\n" +
//                        "              }\n" +
//                        "            ],\n" +
//                        "            \"correctAnswer\": 1,\n" +
//                        "            \"additionalAttributes\": {\n" +
//                        "              \"lowerBound\": 0,\n" +
//                        "              \"upperBound\": 1,\n" +
//                        "              \"defaultValue\": -1,\n" +
//                        "              \"maxChoice\": 1\n" +
//                        "            }\n" +
//                        "          }\n" +
//                        "        },\n" +
//                        "        \"explanation\": \"<DIV><SPAN id=displayanswer>-Diffusion occurs when there is a difference in the concentration across a membrane. Substances move from higher concentration to lower concentration till the concentration becomes equal in both the compartments. &#13;&#10;<DIV>-The rate of diffusion depends upon the thickness of the membrane the molecules must diffuse across. Molecules can easily diffuse across thinner membranes and hence the arteries branch out to form&nbsp;very thin capillaries so that diffusion can occur easily.</DIV>&#13;&#10;<DIV>-Therefore diffusion occurs faster across thinner walls than thicker walls.</DIV>&#13;&#10;<DIV>-Capillaries are very thin and fragile. Their walls&nbsp;are actually only one cell thick. They are so thin that blood cells can only pass through them one at a time as shown here. &#13;&#10;<DIV>&nbsp;</DIV>&#13;&#10;<DIV>[AHB_gt_159_2.png]</DIV></DIV></SPAN></DIV>\",\n" +
//                        "        \"explVoiceover\": null,\n" +
//                        "        \"isDynamic\": false,\n" +
//                        "        \"type\": \"D\",\n" +
//                        "        \"template\": \"Dropdown\",\n" +
//                        "        \"templateFile\": \"Dropdown.js\",\n" +
//                        "        \"trials\": \"1\",\n" +
//                        "        \"showEditorTool\": \"0\",\n" +
//                        "        \"hints\": [],\n" +
//                        "        \"conditions\": [],\n" +
//                        "        \"glossary\": [],\n" +
//                        "        \"tags\": [],\n" +
//                        "        \"attributes\": [],\n" +
//                        "        \"createdAt\": \"2018-12-10 05:41:27\",\n" +
//                        "        \"updatedAt\": \"2018-12-10 05:41:27\",\n" +
//                        "        \"sc\": 1\n" +
//                        "      }\n" +
//                        "    ],\n" +
//                        "    \"contentTranslationFlag\": false\n" +
//                        "  }\n" +
//                        "}";
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

                Call<MindSparkQuestionParent> call = mFlavorNetworkModel.submitAndFetchNewQuestion(mindSparkQuestionSubmit);
                Response<MindSparkQuestionParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkQuestionParent mindSparkQuestionResponse = response.body();

                    assert mindSparkQuestionResponse != null;
                    if (!TextUtils.isEmpty(mindSparkQuestionResponse.getResultCode()) && mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                        Log.e("MSQuestionResponse", "Successful");
                        e.onNext(mindSparkQuestionResponse);
                    } else if (mindSparkQuestionResponse.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkQuestionParent> response2 = call.clone().execute();
                        Log.e("MSQuestionResponse", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkQuestionParent mindSparkQuestionResponse2 = response2.body();

                            assert mindSparkQuestionResponse2 != null;
                            if (!TextUtils.isEmpty(mindSparkQuestionResponse2.getResultCode()) && mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                Log.e("MSQuestionResponse", "Successful");
                                e.onNext(mindSparkQuestionResponse2);
                            } else if (mindSparkQuestionResponse2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
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

                Call<MindSparkTopicResult> call = mFlavorNetworkModel.getMindSparkTopicResult(new MindSparkTopicListRequest(MindSparkPrefs.getMindSparkJsonWebToken(mContext)));
                Response<MindSparkTopicResult> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    MindSparkTopicResult mindSparkTopicResult = response.body();

                    assert mindSparkTopicResult != null;
                    if (!TextUtils.isEmpty(mindSparkTopicResult.getResultCode()) && mindSparkTopicResult.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                        Log.e("MSTopicResult", "Successful");
                        e.onNext(mindSparkTopicResult);
                    } else if ((mindSparkTopicResult.getResultCode().equals(RESULT_CODE_UNAUTHORIZED) ||
                            mindSparkTopicResult.getResultCode().equals(RESULT_CODE_JWT_EXPIRED))
                            && SyncServiceHelper.refreshToken(mContext)) {
                        Response<MindSparkTopicResult> response2 = call.clone().execute();
                        Log.e("MSTopicResult", "Failed");
                        if (response2 != null && response2.isSuccessful()) {
                            MindSparkTopicResult mindSparkTopicResult2 = response2.body();

                            assert mindSparkTopicResult2 != null;
                            if (!TextUtils.isEmpty(mindSparkTopicResult2.getResultCode()) && mindSparkTopicResult2.getResultCode().equals(RESULT_CODE_SUCCESS)) {
                                Log.e("MSTopicResult", "Successful");
                                e.onNext(mindSparkTopicResult2);
                            } else if (mindSparkTopicResult2.getResultCode().equals(RESULT_CODE_UNAUTHORIZED)) {
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

    /*Method to save Mind Spark Json Web Token to shared preference*/
    private void saveMindSparkJwt(String mindSparkAuthToken) {
        if (!TextUtils.isEmpty(mindSparkAuthToken)) {
            Log.e("MS_JWT--", mindSparkAuthToken);
            MindSparkPrefs.setMindSparkJsonWebToken(mContext, mindSparkAuthToken);
        }
    }

    public Question convertMSQuestionToLILQuestion(int questionOrder, String contentId, MindSparkQuestion mindSparkQuestion) {
        Question question = new Question();
        question.setOrder(questionOrder);
        question.setUidQuestion(contentId);
        question.setQuestionText(mindSparkQuestion.getQuestionTextBody());
//        question.setQuestionText("\"Sample\" +\n" +
//                "                        \"\" +\n" +
//                "                        \"The picture below can be folded in half such that the right and left halves are <span style=\\\"color: #000000;\\\"><strong>EXACTLY THE SAME</strong></span>. <br><br>Such a picture is said to have <span style=\\\"color: #ff6600;\\\"><strong>line symmetry</strong></span> and line&nbsp;<em>m</em>&nbsp;is called the <span style=\\\"color: #0000ff;\\\"><strong>line of symmetry</strong></span>.<br>&nbsp;<br>\" +\n" +
//                "                        \"\" +\n" +
//                "                        \"<iframe id='quesInteractive' src='https://d2tl1spkm4qpax.cloudfront.net/Enrichment_Modules/html5/questions/GEO/GEO_qcode_12609_1/src/index.html?question=2&language=english' height='400px' width='600px' frameborder='0' scrolling='no'></iframe>\" +\n" +
//                "                        \"\" +\n" +
//                "                        \"<br><br>Which&nbsp;of the above pictures has <span style=\\\"color: #ff6600;\\\"><strong>line symmetry</strong></span> with&nbsp;<em>m</em> as the <span style=\\\"color: #0000ff;\\\"><strong>line of symmetry</strong></span>?\" +\n");
        question.setExplanation(mindSparkQuestion.getQuestionExplanation());
        question.setProgressionRule(mindSparkQuestion.getRevisionNo());
        question.setQuestionHints(getQuestionHints(mindSparkQuestion.getQuestionHints()));
        question.setQuestionChoices(getQuestionChoices(mindSparkQuestion.getQuestionResponse()));
        question.setQuestionType(getQuestionType(mindSparkQuestion.getQuestionResponse()));

        return question;
    }

    private String getQuestionType(Map<String, Object> questionResponse) {
        for (String key : questionResponse.keySet()) {
            if (key.contains(TYPE_MCQ.toLowerCase())) {
                return Question.TYPE_DISPLAY_RADIO;
            } else if (key.contains(TYPE_BLANK.toLowerCase())) {
                return MindSparkConstants.TYPE_BLANK;
            } else if (key.contains(TYPE_DROPDOWN.toLowerCase())) {
                return TYPE_DROPDOWN;
            }
        }
        return null;
    }

    private ArrayList<QuestionHint> getQuestionHints(ArrayList<String> mindSparkQuestionHints) {
        ArrayList<QuestionHint> questionHints = new ArrayList<>();
        for (int i = 0; i < mindSparkQuestionHints.size(); i++) {
            questionHints.add(new QuestionHint(i, mindSparkQuestionHints.get(i)));
        }
        return questionHints;
    }

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

                int correctIndex = Integer.parseInt(MindSparkConstants.decodeBase64String(mindSparkQuestionTypeMCQ.getCorrectAnswer()));

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
                String correctAnswer = MindSparkConstants.decodeBase64String(mindSparkQuestionTypeBlank.getCorrectAnswer()).replaceAll("\\\\", "");
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

}
