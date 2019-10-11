package in.securelearning.lil.android.courses;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.courses.di.component.CoursesBaseComponent;
import in.securelearning.lil.android.courses.di.component.DaggerCoursesAppComponent;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public class InjectorCourses {

    public static final InjectorCourses INSTANCE = new InjectorCourses();

    protected CoursesBaseComponent mComponent;

    public CoursesBaseComponent getComponent() {
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
