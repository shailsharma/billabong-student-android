package in.securelearning.lil.android.syncadapter.job.validation;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserEarnBadges;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.model.PostModel;
import in.securelearning.lil.android.base.model.PostResponseModel;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.syncadapter.permission.PreferenceSettingUtilClass;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostResponseReceivedEvent;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static in.securelearning.lil.android.base.utils.FileUtils.checkIfResourceExists;


/**
 * Validate learning network post data job.
 *
 * @author Pushkar Raj
 */
public class ValidateLearningNetworkPostResponseJob extends BaseValidationJob<PostResponse> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    @Inject
    PostDataModel mPostDataModel;

    @Inject
    PostModel mPostModel;

    @Inject
    GroupModel mGroupModel;

    @Inject
    BadgesModel mBadgesModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    PostResponseModel mPostResponseModel;

    @Inject
    OgUtils mOgUtils;

    private boolean mShouldShowNotification;


    public ValidateLearningNetworkPostResponseJob(PostResponse dataObject, boolean shouldShowNotification) {
        super(dataObject);
        mShouldShowNotification = shouldShowNotification;

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the learning network post data
     */
    @SuppressLint("CheckResult")
    @Override
    public boolean executeValidation() {
        /*fetch learning network post data from database*/

        if (mDataObject != null && mDataObject.getResources() != null) {
            Log.e(TAG, "number of resource to download : " + mDataObject.getResources().size());

            final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

            int downloadCount = 0;
            mDataObject.setoGDataList(mOgUtils.extractUrls(mDataObject.getText()));

            /*download post resources*/
            for (String url : mDataObject.getResources()) {

                if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                        /*increment download count*/
                        downloadCount++;

                        url = filePathPrefix + resourceLocal.getDeviceURL();
                        Log.e(TAG, "post response image downloaded");
                    }
                    resourceLocal = null;
                }
            }

            if (downloadCount == mDataObject.getResources().size()) {
            /*save post response data with complete sync status
            and update status of all json's tagging this post response data*/
                mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);

                /*Update Uer profile in case if any badge assigned to the user who logged in*/
                if (mDataObject.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType()) && mDataObject.getAssignedPostResponseId() != null) {

                    /*For Badge assigned on post response*/
                    PostResponse postResponse1 = mPostDataModel.getPostResponseFromUidSync(mDataObject.getAssignedPostResponseId());
                    if (postResponse1 != null && postResponse1.getFrom().getId().equalsIgnoreCase(mAppUserModel.getObjectId())) {


                        Observable.just(mContext).subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) {

                                UserEarnBadges userEarnBadges = mBadgesModel.fetchUserEarnBadgeSync(mAppUserModel.getObjectId());
                                List<String> badgesEarned = userEarnBadges.getAssignedBadges();
                                Set<String> set = new HashSet(badgesEarned);
                                set.add(mDataObject.getAssignedBadgeId());
                                badgesEarned = new ArrayList(set);
                                userEarnBadges.setAssignedBadges(badgesEarned);
                                userEarnBadges.setUserId(mAppUserModel.getObjectId());
                                mBadgesModel.saveUserBadges(userEarnBadges);
                            }
                        });


                    }


                } else {

                    if (mDataObject.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType())) {
                        PostData postData = mPostDataModel.getPostDataFromUidSync(mDataObject.getPostID());

                        /*For Badge assigned on post response*/
                        if (mDataObject != null && postData.getFrom().getId().equalsIgnoreCase(mAppUserModel.getObjectId())) {
                            Observable.just(mContext).subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object o) {

                                    UserEarnBadges userEarnBadges = mBadgesModel.fetchUserEarnBadgeSync(mAppUserModel.getObjectId());
                                    List<String> badgesEarned = userEarnBadges.getAssignedBadges();
                                    Set<String> set = new HashSet(badgesEarned);
                                    set.add(mDataObject.getAssignedBadgeId());
                                    badgesEarned = new ArrayList(set);
                                    userEarnBadges.setAssignedBadges(badgesEarned);
                                    userEarnBadges.setUserId(mAppUserModel.getObjectId());
                                    mBadgesModel.saveUserBadges(userEarnBadges);
                                }
                            });
                        }

                    }


                }


            }

            if (mDataObject != null) {

                mPostResponseModel.saveNewPostResponseObject(mDataObject);

            }

            if (mShouldShowNotification) {
                mRxBus.send(new LoadNewPostResponseReceivedEvent(mDataObject));
            }


            return true;

        }

        return false;
    }

    @Override
    public void saveJson(PostResponse postResponse) {
        mJobModel.savePostResponse(postResponse);
    }

    @Override
    protected CharSequence getStartNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getStartNotificationTickerText() {
        return null;
    }

    @Override
    protected int getNotificationId() {
        return NotificationUtil.DOWNLOAD_LEARNING_NETWORK_GROUP_NOTIFICATION;
    }

    @Override
    protected CharSequence getFailedNotificationTitle() {
        return null;
    }

    @Override
    protected CharSequence getFailedNotificationTickerText() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationTitle() {
        return "Learning Network";
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return "New post response";
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {
        if (mDataObject.getType().equals(PostResponseType.TYPE_RECOMMEND.toString())) {
            return "Post liked by : " + mDataObject.getFrom().getName();
        } else if (mDataObject.getType().equals(PostResponseType.TYPE_COMMENT.toString())) {
            return mDataObject.getFrom().getName() + " : " + Html.fromHtml(mDataObject.getText());
        } else {
            return "Badge assigned by " + mDataObject.getFrom().getName();
        }
    }

    @Override
    protected int getProgressCountMax() {
        return 0;
    }

    @Override
    protected boolean isIndeterminate() {
        return false;
    }

    @Override
    protected boolean isNotificationEnabled() {
        if (PreferenceSettingUtilClass.isLearningNetwork(mContext)) {
            return !mDataObject.getFrom().getId().equals(mAppUserModel.getObjectId());
        } else {
            return false;
        }

    }

    @Override
    public int getNotificationResourceId() {
        return R.drawable.learning_network_w;
    }

    @Override
    public void showDownloadStartNotification() {

    }

    @Override
    public void showDownloadFailedNotification() {

    }

    @Override
    public void showDownloadSuccessfulNotification() {
        if (!mDataObject.getType().equals("Favourite") && mShouldShowNotification) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(getNotificationResourceId())
                            .setLargeIcon(getLargeNotificationBitmap())
                            .setColor(getSmallBackgroundColor())
                            .setTicker(getSuccessfulNotificationTickerText())
                            .setAutoCancel(true)
                            .setContentTitle(getSuccessfulNotificationTitle())
                            .setContentText(getSuccessfulNotificationText());
            if (isNotificationSoundEnabled()) {
                builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
                builder.setOnlyAlertOnce(true);
                Uri uri = getNotificationSoundUri();

                if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                    builder.setSound(uri);
                }
            }
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addParentStack(PostListActivity.class);
            stackBuilder.addNextIntent(PostListActivity.getIntentForPostList(mContext, mDataObject.getTo().getId(), false));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                assert notifyMgr != null;
                notifyMgr.createNotificationChannel(channel);
            }
            assert notifyMgr != null;
            notifyMgr.notify(getNotificationId(), builder.build());
        }

    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    /**
     * get list of all resources to download
     *
     * @return list of  resources
     */
    public List<String> getListOfAllResourcesToDownload(PostResponse postResponse) {
        /*list of all resources tagged in post postResponse */

        /*list of resources to download*/
        List<String> listToDownload = new ArrayList<>();

        /*find resources to download*/
        for (String resource : postResponse.getResources()) {
            /*if resource does not exist on disk*/
            if (!checkIfResourceExists(resource)) {
                /*add for download*/
                listToDownload.add(resource);
            }
        }
        return listToDownload;
    }

}
