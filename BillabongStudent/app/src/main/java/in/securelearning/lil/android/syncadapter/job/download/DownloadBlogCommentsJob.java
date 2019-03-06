package in.securelearning.lil.android.syncadapter.job.download;

import java.util.List;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.BlogComment;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import retrofit2.Call;

/**
 * Created by Prabodh Dhabaria on 21-12-2016.
 */

public class DownloadBlogCommentsJob extends BaseDownloadArrayJob<ArrayList<BlogComment>> {
    public DownloadBlogCommentsJob(String objectId) {
        super(objectId);

        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void createValidationJobs(ArrayList<BlogComment> blogComments) {

    }

    @Override
    public Call<ArrayList<BlogComment>> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchBlogComments(objectId);
    }

    @Override
    public ArrayList<BlogComment> save(ArrayList<BlogComment> blogComments) {
        for (BlogComment blogComment :
                blogComments) {
            blogComment.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
            blogComment = mJobModel.saveBlogComment(blogComment);
        }
        return blogComments;
    }

    @Override
    public List<String> getObjectIdList(ArrayList<BlogComment> blogComments) {
        List<String> strings = new java.util.ArrayList<>();

        for (BlogComment blogComment :
                blogComments) {
            strings.add(blogComment.getObjectId());
        }

        return strings;
    }
}
