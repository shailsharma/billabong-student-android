package in.securelearning.lil.android.gamification.dataobject;

import java.io.Serializable;

/* Class create for gamification
this class will specify the action that we need to perform
on subject or on assignments or blank specify the only msg
gamiing object associated with bonus object
*** GmaificationEventObject will fire based on always criteria
*/
public class GamificationEvent implements Serializable {

    private int eventId;
    private String eventType;
    private String eventName;
    private int priority;
    private String message;
    private long timeAfterLogin;
    // where we have to show the action ie dashboard
    private String activity;
    // in dashboard- subjects
    private String subActivity;
    private int frequency;
    // can be day hours
    private String frequencyUnit;
    // action to be Initiate Bonus or blank or deduct Bonus
    private String actionToPerform;
    //action on which on which event will be fire
    private String onActionCriteria;
    private long msgDuration;
    private String msgDurationUnit;
    private boolean isBonusAvailable;
    private boolean isOptionAvailable;
    private boolean isPointsAvailable;
    private boolean isGamingEventDone;
    private String eventOccurrenceDate;
    private String eventCreateDate;
    private float gifXPosition;
    private boolean isMascotSpeak = true;
    private boolean isMascotShouldPlay = true;

    public String getBonusCalculateDate() {
        return bonusCalculateDate;
    }

    public void setBonusCalculateDate(String bonusCalculateDate) {
        this.bonusCalculateDate = bonusCalculateDate;
    }

    private float gifYPosition;
    private String bonusCalculateDate;

    // it is *2 it specify action to be taken associated  with bonus object
    private GamificationBonus bonusObject;
    private GamificationPoint mPointObject;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeAfterLogin() {
        return timeAfterLogin;
    }

    public void setTimeAfterLogin(long timeAfterLogin) {
        this.timeAfterLogin = timeAfterLogin;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getSubActivity() {
        return subActivity;
    }

    public void setSubActivity(String subActivity) {
        this.subActivity = subActivity;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(String frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public String getActionToPerform() {
        return actionToPerform;
    }

    public void setActionToPerform(String actionToPerform) {
        this.actionToPerform = actionToPerform;
    }

    public String getOnActionCriteria() {
        return onActionCriteria;
    }

    public void setOnActionCriteria(String onActionCriteria) {
        this.onActionCriteria = onActionCriteria;
    }

    public long getMsgDuration() {
        return msgDuration;
    }

    public void setMsgDuration(long msgDuration) {
        this.msgDuration = msgDuration;
    }

    public String getMsgDurationUnit() {
        return msgDurationUnit;
    }

    public void setMsgDurationUnit(String msgDurationUnit) {
        this.msgDurationUnit = msgDurationUnit;
    }

    public boolean isBonusAvailable() {
        return isBonusAvailable;
    }

    public void setBonusAvailable(boolean bonusAvailable) {
        isBonusAvailable = bonusAvailable;
    }

    public boolean isOptionAvailable() {
        return isOptionAvailable;
    }

    public void setOptionAvailable(boolean optionAvailable) {
        isOptionAvailable = optionAvailable;
    }

    public boolean isPointsAvailable() {
        return isPointsAvailable;
    }

    public void setPointsAvailable(boolean pointsAvailable) {
        isPointsAvailable = pointsAvailable;
    }

    public boolean isGamingEventDone() {
        return isGamingEventDone;
    }

    public void setGamingEventDone(boolean gamingEventDone) {
        isGamingEventDone = gamingEventDone;
    }

    public String getEventOccurrenceDate() {
        return eventOccurrenceDate;
    }

    public void setEventOccurrenceDate(String eventOccurrenceDate) {
        this.eventOccurrenceDate = eventOccurrenceDate;
    }

    public GamificationBonus getBonusObject() {
        return bonusObject;
    }

    public void setBonusObject(GamificationBonus bonusObject) {
        this.bonusObject = bonusObject;
    }

    public GamificationPoint getPointObject() {
        return mPointObject;
    }

    public void setPointObject(GamificationPoint pointObject) {
        mPointObject = pointObject;
    }

    public String getEventCreateDate() {
        return eventCreateDate;
    }

    public void setEventCreateDate(String eventCreateDate) {
        this.eventCreateDate = eventCreateDate;
    }

    public float getGifXPosition() {
        return gifXPosition;
    }

    public void setGifXPosition(float gifXPosition) {
        this.gifXPosition = gifXPosition;
    }

    public float getGifYPosition() {
        return gifYPosition;
    }

    public void setGifYPosition(float gifYPosition) {
        this.gifYPosition = gifYPosition;
    }

    public boolean isMascotSpeak() {
        return isMascotSpeak;
    }

    public void setMascotSpeak(boolean mascotSpeak) {
        isMascotSpeak = mascotSpeak;
    }

    public boolean isMascotShouldPlay() {
        return isMascotShouldPlay;
    }

    public void setMascotShouldPlay(boolean mascotShouldPlay) {
        isMascotShouldPlay = mascotShouldPlay;
    }
}