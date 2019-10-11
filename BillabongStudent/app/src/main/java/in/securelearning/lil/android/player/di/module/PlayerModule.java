package in.securelearning.lil.android.player.di.module;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.model.UserCourseProgressModel;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.utils.TextToSpeechUtils;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */
@Module
public class PlayerModule {


    @Provides
    @ActivityScope
    public NetworkModel networkModel() {
        return new NetworkModel();
    }

    @Provides
    @ActivityScope
    public PlayerModel playerModel() {
        return new PlayerModel();
    }


    @Provides
    @ActivityScope
    public UserCourseProgressModel userCourseProgressModel() {
        return new UserCourseProgressModel();
    }


    @Provides
    @ActivityScope
    public TextToSpeechUtils textToSpeechUtils() {
        return new TextToSpeechUtils();
    }

}
