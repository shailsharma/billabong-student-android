package in.securelearning.lil.android.home;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.home.di.component.DaggerHomeAppComponent;
import in.securelearning.lil.android.home.di.component.HomeBaseComponent;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public class InjectorHome {
    public static final InjectorHome INSTANCE=new InjectorHome();

    protected HomeBaseComponent mComponent;

    public HomeBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(HomeBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorHome() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerHomeAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }
    }
}
