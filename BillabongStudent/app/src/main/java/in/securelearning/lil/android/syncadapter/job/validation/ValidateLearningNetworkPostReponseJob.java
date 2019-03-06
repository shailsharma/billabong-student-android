package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
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
public class ValidateLearningNetworkPostReponseJob extends BaseValidationJob<PostResponse> {
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

    public ValidateLearningNetworkPostReponseJob(PostResponse dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the learning network post data
     */
    @Override
    public boolean executeValidation() {
        /*fetch learning network post data from database*/
        // PostResponse postResponse = mJobModel.fetchPostResponseFromObjectId(mDataObject.getObjectId());

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
            /*save postdata with complete sync status
            and update status of all json's tagging this postdata*/
                mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);

                //Fetching post and update status to unread and save again
//                PostData postData = mPostDataModel.getPostDataFromUidSync(mDataObject.getPostID());
//                postData.setUnread(true);
                // mPostDataModel.savePostData(postData);

                //Fetching group and update creations time and save again
//                if (postData != null && postData.getTo() != null && postData.getTo().getId() != null) {
//                    Group group = mGroupModel.fetchGroupFromUUidSync(postData.getTo().getId());
//
//
//                    if (group != null && group.getLastMessageTime() != null) {
//                        if (DateUtils.convertrIsoDate(mDataObject.getCreatedTime()).after(group.getLastMessageTime()))
//                            group.setLastMessageTime(DateUtils.convertrIsoDate(mDataObject.getCreatedTime()));
//                    } else {
//                        group.setLastMessageTime(DateUtils.convertrIsoDate(mDataObject.getCreatedTime()));
//                        group.setLastPostText(mDataObject.getText());
////                        group.setPostContanisPhotos(false);
//                    }
//
//
//                    if (mDataObject.getType().equalsIgnoreCase(PostResponseType.TYPE_COMMENT.getPostResponseType())) {
//                        group.setLastPostText(mDataObject.getText());
//                        group.setLastPostPostedBy(mDataObject.getFrom().getName());
//                        group.setResourceType(null);
//                    } else {
////                        group.setLastPostPostedBy("");
////                        group.setResourceType(null);
////                        group.setLastPostText("");
//                    }
//
//
//                    if (postData.getLastMessageTime() != null) {
//                        if (postData.getLastMessageTime().after(DateUtils.convertrIsoDate(mDataObject.getCreatedTime())))
//                            postData.setLastMessageTime(DateUtils.convertrIsoDate(mDataObject.getCreatedTime()));
//                    } else
//                        postData.setLastMessageTime(DateUtils.convertrIsoDate(mDataObject.getCreatedTime()));
//
//
//                    if (postData != null)
//                        mPostDataModel.savePostData(postData);
//
//                    if (group != null)
//                        mGroupModel.saveGroup(group);
//                }



                    /*Update Uer profile in case if any badge assiged to the user who logged in*/
                if (mDataObject.getType().equalsIgnoreCase(PostResponseType.TYPE_BADGE.getPostResponseType()) && mDataObject.getAssignedPostResponseId() != null) {
                    //For Badge assiged on post response
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

                        //For Badge assiged on post
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
//            mDataObject.setDocId("");
            mPostResponseModel.saveNewPostResponseObject(mDataObject);
//            PostData postData = mPostModel.getObjectById(mDataObject.getPostID());
//            String postAlias = postData.getAlias();
            mRxBus.send(new LoadNewPostResponseReceivedEvent(mDataObject));
            //    mRxBus.send(new RefreshPostOnNewPostResponseReceived(postAlias));


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
            if (!mDataObject.getFrom().getId().equals(mAppUserModel.getObjectId())) {
                return true;
            } else {
                return false;
            }
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
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
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

//        NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(mContext);
//        mNotifyMgr.notify(getNotificationId(), builder.build());

        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(getNotificationId(), builder.build());
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    /**
     * get list of all resources to download
     *
     * @param postResponse
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
