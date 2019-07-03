package in.securelearning.lil.android.syncadapter.job.validation;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.PostModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostReceivedEvent;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Validate learning network post data job.
 *
 * @author Pushkar Raj
 */
public class ValidateLearningNetworkPostDataJob extends BaseValidationJob<PostData> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    PostModel mPostModel;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    @Inject
    OgUtils mOgUtils;

    private boolean mShouldShowNotification;

    public ValidateLearningNetworkPostDataJob(PostData dataObject, boolean shouldShowNotification) {
        super(dataObject);
        mShouldShowNotification = shouldShowNotification;

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the learning network post data
     */
    @Override
    public boolean executeValidation() {
        /*fetch learning network post data from database*/
        // PostData postData = mJobModel.fetchPostDataFromObjectId(mDataObject.getObjectId());

        Log.e(TAG, "number of post resource to download : " + mDataObject.getPostResources().size());
        final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

        int downloadCount = 0;
        mDataObject.setoGDataList(mOgUtils.extractUrls(mDataObject.getPostText()));
        for (Resource resource : mDataObject.getPostResources()) {

            String url = resource.getUrlMain();
            /*download Resource*/

            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = filePathPrefix + resourceLocal.getDeviceURL();

                    //     mDataObject.setPostResources(mDataObject.getPostResources().set(i,url));
                    resource.setDeviceURL(url);
                    saveJson(mDataObject);

                    Log.e(TAG, "resources image downloaded");
                }
                resourceLocal = null;
            }

            url = null;
        }
        if (downloadCount == mDataObject.getPostResources().size()) {

            if (mDataObject.getLastMessageTime() != null) {
                if (isDateAfter(mDataObject))
                    mDataObject.setLastMessageTime(DateUtils.convertrIsoDate(mDataObject.getCreatedTime()));
            } else
                mDataObject.setLastMessageTime(DateUtils.convertrIsoDate(mDataObject.getCreatedTime()));


            /*save postdata with complete sync status
            and update status of all json's tagging this postdata*/
            mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);


            if (mDataObject != null && mDataObject.getTo() != null && mDataObject.getTo().getId() != null) {
                //Fetching group and update creations time and save again
//                Group group = mGroupModel.fetchGroupFromUUidSync(mDataObject.getTo().getId());

//                group.setLastMessageTime(DateUtils.convertrIsoDate(mDataObject.getCreatedTime()));
//                group.setLastPostText(mDataObject.getPostText());
//                group.setLastPostPostedBy(mDataObject.getFrom().getName());

//                if (mDataObject.getPostResources() != null && !mDataObject.getPostResources().isEmpty()) {
//                    String resource = mDataObject.getPostResources().get(0).getDeviceURL();
//                    String mimeType = URLConnection.guessContentTypeFromName(resource);
//                    if (mimeType.contains("image"))
//                        group.setResourceType(PostResourceType.IMAGE.getPostresourceType());
//                    else if (mimeType.contains("video"))
//                        group.setResourceType(PostResourceType.VIDEO.getPostresourceType());
//                    else if (mimeType.contains("audio"))
//                        group.setResourceType(PostResourceType.AUDIO.getPostresourceType());
//                    else if (mimeType.contains("pdf"))
//                        group.setResourceType(PostResourceType.PDF.getPostresourceType());
//
//                    resource = null;
//                    mimeType = null;
//                } else
//                    group.setResourceType(null);


//                Log.d(TAG, "---beore save post text---" + mDataObject.getPostText());
//                Log.d(TAG, "---beore save group text---" + group.getLastPostText());
//                mGroupModel.saveGroup(group);
            }
            /*send new post downloaded event*/
            // TODO: 10-Aug-17 create entry new
            mDataObject.setDocId("");
            mPostModel.saveNewPostObject(mDataObject);
            mRxBus.send(new LoadNewPostReceivedEvent(mDataObject.getTo().getId()));


            return true;
        }

        return false;
    }

    private boolean isDateAfter(PostData postData) {
        if (DateUtils.convertrIsoDate(postData.getCreatedTime()).after(postData.getLastMessageTime()))
            return true;
        else
            return false;
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
        return "New post";
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

    @SuppressLint("CheckResult")
    @Override
    public void showDownloadSuccessfulNotification() {
        if (mShouldShowNotification) {

            mPostDataLearningModel.getNewPostListByGroupId(mDataObject.getTo().getId(), 0, 6)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PostData>>() {
                @Override
                public void accept(final ArrayList<PostData> posts) throws Exception {
                    final ArrayList<String> postInfo = new ArrayList<String>();
                    if (posts.size() > 0) {
                        for (int i = 0; i < posts.size(); i++) {
                            String postBy = posts.get(i).getFrom().getName();
                            String post = posts.get(i).getPostText();
                            postInfo.add(postBy + " : " + Html.fromHtml(post).toString().trim());
                        }
                        mPostDataLearningModel.getUnreadPostCountForGroup(posts.get(0).getTo().getId())
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer newCount) throws Exception {
                                if (posts.size() == 1) {
                                    String title = posts.get(0).getTo().getName() + " (" + String.valueOf(newCount) + " new post)";
                                    showPostNotification(title, postInfo, posts.get(0).getTo().getId());
                                } else {
                                    String title = posts.get(0).getTo().getName() + " (" + String.valueOf(newCount) + " new posts)";
                                    showPostNotification(title, postInfo, posts.get(0).getTo().getId());
                                }


                            }
                        });

                    }

                }

            });
        }

    }

    private void showPostNotification(String title, ArrayList<String> postInfo, String to) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(TextUtils.join("\n", postInfo)))
                .setSmallIcon(R.drawable.learning_network_w)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notification_icon))
                .setColor(ContextCompat.getColor(mContext, R.color.notification_small_background))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(TextUtils.join("\n", postInfo));
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
        stackBuilder.addNextIntent(PostListActivity.getIntentForPostList(mContext, to, false));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(to.hashCode(), PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(resultPendingIntent);

//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(mContext);
//        managerCompat.notify(to.hashCode(), builder.build());

        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationUtil.NOTIFICATION_CHANNEL_ID, NotificationUtil.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notifyMgr.createNotificationChannel(channel);
        }
        notifyMgr.notify(to.hashCode(), builder.build());
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationText() {
        return mDataObject.getTo().getName() + " : " + Html.fromHtml(mDataObject.getPostText());
    }

    /**
     * save quiz
     *
     * @param postData to save
     */

    public void saveJson(PostData postData) {
        mJobModel.saveLearningNetworkPostData(postData);
    }


}
