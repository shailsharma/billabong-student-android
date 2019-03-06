package in.securelearning.lil.android.courses;

import android.content.Context;

import in.securelearning.lil.android.base.App;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.courses.di.component.CoursesBaseComponent;
import in.securelearning.lil.android.courses.di.component.DaggerCoursesAppComponent;
import in.securelearning.lil.android.courses.di.module.CoursesModule;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public class InjectorCourses {

    public static final InjectorCourses INSTANCE = new InjectorCourses();

    protected CoursesBaseComponent mComponent;

    public CoursesBaseComponent getComponent() {
        Context context = Injector.INSTANCE.getComponent().appContext();
        if (context != null && context instanceof App) {
            ((App) context).setWebPlayerLiveModel(new WebPlayerLiveModel());
        }
        initializeComponent();
        return mComponent;
    }

    public void setComponent(CoursesBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorCourses() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerCoursesAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }

    }
}
