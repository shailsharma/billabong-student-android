package in.securelearning.lil.android.resources.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import javax.inject.Inject;

import cn.jzvd.JZVideoPlayerStandard;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutVideoDescriptionPlayBinding;
import in.securelearning.lil.android.assignments.views.activity.AssignActivity;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.events.GenerateSubmissionEvent;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;

import static in.securelearning.lil.android.base.utils.ImageUtils.getScaledBitmapFromPath;

/**
 * Created by Chaitendra on 12-Mar-18.
 */

public class VideoPlayActivity extends AppCompatActivity {
    @Inject
    RxBus mRxBus;
    LayoutVideoDescriptionPlayBinding mBinding;
    private static final String OBJECT = "object";

    private static final String ASSIGNMENT_RESPONSE_ID = "assignmentResponseId";
    private String mAssignmentResponseId;

    public static Intent getStartIntent(Context context, FavouriteResource resource) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(OBJECT, resource);
        return intent;
    }

    public static Intent getStartIntent(Context context, FavouriteResource resource, String assignmentResponseId) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(OBJECT, resource);
        intent.putExtra(ASSIGNMENT_RESPONSE_ID, assignmentResponseId);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_video_description_play);
        handleIntent();
    }

    private void handleIntent() {
        if (getIntent() != null) {
            FavouriteResource resource = (FavouriteResource) getIntent().getSerializableExtra(OBJECT);
            mAssignmentResponseId = getIntent().getStringExtra(ASSIGNMENT_RESPONSE_ID);

            if (resource != null) {
                initializeUiAndListeners(resource);
            } else {
                finish();
                Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
            Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeUiAndListeners(FavouriteResource resource) {

        initializePlayer(resource);
        assignResource(resource);
        int color = PrefManager.getColorForSubject(VideoPlayActivity.this, resource.getMetaInformation().getSubject().getId());
        if (!TextUtils.isEmpty(resource.getTitle()))
            mBinding.textView.setText(resource.getTitle());
        if (!TextUtils.isEmpty(resource.getMetaInformation().getSubject().getName())) {
            mBinding.textViewSubejct.setText(resource.getMetaInformation().getSubject().getName());
            mBinding.textViewSubejct.setTextColor(color);
        }
        if (!TextUtils.isEmpty(resource.getMetaInformation().getTopic().getName())) {
            mBinding.textViewTopic.setText(resource.getMetaInformation().getTopic().getName());
        }
        if (!TextUtils.isEmpty(resource.getMetaInformation().getGrade().getName())) {
            mBinding.layoutGrade.setVisibility(View.VISIBLE);
            mBinding.textViewGrade.setText(resource.getMetaInformation().getGrade().getName());
            mBinding.textViewGrade.setTextColor(color);
        } else {
            mBinding.layoutGrade.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(resource.getMetaInformation().getLearningLevel().getName())) {
            mBinding.layoutLearningLavel.setVisibility(View.VISIBLE);
            mBinding.textViewLearningLavel.setText(resource.getMetaInformation().getLearningLevel().getName());
            mBinding.textViewLearningLavel.setTextColor(color);
        } else {
            mBinding.layoutLearningLavel.setVisibility(View.GONE);
        }
    }

    private void initializePlayer(FavouriteResource resource) {

        Bitmap bitmap = getScaledBitmapFromPath(getBaseContext().getResources(), resource.getUrlMain());
        bitmap = Bitmap.createScaledBitmap(bitmap, 600, 340, false);
        mBinding.videoView.thumbImageView.setImageBitmap(bitmap);
        mBinding.videoView.setUp(resource.getUrlMain(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, resource.getUrlMain());
        mBinding.videoView.startButton.performClick();
        if (!TextUtils.isEmpty(mAssignmentResponseId)) {
            mRxBus.send(new GenerateSubmissionEvent(mAssignmentResponseId));
        }
    }

    private void assignResource(final FavouriteResource favouriteResource) {
        if (PermissionPrefsCommon.getDashboardTeacherViewPermission(this)) {
            mBinding.buttonAssignResource.setVisibility(View.VISIBLE);
            mBinding.buttonAssignResource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = AssignActivity.getLaunchIntentForResource(getBaseContext(),
                            favouriteResource.getObjectId(),
                            favouriteResource.getTitle(),
                            favouriteResource.getType(),
                            favouriteResource.getMetaInformation(),
                            favouriteResource.getUrlMain(),
                            favouriteResource.getUrlThumbnail(),
                            favouriteResource.getDuration(),
                            favouriteResource.getUrlMain());
                    startActivity(mIntent);
                }
            });
        } else {
            mBinding.buttonAssignResource.setVisibility(View.GONE);
        }
    }

}
