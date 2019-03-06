package in.securelearning.lil.android.home.model;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.events.LoadAnnouncementNdActivityListEvent;
import in.securelearning.lil.android.assignments.events.LoadCalendarListEvent;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.db.DatabaseHandler;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DocumentUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.syncadapter.service.MessageService;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.*;

/**
 * Created by Cp on 1/7/2017.
 */


public class CalendarEventModel {

    @Inject
    Context mAppContext;

    @Inject
    CalEventModel mCalEventModel;

    @Inject
    GroupModel mGroupModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    RxBus mRxBus;

    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    public CalendarEventModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);


    }

    public void saveEvent(CalendarEvent calendarEvent) {
        Map<String, Object> docContent = new DocumentUtils().getDocumentFromObject(DatabaseHandler.DOC_TYPE_CALENDAR_EVENT, calendarEvent);
        try {
            String docId = mDatabaseQueryHelper.createInLearningNetwork(docContent);
            calendarEvent.setDocId(docId);
            createInternalNotificationForCalendarEvent(calendarEvent, ACTION_TYPE_NETWORK_UPLOAD);
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void createInternalNotificationForCalendarEvent(CalendarEvent calendarEvent, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, calendarEvent.getAlias());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(calendarEvent.getEventType());
            internalNotification.setObjectDocId(calendarEvent.getDocId());
            internalNotification.setObjectId(calendarEvent.getAlias());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_CALENDAR_EVENT);
            internalNotification.setTitle(calendarEvent.getEventTitle());

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        MessageService.startActionFetchInternalNotification(mAppContext, internalNotification.getDocId());
    }

    public void fetchEventListByStartDate(ArrayList<String> startDateArrayList) {

        mCalEventModel.fetchEventListByStartDate(startDateArrayList).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<CalendarEvent>>() {
            @Override
            public void accept(final ArrayList<CalendarEvent> calendarEvents) {
                mRxBus.send(new LoadCalendarListEvent(calendarEvents));
            }

        });
    }

//    public void fetchAllAnnouncementNdActivityEventList(String mGroupObjectId) {
//
//        mCalEventModel.fetchAllAnnouncementNdActivityEventList(mGroupObjectId).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<CalendarEvent>>() {
//            @Override
//            public void accept(final ArrayList<CalendarEvent> calendarEvents) {
//
//
//                for (CalendarEvent calendarEvent : calendarEvents) {
//                    //Updating unread  count of post
//                    calendarEvent.setUnread(false);
//                    mCalEventModel.saveCalendarEvent(calendarEvent);
//                }
//
//                mRxBus.send(new LoadAnnouncementNdActivityListEvent(calendarEvents));
//            }
//
//        });
//    }

    public Observable<ArrayList<CalendarEvent>> getAnnouncementAndActivityEventList(final String startTime, final String endTime, final int skip, final int limit) {

        Observable<ArrayList<CalendarEvent>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<CalendarEvent>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CalendarEvent>> subscriber) {
                ArrayList<CalendarEvent> calendarEvents = mCalEventModel.getAllCalendarEvent(startTime, endTime, skip, limit);
                subscriber.onNext(calendarEvents);
                subscriber.onComplete();

            }
        });
        return observable;
    }

    /**
     * synchronous load of group list for the current user
     *
     * @return group list
     */
    public ArrayList<Group> getGroupListEvent() {
        return mGroupModel.getGroupListByUserUIdSync(mAppUserModel.getObjectId());
    }
}
