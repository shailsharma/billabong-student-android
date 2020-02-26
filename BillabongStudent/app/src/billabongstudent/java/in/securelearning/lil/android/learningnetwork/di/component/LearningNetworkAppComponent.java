package in.securelearning.lil.android.learningnetwork.di.component;

import dagger.Component;
import in.securelearning.lil.android.base.di.component.AppComponent;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.learningnetwork.di.module.LearningNetworkModule;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {LearningNetworkModule.class})
public interface LearningNetworkAppComponent extends LearningNetworkComponent {
}
