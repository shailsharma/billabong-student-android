package in.securelearning.lil.android.login;

import android.content.Context;
import android.webkit.WebView;

import in.securelearning.lil.android.base.App;
import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.login.di.component.DaggerLoginAppComponent;
import in.securelearning.lil.android.login.di.component.LoginBaseComponent;
import in.securelearning.lil.android.syncadapter.model.WebPlayerLiveModel;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public class InjectorLogin {
    public static final InjectorLogin INSTANCE=new InjectorLogin();

    protected LoginBaseComponent mComponent;

    public LoginBaseComponent getComponent() {
        initializeComponent();
        return mComponent;
    }

    public void setComponent(LoginBaseComponent appComponent) {
        mComponent = appComponent;
    }

    private InjectorLogin() {
    }

    static void initializeComponent() {
        if (INSTANCE.mComponent == null) {
            INSTANCE.mComponent = DaggerLoginAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .build();
            WebView webView = INSTANCE.mComponent.webView();
            Context context = Injector.INSTANCE.getComponent().appContext();
            if (context instanceof App) {
                ((App) context).setWebPlayerLiveModel(new WebPlayerLiveModel());
            }
        }

    }
}
