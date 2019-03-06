package in.securelearning.lil.android.syncadapter.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.assignments.views.activity.AssignmentDetailActivity;
import in.securelearning.lil.android.assignments.views.activity.AssignmentStudentActivity;
import in.securelearning.lil.android.assignments.views.fragment.AssignmentStudentFragment;
import in.securelearning.lil.android.base.dataobjects.AssignmentStudent;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.dataobjects.TimeUtils;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.model.SyncServiceModel;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Created by Chaitendra on 15-Sep-17.
 */

public class ReminderService extends IntentService {

    private static final int LIMIT = 3;
    private static final int DATE_RANGE = 2;
    public static final String INTENT_LIMIT = "limit";
    public static final String INTENT_DATE_RANGE = "dateRange";
    private static final String ACTION_FETCH_ASSIGNMENTS = "in.securelearning.lil.android.syncadapter.service.action.FETCH_ASSIGNMENTS";
    private static final String ACTION_ALARM = "in.securelearning.lil.android.syncadapter.service.action.ALARM";
    private boolean mIsHasOverdue = false;
    private boolean mIsHasDueToday = false;
    private boolean mIsHasDueTomorrow = false;

    @Inject
    SyncServiceModel mSyncServiceModel;

    public ReminderService() {
        super("ReminderService");
    }

    public static Intent getReminderAlarmIntent(Context context) {
        Intent intent = new Intent(context, ReminderService.class);
        intent.putExtra(INTENT_LIMIT, LIMIT);
        intent.putExtra(INTENT_DATE_RANGE, DATE_RANGE);
        intent.setAction(ACTION_ALARM);
        return intent;
    }

    public static void startReminderIntentService(Context context) {
        startReminderIntentService(context, LIMIT, DATE_RANGE);
    }

    public static void startReminderIntentService(Context context, int limit, int dateRange) {
        Intent intent = new Intent(context, ReminderService.class);
        intent.putExtra(INTENT_LIMIT, limit);
        intent.putExtra(INTENT_DATE_RANGE, dateRange);
        intent.setAction(ACTION_FETCH_ASSIGNMENTS);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (PreferenceSettingUtilClass.isAssignment(getBaseContext())) {
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals(ACTION_FETCH_ASSIGNMENTS)) {
                    int limit = intent.getExtras().getInt(INTENT_LIMIT);
                    int dateRange = intent.getExtras().getInt(INTENT_DATE_RANGE);
                    getTopThreeNotSubmittedAssignment(limit, dateRange);
                } else {
                    PreferenceSettingUtilClass.setAssignmentReminderAlarm(getBaseContext(), false);
                    int limit = intent.getExtras().getInt(INTENT_LIMIT);
                    int dateRange = intent.getExtras().getInt(INTENT_DATE_RANGE);
                    getTopThreeNotSubmittedAssignment(limit, dateRange);

                }
            }
            handleLastAction();
        }
    }

    private void handleLastAction() {
        setAlarm(getBaseContext());
    }

    private void getTopThreeNotSubmittedAssignment(int limit, int dateRange) {
        long date = DateUtils.getSecondsForMorningFromDate(AssignmentStudentFragment.getDateFromString(getDayAfterTomorrowDate(dateRange)));
        getDueAssignments("", AssignmentStudentFragment.getSelectedDateToString(date), "", 0, limit);

    }

    private String getDayAfterTomorrowDate(int dateRange) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, dateRange);
        long date = DateUtils.getSecondsForMorningFromDate(calendar.getTime());
        return DateUtils.getISO8601DateStringFromSeconds(date);
    }

    private void getDueAssignments(final String fromDate, final String toDate, final String subject, final int skip, final int limit) {

        ArrayList<AssignmentStudent> assignmentStudents = mSyncServiceModel.getNotSubmittedAssignmentList(fromDate, toDate, subject, skip, limit);
        if (assignmentStudents.size() > 0) {
            ArrayList<String> assignmentInfo = new ArrayList<String>();
            String notificationTitle = "";
            for (int i = 0; i < assignmentStudents.size(); i++) {
                String title = assignmentStudents.get(i).getAssignmentTitle();
                String assignmentDueDate = assignmentStudents.get(i).getAssignmentDueDate();
                String date = DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(assignmentDueDate));
                String todayDate = DateUtils.getCurrentISO8601DateString();

                if (TimeUtils.compare(assignmentDueDate, todayDate) == -1) {
                    mIsHasOverdue = true;
                } else if (TimeUtils.compare(assignmentDueDate, todayDate) == 0) {
                    mIsHasDueToday = true;
                } else if (TimeUtils.compare(assignmentDueDate, todayDate) == 1) {
                    mIsHasDueTomorrow = true;
                }
                assignmentInfo.add(title + " DUE ON " + date.substring(0, date.length() - 4));
            }

            if (assignmentStudents.size() == 1) {
                notificationTitle = getBaseContext().getString(R.string.string_assignment);
            } else {
                notificationTitle = getBaseContext().getString(R.string.string_assignments);
            }

            if (mIsHasOverdue) {
                showReminderNotificationForAssignment(assignmentInfo, notificationTitle + " Overdue", assignmentStudents);
            } else if (mIsHasDueToday) {
                showReminderNotificationForAssignment(assignmentInfo, notificationTitle + " Due Today", assignmentStudents);
            } else if (mIsHasDueTomorrow) {
                showReminderNotificationForAssignment(assignmentInfo, notificationTitle + " Due Tomorrow", assignmentStudents);
            }
        }else {
            NotificationManager nMgr = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(NotificationUtil.REMINDER_ASSIGNMENT_PENDING);
        }
    }

    private void showReminderNotificationForAssignment(ArrayList<String> assignmentInfo, String title, ArrayList<AssignmentStudent> assignmentStudents) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),NotificationUtil.NOTIFICATION_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(TextUtils.join("\n", assignmentInfo)))
                .setSmallIcon(R.drawable.logo_assignment_w)
                .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.notification_icon))
                .setColor(ContextCompat.getColor(getBaseContext(), R.color.notification_small_background))
                .setOngoing(true)
                .setContentTitle(title)
                .setOnlyAlertOnce(true)
                .setSound(null)
                .setContentText(TextUtils.join("\n", assignmentInfo));

        if (assignmentStudents.size() == 1) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
            stackBuilder.addParentStack(AssignmentDetailActivity.class);
            stackBuilder.addNextIntent(AssignmentDetailActivity.startAssignmentDetailActivity(getBaseContext(), assignmentStudents.get(0).getObjectId(), assignmentStudents.get(0).getDocId()));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(resultPendingIntent);
        } else {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
            stackBuilder.addParentStack(AssignmentStudentActivity.class);
            stackBuilder.addNextIntent(AssignmentStudentActivity.getStartIntent(getBaseContext()));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(resultPendingIntent);
        }

//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getBaseContext());
//        managerCompat.notify(NotificationUtil.REMINDER_ASSIGNMENT_PENDING, builder.build());

        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(NotificationUtil.REMINDER_ASSIGNMENT_PENDING, builder.build());
    }

    private void setAlarm(Context context) {
        if (!PreferenceSettingUtilClass.isAssignmentReminderAlarm(context)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);

            PendingIntent pendingIntent = PendingIntent.getService(context, 0, getReminderAlarmIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            PreferenceSettingUtilClass.setAssignmentReminderAlarm(context, true);
        }
    }
}
