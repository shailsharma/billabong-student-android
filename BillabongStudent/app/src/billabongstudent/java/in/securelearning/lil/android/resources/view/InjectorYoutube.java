package in.securelearning.lil.android.resources.view;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.resources.di.component.DaggerResourcesAppComponent;
import in.securelearning.lil.android.resources.di.component.ResourcesBaseComponent;

/**
 * Created by Secure on 12-06-2017.
 */

public class InjectorYoutube {
    public static final InjectorYoutube INSTANCE = new InjectorYoutube();

    protected ResourcesBaseComponent mComponent;

    public ResourcesBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(ResourcesBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorYoutube() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerResourcesAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }
    }
}
