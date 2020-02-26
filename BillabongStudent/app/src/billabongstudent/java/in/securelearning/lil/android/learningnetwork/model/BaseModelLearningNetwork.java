package in.securelearning.lil.android.learningnetwork.model;

import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.di.component.LearningNetworkComponent;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class BaseModelLearningNetwork {
    private LearningNetworkComponent mLearningNetworkComponent;

    public LearningNetworkComponent getLearningNetworkComponent() {
        return mLearningNetworkComponent;
    }

    public BaseModelLearningNetwork() {
        InjectorLearningNetwork.INSTANCE.initializeComponent();
        mLearningNetworkComponent = InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent();
    }
}
