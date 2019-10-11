package in.securelearning.lil.android.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static AppLifecycleHandler instance;
    public FlavorHomeModel mFlavorHomeModel;
    private int resumed;
    private int started;
    private boolean isVisible = false;
    private boolean isInForeground = false;

    private AppLifecycleHandler() {
    }

    public static AppLifecycleHandler getInstance() {
        if (instance == null) {
            instance = new AppLifecycleHandler();
        }

        return instance;
    }

    private static boolean isApplicationVisible() {
        return AppLifecycleHandler.getInstance().started > 0;
    }

    public static boolean isApplicationInForeground() {
        return AppLifecycleHandler.getInstance().resumed > 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        setForeground((resumed > 0));
    }

    @Override
    public void onActivityPaused(Activity activity) {
        --resumed;
        setForeground((resumed > 0));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (started == 0) {
            if (mFlavorHomeModel != null) {
                callUserStatus(activity, ConstantUtil.TYPE_ACTIVE);
            }
        }
        ++started;
        setVisible((started > 0));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        --started;
        if (mFlavorHomeModel != null && !isApplicationVisible()) {
            callUserStatus(activity, ConstantUtil.TYPE_INACTIVE);
        }
        setVisible((started > 0));
    }

    @SuppressLint("CheckResult")
    private void callUserStatus(Activity activity, String status) {
        if (AppPrefs.isLoggedIn(activity) && GeneralUtils.isNetworkAvailable(activity)) {
            mFlavorHomeModel.checkUserStatus(status);
        }

    }

    private void setVisible(boolean visible) {
        if (isVisible == visible) {
            // no change
            return;
        }

        // visibility changed
        isVisible = visible;

        // take some action on change of visibility
    }

    private void setForeground(boolean inForeground) {
        if (isInForeground == inForeground) {
            // no change
            return;
        }

        // in foreground changed
        isInForeground = inForeground;

        // take some action on change of in foreground

    }
}