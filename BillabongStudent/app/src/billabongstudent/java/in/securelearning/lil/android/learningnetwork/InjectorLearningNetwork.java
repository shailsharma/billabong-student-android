package in.securelearning.lil.android.learningnetwork;

import in.securelearning.lil.android.base.Injector;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.learningnetwork.di.component.DaggerLearningNetworkAppComponent;
import in.securelearning.lil.android.learningnetwork.di.component.LearningNetworkComponent;
import in.securelearning.lil.android.learningnetwork.di.module.LearningNetworkModule;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public enum InjectorLearningNetwork {

    INSTANCE;

    protected LearningNetworkComponent mLearningNetworkComponent;

    public LearningNetworkComponent getLearningNetworkComponent() {
        initializeComponent();
        return mLearningNetworkComponent;
    }

    public void setLearningNetworkComponent(LearningNetworkComponent learningNetworkComponent) {
        mLearningNetworkComponent = learningNetworkComponent;
    }

    public void initializeComponent() {
        if (mLearningNetworkComponent == null) {
            mLearningNetworkComponent = DaggerLearningNetworkAppComponent.builder()
                    .appComponent((AppComponent) Injector.INSTANCE.getComponent())
                    .learningNetworkModule(new LearningNetworkModule())
                    .build();
        }
    }

}
