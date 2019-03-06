package in.securelearning.lil.android.syncadapter.job.validation;

import android.app.PendingIntent;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.resource.ResourceNetworkOperation;
import in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils;
import in.securelearning.lil.android.syncadapter.utils.NotificationUtil;

/**
 * Validate  group job.
 *
 * @author Pushkar Raj
 */
public class ValidateGroupJob extends BaseValidationJob<Group> {
    private final String TAG = this.getClass().getCanonicalName();
    @Inject
    ResourceNetworkOperation mResourceNetworkOperation;

    public ValidateGroupJob(Group dataObject) {
        super(dataObject);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    /**
     * execute validation of the  group
     */
    @Override
    public boolean executeValidation() {
        /*fetch  group from database*/
        // Group group = mJobModel.fetchGroupFromObjectId(mDataObject.getObjectId());

        //Groups user's pic download count
        int downloadResCount = getCountOfResourcesToBeDownload(mDataObject);

        /*if  group is available validate it*/
        if (mDataObject != null && mDataObject.getObjectId().equals(mDataObject.getObjectId())) {

            final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;

            Log.e(TAG, "------------------------------------------------------------------------------------");
            Log.e(TAG, "------------------------------------------------------------------------------------");
            Log.e(TAG, "------------------------------------------------------------------------------------");
            Log.e(TAG, String.format("\nexecuting group %s validation", mDataObject.getObjectId()));

        /*count to measure number of resources downloaded*/
            int downloadCount = 0;


            //Download group's thumbnail image
            String url = mDataObject.getThumbnail().getThumb();
            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = filePathPrefix + resourceLocal.getDeviceURL();

                    mDataObject.getThumbnail().setThumb(url);

                    mJobModel.saveGroup(mDataObject);

                    Log.e(TAG, "banner image downloaded");
                }
                resourceLocal = null;
            }

            //Download groups banner image
            url = mDataObject.getBanner().getThumb();
            if (url != null && !url.isEmpty() && !url.endsWith(File.separator)) {
                Resource resourceLocal = FileUtils.createResourceFromUrl(mDataObject.getObjectId(), url);

                if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                    downloadCount++;

                    url = filePathPrefix + resourceLocal.getDeviceURL();

                    mDataObject.getBanner().setThumb(url);

                    mJobModel.saveGroup(mDataObject);

                    Log.e(TAG, "banner image downloaded");
                }
                resourceLocal = null;
            }


            downloadCount += downloadGroupsMembersThumbnail(mDataObject);
            url = null;
        /*if all resources have been downloaded*/
            if (downloadCount == downloadResCount) {
            /*save quiz with complete sync status
            and update status of all json's tagging this quiz*/

                /*Increament unread post count to one*/
                //  mDataObject.setPostUnreadCount(mDataObject.getPostUnreadCount()+1);
                mDataObject.setLastPostText("You are now member of this group");
                mDataObject.setLastPostPostedBy("");
                mDataObject.setResourceType(null);
                mDataObject.setUnreadActivityCount(0);
                mDataObject.setUnreadAnnouncementCount(0);
                mDataObject.setUnreadRefeneceCount(0);
                mDataObject.setUnreadQueryCount(0);
                mDataObject.setLastMessageTime(mDataObject.getCreationTime());
                mJobModel.updateAndSaveCompleteSyncStatus(mDataObject);
                return true;
            }

