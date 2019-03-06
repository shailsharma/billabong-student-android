package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.di.component.LearningNetworkComponent;

public class BaseFragmentLearningNetwork extends Fragment {


    private LearningNetworkComponent mLearningNetworkComponent;

    public LearningNetworkComponent getLearningNetworkComponent() {
        return mLearningNetworkComponent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.initializeComponent();
        mLearningNetworkComponent = InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent();


    }


}
