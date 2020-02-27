package in.securelearning.lil.android.player.di.component;


import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.player.view.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PracticePlayerActivity;
import in.securelearning.lil.android.player.view.activity.QuizPlayerActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningCardsActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.player.view.activity.RevisionPlayerActivity;
import in.securelearning.lil.android.player.view.adapter.QuestionResourceAdapter;
import in.securelearning.lil.android.player.view.fragment.QuestionFeedbackFragment;
import in.securelearning.lil.android.syncadapter.utils.TextToSpeechUtils;

/**
 * Created by Prabodh Dhabaria on 14-11-2016.
 */

public interface PlayerBaseComponent extends BaseComponent {

    void inject(RapidLearningSectionListActivity rapidLearningSectionListActivity);

    void inject(PlayerModel playerModel);

    void inject(RapidLearningCardsActivity rapidLearningCardsActivity);

    void inject(TextToSpeechUtils textToSpeechUtils);

    void inject(PracticePlayerActivity practicePlayerActivity);

    void inject(QuizPlayerActivity quizPlayerActivity);

    void inject(QuestionResourceAdapter questionResourceAdapter);

    void inject(QuestionFeedbackFragment questionFeedbackFragment);

    void inject(RevisionPlayerActivity revisionPlayerActivity);

    void inject(PlayVideoFullScreenActivity playVideoFullScreenActivity);

    void inject(PlayVimeoFullScreenActivity playVimeoFullScreenActivity);

    void inject(PlayYouTubeFullScreenActivity playYouTubeFullScreenActivity);
}
