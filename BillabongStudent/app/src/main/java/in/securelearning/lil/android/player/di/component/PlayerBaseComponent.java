package in.securelearning.lil.android.player.di.component;


import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.player.view.activity.PracticeQuestionActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningCardsActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.player.view.fragment.FeaturedCardListFragment;
import in.securelearning.lil.android.resources.view.activity.VideoPlayActivity;
import in.securelearning.lil.android.resources.view.activity.VimeoActivity;
import in.securelearning.lil.android.syncadapter.utils.TextToSpeechUtils;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public interface PlayerBaseComponent extends BaseComponent {

    void inject(RapidLearningSectionListActivity rapidLearningSectionListActivity);

    void inject(PlayerModel playerModel);

    void inject(RapidLearningCardsActivity rapidLearningCardsActivity);

    void inject(FeaturedCardListFragment featuredCardListFragment);

    void inject(VideoPlayActivity videoPlayActivity);

    void inject(VimeoActivity vimeoActivity);

    void inject(TextToSpeechUtils textToSpeechUtils);

    void inject(PracticeQuestionActivity practiceQuestionActivity);
}
