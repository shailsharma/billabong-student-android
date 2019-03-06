package in.securelearning.lil.android.resources.view.activity;

/**
 * Created by Chaitendra on 12-Mar-18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.cordova.CordovaActivity;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FabBinding;
import in.securelearning.lil.android.assignments.views.activity.AssignActivity;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.events.GenerateSubmissionEvent;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.player.microlearning.InjectorPlayer;


/**
 * Created by Prabodh Dhabaria on 06-12-2016.
 */

public class VimeoActivity extends CordovaActivity {

    @Inject
    RxBus mRxBus;

    private static final String OBJECT = "object";
    private static final String ASSIGNMENT_RESPONSE_ID = "assignmentResponseId";
    private String mAssignmentResponseId;
    private FavouriteResource mFavouriteResource;
    FabBinding mBinding;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.fab.setVisibility(View.VISIBLE);
        } else {
            mBinding.fab.setVisibility(View.GONE);

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);

        handleIntent();
        if (appView != null) {
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.fab, (ViewGroup) appView.getView().getParent(), true);

            if (PermissionPrefsCommon.getDashboardTeacherViewPermission(this)) {
                mBinding.fab.setVisibility(View.VISIBLE);
                mBinding.fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent mIntent = AssignActivity.getLaunchIntentForResource(getBaseContext(),
                                mFavouriteResource.getObjectId(),
                                mFavouriteResource.getTitle(),
                                mFavouriteResource.getType(),
                                mFavouriteResource.getMetaInformation(),
                                mFavouriteResource.getUrlMain(),
                                mFavouriteResource.getUrlThumbnail(),
                                mFavouriteResource.getDuration(),
                                mFavouriteResource.getReference());
                        startActivity(mIntent);
                    }
                });
            } else {
                mBinding.fab.setVisibility(View.GONE);
            }
        }


    }

    private void handleIntent() {
        if (getIntent() != null) {
            mFavouriteResource = (FavouriteResource) getIntent().getSerializableExtra(OBJECT);
            mAssignmentResponseId = getIntent().getStringExtra(ASSIGNMENT_RESPONSE_ID);

            String url = "";
            if (!TextUtils.isEmpty(mFavouriteResource.getSourceURL())) {
                url = mFavouriteResource.getSourceURL();
            } else if (!TextUtils.isEmpty(mFavouriteResource.getUrlMain())) {
                url = mFavouriteResource.getUrlMain();
            } else if (!TextUtils.isEmpty(mFavouriteResource.getReference())) {
                url = mFavouriteResource.getReference();
            } else if (!TextUtils.isEmpty(mFavouriteResource.getName())) {
                url = mFavouriteResource.getName();
            } else {
                finishActivity();
            }
            String title = mFavouriteResource.getTitle();
            String subject = mFavouriteResource.getMetaInformation().getSubject().getName();
            String grade = mFavouriteResource.getMetaInformation().getGrade().getName();
            String topic = mFavouriteResource.getMetaInformation().getTopic().getName();
            String learningLevel = mFavouriteResource.getMetaInformation().getLearningLevel().getName();


            if (!TextUtils.isEmpty(url)) {
                String[] urlId = url.split("/");
                if (urlId != null && urlId.length > 0) {
                    String id = url.replace("https://vimeo.com/", "");
                    loadUrl("file:///android_asset/vimeoIndex.html?id=" + id +
                            "&color=D02C3C" +
                            "&title=" + title +
                            "&subject=" + subject +
                            "&topic=" + topic +
                            "&ll=" + learningLevel +
                            "&grade=" + grade + "");
                    if (!TextUtils.isEmpty(mAssignmentResponseId)) {
                        mRxBus.send(new GenerateSubmissionEvent(mAssignmentResponseId));
                    }
                    return;
                }

            } else {
                finishActivity();
            }
        } else {
            finishActivity();
        }
    }

    public static Intent getStartIntent(Context context, FavouriteResource resource) {
        Intent intent = new Intent(context, VimeoActivity.class);
        intent.putExtra(OBJECT, resource);
        return intent;
    }

    public static Intent getStartIntent(Context context, FavouriteResource resource, String assignmentResponseId) {
        Intent intent = new Intent(context, VimeoActivity.class);
        intent.putExtra(OBJECT, resource);
        intent.putExtra(ASSIGNMENT_RESPONSE_ID, assignmentResponseId);
        return intent;
    }

    private void finishActivity() {
        Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
        finish();
    }

}
