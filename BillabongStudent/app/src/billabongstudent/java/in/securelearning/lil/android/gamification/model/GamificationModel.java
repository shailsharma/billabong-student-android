package in.securelearning.lil.android.gamification.model;

import android.content.Context;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;

//Saving and getting the Event
public class GamificationModel {
    @Inject
    Context mAppContext;
    private ArrayList<GamificationEvent> eventList = new ArrayList<>();

    public GamificationModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    private void setGamificationEvent(int id, String eventName, String eventType, int priority, String msg, String activity, String subActivity, String action, boolean isBonusAvailable, boolean isPoint, boolean isOption, String eventOccurrenceTime, String criteria) {
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
        event.setFrequency(1);
        event.setFrequencyUnit("day");
        event.setMsgDuration(3000);
        event.setMsgDurationUnit("sec");
        event.setEventOccurrenceDate(eventOccurrenceTime);
        event.setOnActionCriteria(criteria);
        event.setGamingEventDone(false);
        event.setEventCreateDate(CommonUtils.getInstance().getCurrentTime());
        eventList.add(event);
        GamificationPrefs.saveGamificationData(mAppContext, eventList);
    }

    public ArrayList<GamificationEvent> getGamificationEvent() {
        return GamificationPrefs.getGamificationData(mAppContext);
    }

    // Need to update event time
    public void completeEvent(GamificationEvent eventUpdate) {
        eventList = GamificationPrefs.getGamificationData(mAppContext);
        if (eventList != null && !eventList.isEmpty()) {
            for (GamificationEvent event : eventList) {
                if (event.getEventId() == eventUpdate.getEventId()) {
                    event.setEventOccurrenceDate(eventUpdate.getEventOccurrenceDate());
                    event.setGamingEventDone(true);
                    break;
                }
            }
            GamificationPrefs.saveGamificationData(mAppContext, eventList);
        }
    }

    public void createGamificationEvent() {
        //  PermissionPrefs.clearGamificationPrefs(mAppContext);
        try {
            if (getGamificationEvent() == null) {
                setGamificationEvent(1, "welcome", "message", 1, mAppContext.getString(R.string.snackbar_login_msg), "dashboard", null, "msg", false, false, false, null, "morning");
                setGamificationEvent(2, "assignment", "notification", 2, mAppContext.getString(R.string.gamification_assignment_pending), "dashboard", "new_assignment_count", "msg", false, false, false, null, "new_assignment_count");
            } else if (getGamificationEvent().size() == 0) {
                setGamificationEvent(1, "welcome", "message", 1, mAppContext.getString(R.string.snackbar_login_msg), "dashboard", null, "msg", false, false, false, null, "morning");
                setGamificationEvent(2, "assignment", "notification", 2, mAppContext.getString(R.string.gamification_assignment_pending), "dashboard", "new_assignment_count", "msg", false, false, false, null, "new_assignment_count");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
