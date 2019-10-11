package in.securelearning.lil.android.gamification.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.gamification.dataobject.GamificationBonus;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.dataobject.GamificationSurvey;
import in.securelearning.lil.android.gamification.dataobject.GamificationSurveyDetail;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.GlobalConfigurationParent;
import in.securelearning.lil.android.syncadapter.dataobject.GlobalConfigurationRequest;
import in.securelearning.lil.android.syncadapter.model.FlavorNetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MascotModel {

    @Inject
    Context mAppContext;

    @Inject
    FlavorNetworkModel mFlavorNetworkModel;

    private ArrayList<GamificationEvent> eventList = new ArrayList<>();
    private ArrayList<GamificationBonus> bonusList = new ArrayList<>();

    public MascotModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    private void setGamificationEvent(int id, String eventName, String eventType, int priority, String msg, String activity, String subActivity, String action, boolean isBonusAvailable, boolean isPoint, boolean isOption, String eventOccurrenceTime, String criteria, int frequency, String frequencyUnit) {
        GamificationEvent event = new GamificationEvent();
        event.setEventId(id);
        event.setEventName(eventName);
        event.setEventType(eventType);
        event.setPriority(priority);
        event.setActivity(activity);
        event.setSubActivity(subActivity);
        event.setMessage(msg);
        event.setActionToPerform(action);
        // event.setOnActionCriteria("complete");
        event.setBonusAvailable(isBonusAvailable);
        event.setPointsAvailable(isPoint);
        event.setOptionAvailable(isOption);
        event.setFrequency(frequency);
        event.setFrequencyUnit(frequencyUnit);
        event.setMsgDuration(3000);
        event.setMsgDurationUnit("sec");
        event.setEventOccurrenceDate(eventOccurrenceTime);
        event.setOnActionCriteria(criteria);
        event.setGamingEventDone(false);
        event.setEventCreateDate(CommonUtils.getInstance().getCurrentTime());
        //event.setBonusCalculateDate(null);
        eventList.add(event);
        GamificationPrefs.saveGamificationData(mAppContext, eventList);
    }

    public void createGamificationBonusObject(GamificationBonus bonus, boolean isAvail) {

        bonusList.clear();
        bonus.setGamificationId(3);
        bonus.setBonusAvail(isAvail);
        bonusList.add(bonus);
        setBonusToGamificationEvent();


    }

    public int getEventPosition() {
        return GamificationPrefs.getEventPosition(mAppContext);
    }

    public void setEventPosition(int position) {
        GamificationPrefs.saveGamificationEventPosition(mAppContext, position);
    }

    public GamificationEvent getGamificationEventForPosition() {
        eventList = GamificationPrefs.getGamificationData(mAppContext);
        if (eventList != null && !eventList.isEmpty()) {
            return eventList.get(getEventPosition());
        }
        return null;
    }


    public GamificationBonus createGamificationBonusForServer(int gamificationId, int bonusId, String bonusActivity, String bonusSubActivity, String userId, String subjectId, String topicId, String expiryDate, String bonusType, int multiplier, String sectionId, String gradeId, String subjectName) {
        GamificationBonus bonus = new GamificationBonus();
        bonus.setBonusId(null);
        bonus.setUserId(userId);
        bonus.setSubjectId(subjectId);
        bonus.setSubjectName(subjectName);
        bonus.setStartDate(DateUtils.getCurrentISO8601DateString());
        bonus.setEndDate(expiryDate);
        bonus.setBonusType(bonusType);
        bonus.setMultiplier(multiplier);
        bonus.setGamificationId(gamificationId);
        bonus.setSectionId(null);
        bonus.setGradeId(null);
        bonusList.add(bonus);
        return bonus;
    }

    public GamificationSurvey createGamificationSurveyForServer(String userId, GamificationSurveyDetail detail) {
        GamificationSurvey survey = new GamificationSurvey();

        survey.setType("School Survey");
        survey.setUserId(userId);
        survey.setSurveyDetail(detail);
        return survey;
    }

    public Observable<GamificationBonus> saveGamificationBonusToServer(final GamificationBonus bonus) {

        return Observable.create(new ObservableOnSubscribe<GamificationBonus>() {
            @Override
            public void subscribe(ObservableEmitter<GamificationBonus> e) throws Exception {
                Call<GamificationBonus> call = mFlavorNetworkModel.saveBonus(bonus);
                Response<GamificationBonus> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("saveBonus", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mAppContext)) {
                    Response<GamificationBonus> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("saveBonus", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mAppContext.startActivity(LoginActivity.getUnauthorizedIntent(mAppContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("saveBonus", "Failed");
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    public Observable<GamificationBonus> isBonusForAnySubject(final String bonusId) {

        return Observable.create(new ObservableOnSubscribe<GamificationBonus>() {
            @Override
            public void subscribe(ObservableEmitter<GamificationBonus> e) throws Exception {
                Call<GamificationBonus> call = mFlavorNetworkModel.getBonus(bonusId);
                Response<GamificationBonus> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("getBonus", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mAppContext)) {
                    Response<GamificationBonus> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("getBonus", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mAppContext.startActivity(LoginActivity.getUnauthorizedIntent(mAppContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("saveBonus", "Failed");
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    public Observable<ResponseBody> saveSurveyData(final GamificationSurvey survey) {

        return Observable.create(new ObservableOnSubscribe<ResponseBody>() {
            @Override
            public void subscribe(ObservableEmitter<ResponseBody> e) throws Exception {
                Call<ResponseBody> call = mFlavorNetworkModel.saveGamificationSurvey(survey);
                Response<ResponseBody> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("saveSurveyData", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mAppContext)) {
                    Response<ResponseBody> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("saveSurveyData", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mAppContext.startActivity(LoginActivity.getUnauthorizedIntent(mAppContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("saveSurveyData", "Failed");
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                }
                e.onComplete();

            }
        });
    }

    public void setBonusCalculateDateGamificationEvent(String currentDate) {
        eventList = GamificationPrefs.getGamificationData(mAppContext);
        if (eventList != null && !eventList.isEmpty()) {
            for (GamificationEvent event : eventList) {
                if (event != null && event.isBonusAvailable() && !TextUtils.isEmpty(currentDate)) {
                    event.setBonusCalculateDate(currentDate);
                    GamificationPrefs.saveGamificationData(mAppContext, eventList);
                    break;
                }
            }
        }
    }

    public void setBonusToGamificationEvent() {
        eventList = GamificationPrefs.getGamificationData(mAppContext);
        if (eventList != null && !eventList.isEmpty()) {
            for (GamificationEvent event : eventList) {
                if (event != null && event.isBonusAvailable()) {
                    if (bonusList != null && !bonusList.isEmpty()) {
                        for (GamificationBonus bonus : bonusList) {
                            if (bonus != null && bonus.getGamificationId() == event.getEventId()) {
                                event.setBonusCalculateDate(DateUtils.getCurrentISO8601DateString());
                                event.setBonusObject(bonus);
                                GamificationPrefs.saveGamificationData(mAppContext, eventList);
                                break;
                            }
                        }
                    }

                }
            }

        }
    }


    public ArrayList<GamificationEvent> getGamificationEvent() {
        return GamificationPrefs.getGamificationData(mAppContext);
    }

    // Need to update event time
    public void completeEvent(GamificationEvent eventUpdate) {
        eventList = GamificationPrefs.getGamificationData(mAppContext);
        if (eventList != null && !eventList.isEmpty()) {
            for (GamificationEvent event : eventList) {
                if (event != null && event.getEventId() == eventUpdate.getEventId()) {
                    event.setEventOccurrenceDate(eventUpdate.getEventOccurrenceDate());
                    event.setGamingEventDone(true);
                    break;
                }
            }
            GamificationPrefs.saveGamificationData(mAppContext, eventList);
        }
    }

    public void createGamificationEvent() {
        try {
            if (getGamificationEvent() == null) {
                setGamificationEvent(1, "welcome", "welcome_message", 1, mAppContext.getString(R.string.snackbar_login_msg), "dashboard", "welcome", "msg", false, false, false, null, "morning", 1, "days");
                setGamificationEvent(2, "assignment", "notification", 2, mAppContext.getString(R.string.gamification_assignment_pending), "dashboard", "new_assignment_count", "msg", false, false, false, null, "new_assignment_count", 1, "days");
                setGamificationEvent(3, "bonus", "bonus", 3, mAppContext.getString(R.string.gamification_bonus), "dashboard", "subject", "msg", true, false, false, null, "performance<10", 5, "days");
                setGamificationEvent(4, "survey", "option", 1, mAppContext.getString(R.string.gamification_school_survey), "dashboard", "survey", "msg", false, false, true, null, "3pm", 1, "days");
                setGamificationEvent(5, "points", "points", 3, mAppContext.getString(R.string.gamification_practice_accuracy), "LRPA", "practise", "msg", false, true, false, null, "100% accuracy", 1, "days");
            } else if (getGamificationEvent() != null && !getGamificationEvent().isEmpty() && getGamificationEvent().size() <= 2) {
                GamificationPrefs.clearGamificationPrefs(mAppContext);
                setGamificationEvent(1, "welcome", "welcome_message", 1, mAppContext.getString(R.string.snackbar_login_msg), "dashboard", "welcome", "msg", false, false, false, null, "morning", 1, "days");
                setGamificationEvent(2, "assignment", "notification", 2, mAppContext.getString(R.string.gamification_assignment_pending), "dashboard", "new_assignment_count", "msg", false, false, false, null, "new_assignment_count", 1, "days");
                setGamificationEvent(3, "bonus", "bonus", 3, mAppContext.getString(R.string.gamification_bonus), "dashboard", "subject", "msg", true, false, false, null, "performance<10", 5, "days");
                setGamificationEvent(4, "survey", "option", 1, mAppContext.getString(R.string.gamification_school_survey), "dashboard", "survey", "msg", false, false, true, null, "3pm", 1, "days");
                setGamificationEvent(5, "points", "points", 3, mAppContext.getString(R.string.gamification_practice_accuracy), "LRPA", "practise", "msg", false, true, false, null, "100% accuracy", 1, "days");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /*To fetch bonus configuration for gamification*/
    public Observable<GlobalConfigurationParent> fetchBonusConfiguration() {
        return Observable.create(new ObservableOnSubscribe<GlobalConfigurationParent>() {
            @Override
            public void subscribe(ObservableEmitter<GlobalConfigurationParent> e) throws Exception {
                GlobalConfigurationRequest globalConfigurationRequest = new GlobalConfigurationRequest();
                globalConfigurationRequest.setBonusValue(true);
                Call<GlobalConfigurationParent> call = mFlavorNetworkModel.fetchGlobalConfiguration(globalConfigurationRequest);
                Response<GlobalConfigurationParent> response = call.execute();

                if (response != null && response.isSuccessful()) {
                    Log.e("BonusConfig", "Successful");
                    e.onNext(response.body());
                } else if (response.code() == 404) {
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(mAppContext)) {
                    Response<GlobalConfigurationParent> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        Log.e("BonusConfig", "Successful");
                        e.onNext(response2.body());
                    } else if (response2.code() == 401) {
                        mAppContext.startActivity(LoginActivity.getUnauthorizedIntent(mAppContext));
                    } else if (response2.code() == 404) {
                        throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("BonusConfig", "Failed");
                    throw new Exception(mAppContext.getString(R.string.messageUnableToGetData));
                }

                e.onComplete();
            }
        });
    }

}
