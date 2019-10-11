package in.securelearning.lil.android.thirdparty.di.component;

import in.securelearning.lil.android.base.di.component.BaseComponent;
import in.securelearning.lil.android.thirdparty.model.ThirdPartyModel;
import in.securelearning.lil.android.thirdparty.views.activity.LogiqidsQuizPlayerActivity;
import in.securelearning.lil.android.thirdparty.views.activity.MindSparkAllTopicListActivity;
import in.securelearning.lil.android.thirdparty.views.activity.MindSparkPlayerActivity;

public interface ThirdPartyBaseComponent extends BaseComponent {

    void inject(ThirdPartyModel thirdPartyModel);

    void inject(MindSparkAllTopicListActivity mindSparkAllTopicListActivity);

    void inject(MindSparkPlayerActivity mindSparkPlayerActivity);

    void inject(LogiqidsQuizPlayerActivity logiqidsQuizPlayerActivity);
}