            Log.e(TAG, "------------------------------------------------------------------------------------");
            Log.e(TAG, "------------------------------------------------------------------------------------");
            Log.e(TAG, "------------------------------------------------------------------------------------");

        }

        return false;
        //Download groups Post and post response
        //JobCreator.createDownloadGroupPostNResponseJob(mDataObject.getObjectId()).execute();

    }

    @Override
    public void saveJson(Group group) {
        mJobModel.saveGroup(group);
    }

    @Override
    protected PendingIntent getPendingIntent() {
        return null;
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
        return null;
    }

    @Override
    protected CharSequence getSuccessfulNotificationTickerText() {
        return null;
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
        return false;
    }

    @Override
    public int getNotificationResourceId() {
        return 0;
    }

    /*Dowload user's thumbnail for groups members and modretors*/
    private int downloadGroupsMembersThumbnail(Group group) {
        int downloadCount = 0;
        final String filePathPrefix = "file://" + mContext.getFilesDir() + File.separator;
        /*download  all members of groups and their pic*/
        for (GroupMember groupMember : group.getMembers()) {
            if (groupMember.getPic().getThumb() != null && !groupMember.getPic().getThumb().trim().isEmpty() && !groupMember.getPic().getThumb().endsWith(File.separator)) {

                String userPicCloudUrl = groupMember.getPic().getThumb();
                if (userPicCloudUrl != null && !userPicCloudUrl.isEmpty() && !userPicCloudUrl.endsWith(File.separator)) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(groupMember.getObjectId(), userPicCloudUrl);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                        downloadCount++;

                        userPicCloudUrl = filePathPrefix + resourceLocal.getDeviceURL();

                        groupMember.getPic().setThumb(userPicCloudUrl);

                        mJobModel.saveGroup(mDataObject);

                        Log.e(TAG, "group's member pic downloaded");
                    }
                    resourceLocal = null;
                }
                userPicCloudUrl = null;
            }
        }
        /*download all moderators user thumbnail*/
        for (Moderator moderator : group.getModerators()) {
            if (moderator.getPic() != null && !moderator.getPic().getThumb().trim().isEmpty() && !moderator.getPic().getThumb().endsWith(File.separator)) {
                String userPicCloudUrl = moderator.getPic().getThumb();
                if (userPicCloudUrl != null && !userPicCloudUrl.isEmpty() && !userPicCloudUrl.endsWith(File.separator)) {
                    Resource resourceLocal = FileUtils.createResourceFromUrl(moderator.getId(), userPicCloudUrl);

                    if (!resourceLocal.getDeviceURL().isEmpty() && mResourceNetworkOperation.downloadResource(mContext, resourceLocal)) {

                    /*increment download count*/
                        downloadCount++;

                        userPicCloudUrl = filePathPrefix + resourceLocal.getDeviceURL();

                        moderator.getPic().setThumb(userPicCloudUrl);

                        mJobModel.saveGroup(mDataObject);

                        Log.e(TAG, "groups moderator pic downloaded");
                    }
                    resourceLocal = null;
                }
                userPicCloudUrl = null;
            }
        }

        return downloadCount;
    }


    public int getCountOfResourcesToBeDownload(Group group) {

        int downloadResCount = 0;
        /*Fetch all members of groups and their pic*/
        for (GroupMember groupMember : group.getMembers()) {
            if (groupMember.getPic() != null && !groupMember.getPic().getThumb().trim().isEmpty() && !groupMember.getPic().getThumb().endsWith(File.separator)) {
                downloadResCount++;
            }
        }
        /*Fetch all moderators of groups*/
        for (Moderator moderator : group.getModerators()) {
            if (moderator.getPic() != null && !moderator.getPic().getThumb().trim().isEmpty() && !moderator.getPic().getThumb().endsWith(File.separator)) {
                downloadResCount++;
            }
        }

        if (group.getThumbnail() != null && !group.getThumbnail().getThumb().trim().isEmpty() && !group.getThumbnail().getThumb().endsWith(File.separator)) {
            downloadResCount++;
        }
        if (group.getBanner() != null && !group.getBanner().getThumb().trim().isEmpty() && !group.getBanner().getThumb().endsWith(File.separator)) {
            downloadResCount++;
        }


        return downloadResCount;
    }


    /**
     * save and update sync status
     *
     * @param group
     */
    public void updateAndSaveCompleteSyncStatus(Group group) {
        mJobModel.updateAndSaveCompleteSyncStatus(group);
    }


}
