package in.securelearning.lil.android.base;

import in.securelearning.lil.android.base.di.component.BaseComponentExt;
import in.securelearning.lil.android.base.di.component.DaggerAppBaseComponentExt;
import in.securelearning.lil.android.base.di.module.BaseModuleExt;

/**
 * Created by Prabodh Dhabaria on 15-11-2017.
 */

public class InjectorExt {
    public static final InjectorExt INSTANCE = new InjectorExt();
    protected BaseComponentExt mComponent;

    public BaseComponentExt getComponent() {
        return this.mComponent;
    }

    public void setComponent(BaseComponentExt appComponent) {
        this.mComponent = appComponent;
    }

    private InjectorExt() {
    }

    public static void initializeComponent(App app) {
        INSTANCE.mComponent = DaggerAppBaseComponentExt.builder()
                .baseModuleExt(new BaseModuleExt(app))
                .build();
    }
}
