package in.securelearning.lil.android.app;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.firebase.FirebaseApp;

import java.io.File;

import javax.inject.Inject;

import in.securelearning.lil.android.base.App;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.gamification.model.MascotModel;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;

public class MyApplication extends App {
    @SuppressLint("StaticFieldLeak")
    private static MyApplication mMyApplication;

    @Inject
    FlavorHomeModel mFlavorHomeModel;
    @Inject
    MascotModel mMascotModel;

    public static MyApplication getInstance() {
        if (mMyApplication == null) {
            mMyApplication = new MyApplication();
        } else
            return mMyApplication;
        return mMyApplication;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        InjectorHome.INSTANCE.getComponent().inject(this);
        registerActivityLifecycleCallbacks(AppLifecycleHandler.getInstance());
        initializeWebPlayerInterface();
        AppLifecycleHandler.getInstance().mFlavorHomeModel = mFlavorHomeModel;
        if (mMascotModel != null) {
            mMascotModel.createGamificationEvent();
        }
    }


    /*Here initializing WebPlayerLiveModel with WebPlayerLiveModelInterface
     * for web players*/
    private void initializeWebPlayerInterface() {
        Context context = Injector.INSTANCE.getComponent().appContext();

        if (context instanceof App) {
            ((App) context).setWebPlayerLiveModel(new WebPlayerLiveModel());
        }

    }


    public boolean clearPicassoCache(Context baseContext) {
        try {
            if (null != baseContext && baseContext.getCacheDir() != null) {
                File cache = new File(baseContext.getCacheDir(), "picasso-cache");
                if (cache.exists() && cache.isDirectory()) {
                    return deleteDir(cache);
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
}
