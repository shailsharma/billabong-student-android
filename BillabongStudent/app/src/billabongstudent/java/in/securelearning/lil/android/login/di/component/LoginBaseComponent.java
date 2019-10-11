package in.securelearning.lil.android.login.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.login.views.activity.LoginActivity;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public interface LoginBaseComponent extends BaseComponent {

    void inject(LoginActivity activity);

}
