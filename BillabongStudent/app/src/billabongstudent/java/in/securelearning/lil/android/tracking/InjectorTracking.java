package in.securelearning.lil.android.tracking;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.tracking.di.component.DaggerTrackingAppComponent;
import in.securelearning.lil.android.tracking.di.component.TrackingBaseComponent;

/**
 * Created by Secure on 26-04-2017.
 */

public class InjectorTracking {
    public static final InjectorTracking INSTANCE = new InjectorTracking();

    protected TrackingBaseComponent mComponent;

    public TrackingBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(TrackingBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorTracking() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerTrackingAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }
    }

}
