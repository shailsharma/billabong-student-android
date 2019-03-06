package in.securelearning.lil.android.syncadapter.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.BaseObject;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.utils.DocumentUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;

/**
 * Implementation of SyncServiceModelInterface.
 */
public class FlavorSyncServiceModel extends BaseModel {
    public final String TAG = this.getClass().getCanonicalName();

    @Inject
    Context mContext;
    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;

    public FlavorSyncServiceModel() {
        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public boolean isDownloadAllowed() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (PreferenceSettingUtilClass.isDownloadOnWiFi(mContext)) {
            return wifiInfo.isConnected();
        } else if (info != null && info.isAvailable() && info.isConnected()) {
            return true;
        }
        return false;
    }

    public <T extends BaseObject> T retrieveAssignments(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveAssignments(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveBlogs(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveBlogs(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveCourses(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveCourses(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveLearningNetwork(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveLearningNetwork(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrievePeriodicEvents(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrievePeriodicEvents(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public <T extends BaseObject> T retrieveNotifications(String docId, Class<T> aClass) {

        T t = GeneralUtils.getObjectFromMap(new DocumentUtils().getObjectMapFromProperties(mDatabaseQueryHelper.retrieveNotification(docId)), aClass);
        t.setDocId(docId);
        return t;
    }

    public void purgeInternalNotification(String docId) {
        mDatabaseQueryHelper.deleteNotifications(docId);
    }

    public String getCourseType(String type) {
        if (type.equalsIgnoreCase("digitalbook")) {
            return CourseDetailActivity.DATA_DB;
        } else if (type.equalsIgnoreCase("videocourse")) {
            return CourseDetailActivity.DATA_VC;
        } else if (type.toLowerCase().contains("map")) {
            return CourseDetailActivity.DATA_CM;
        } else if (type.toLowerCase().contains("interactiveimage")) {
            return CourseDetailActivity.DATA_II;
        } else if (type.toLowerCase().contains("pop")) {
            return CourseDetailActivity.DATA_PU;
        } else if (type.toLowerCase().contains("video")) {
            return CourseDetailActivity.DATA_IV;
        }
        return "";

    }

}



