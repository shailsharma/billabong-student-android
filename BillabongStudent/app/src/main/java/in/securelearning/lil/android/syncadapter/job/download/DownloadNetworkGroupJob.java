package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.IdNameObject;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 30-Jan-19.
 */

public class DownloadNetworkGroupJob {

    @Inject
    Context mContext;

    /**
     * use to make database calls
     */
    @Inject
    JobModel mJobModel;

    /**
     * use to make network calls
     */
    @Inject
    NetworkModel mNetworkModel;

    protected int mLoginCount = 0;
    private int mSkip = 0;
    private int mLimit = 20;
    private boolean mUpdatePreference;

    public DownloadNetworkGroupJob() {
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
        mUpdatePreference = true;
    }


    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            Response<ArrayList<IdNameObject>> response = fetchFromNetwork(mSkip, mLimit).execute();

            if (response.isSuccessful()) {

                actionFetchSuccess(response.body());

            } else {

                actionFailure(response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void actionFailure(int code) {
        final int MAX_LOGIN_ATTEMPTS = 1;
        if (code == 401 && mLoginCount < MAX_LOGIN_ATTEMPTS) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                mLoginCount++;
                execute();
            }
        }
    }

    private Call<ArrayList<IdNameObject>> fetchFromNetwork(int skip, int limit) {
        return mNetworkModel.fetchNetworkGroup(skip, limit);
    }

    public void actionFetchSuccess(ArrayList<IdNameObject> results) {

        downloadFullGroupObject(results);
        mSkip += results.size();
        if (mLimit == results.size()) {
            execute();
        } else if (mUpdatePreference) {
            PrefManager.setShouldDownloadNetworkGroup(false, mContext);
        }

    }

    private void downloadFullGroupObject(ArrayList<IdNameObject> list) {
        for (IdNameObject object : list) {
            Group localGroup = mJobModel.fetchGroupFromObjectId(object.getId());
            if (TextUtils.isEmpty(localGroup.getObjectId())) {
                JobCreator.createDownloadGroupJob(object.getId(), ConstantUtil.GROUP_TYPE_NETWORK).execute();
            }

        }

    }
}
