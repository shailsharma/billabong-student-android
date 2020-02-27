package in.securelearning.lil.android.app;

import in.securelearning.lil.android.app.di.component.AppBaseComponent;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.app.di.component.DaggerAppComponent;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public class InjectorApp {
    public static final InjectorApp INSTANCE = new InjectorApp();

    protected AppBaseComponent mComponent;

    public AppBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(AppBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorApp() {
    }

    static void initializeComponent() {

        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }

    }
}
