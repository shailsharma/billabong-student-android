package in.securelearning.lil.android.thirdparty;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.thirdparty.di.component.DaggerThirdPartyAppComponent;
import in.securelearning.lil.android.thirdparty.di.component.ThirdPartyBaseComponent;


public class InjectorThirdParty {
    public static final InjectorThirdParty INSTANCE = new InjectorThirdParty();

    protected ThirdPartyBaseComponent mComponent;

    public ThirdPartyBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(ThirdPartyBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorThirdParty() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerThirdPartyAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }
    }

}
