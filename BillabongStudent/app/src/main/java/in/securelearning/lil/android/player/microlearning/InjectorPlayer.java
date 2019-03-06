package in.securelearning.lil.android.player.microlearning;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.player.microlearning.di.component.DaggerPlayerAppComponent;
import in.securelearning.lil.android.player.microlearning.di.component.PlayerBaseComponent;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public class InjectorPlayer {
    public static final InjectorPlayer INSTANCE = new InjectorPlayer();

    protected PlayerBaseComponent mComponent;

    public PlayerBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(PlayerBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorPlayer() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerPlayerAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
        }
    }
}
