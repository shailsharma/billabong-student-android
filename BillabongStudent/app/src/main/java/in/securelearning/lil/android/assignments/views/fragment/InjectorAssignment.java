package in.securelearning.lil.android.assignments.views.fragment;

import in.securelearning.lil.android.assignments.di.component.AssignmentBaseComponent;
import in.securelearning.lil.android.assignments.di.component.DaggerAssignmentAppComponent;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public class InjectorAssignment {
    public static final InjectorAssignment INSTANCE = new InjectorAssignment();

    protected AssignmentBaseComponent mComponent;

    private InjectorAssignment() {
    }

    public AssignmentBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(AssignmentBaseComponent appComponent) {
        mComponent = appComponent;
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerAssignmentAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }
    }
}
