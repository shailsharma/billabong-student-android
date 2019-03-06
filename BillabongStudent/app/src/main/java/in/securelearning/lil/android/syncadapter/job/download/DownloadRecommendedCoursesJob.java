package in.securelearning.lil.android.syncadapter.job.download;

import android.content.Context;

import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.JobModel;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Prabodh Dhabaria on 11-02-2017.
 */

public class DownloadRecommendedCoursesJob implements Serializable {
    private final String TAG = this.getClass().getCanonicalName();
    private final int MAX_LOGIN_ATTEMPTS = 1;

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

    public DownloadRecommendedCoursesJob() {

        /*initialize the injection component*/
        InjectorSyncAdapter.INSTANCE.initializeComponent();

    }

    /**
     * execute downloading of the object
     */
    public void execute() {
        try {
            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<ArrayList<AboutCourse>> response = fetchFromNetworkDigitalBook().execute();

            /*if fetch if successful*/
            if (response.isSuccessful()) {

                /*handle the downloaded object*/
                actionFetchSuccess(response.body());

            } else {
                /*handle failure*/
                actionFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "" + e.getMessage());

        }
        try {
            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<ArrayList<AboutCourse>> response = fetchFromNetworkPopup().execute();

            /*if fetch if successful*/
            if (response.isSuccessful()) {

                /*handle the downloaded object*/
                actionFetchSuccess(response.body());

            } else {
                /*handle failure*/
                actionFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "" + e.getMessage());

        }
        try {
            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<ArrayList<AboutCourse>> response = fetchFromNetworkConceptMap().execute();

            /*if fetch if successful*/
            if (response.isSuccessful()) {

                /*handle the downloaded object*/
                actionFetchSuccess(response.body());

            } else {
                /*handle failure*/
                actionFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "" + e.getMessage());

        }
        try {
            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<ArrayList<AboutCourse>> response = fetchFromNetworkInteractiveImage().execute();

            /*if fetch if successful*/
            if (response.isSuccessful()) {

                /*handle the downloaded object*/
                actionFetchSuccess(response.body());

            } else {
                /*handle failure*/
                actionFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "" + e.getMessage());

        }
        try {
            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<ArrayList<AboutCourse>> response = fetchFromNetworkVideoCourse().execute();

            /*if fetch if successful*/
            if (response.isSuccessful()) {

                /*handle the downloaded object*/
                actionFetchSuccess(response.body());

            } else {
                /*handle failure*/
                actionFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "" + e.getMessage());

        }
    }

    /**
     * action to take when fetch is not successful
     *
     * @param code
     */
    public void actionFailure(int code) {
        if (code == 401 && mLoginCount < MAX_LOGIN_ATTEMPTS) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                mLoginCount++;
                execute();
            }
        }
    }

    /**
     * action to take when fetch is successful
     *
     * @param t downloaded object
     */
    public void actionFetchSuccess(ArrayList<AboutCourse> t) {

        /*set sync status of the object to json sync*/
        t = setSyncStatus(t, SyncStatus.JSON_SYNC);

        /*save the object into database*/
        save(t);

        /*create validation job for the download object*/
        createValidationJobs(t);
    }

    private ArrayList<AboutCourse> setSyncStatus(ArrayList<AboutCourse> t, SyncStatus jsonSync) {
        for (AboutCourse course :
                t) {
            course.setSyncStatus(jsonSync.toString());
        }
        return t;
    }


    public void createValidationJobs(ArrayList<AboutCourse> aboutCourses) {
        for (AboutCourse aboutCourse :
                aboutCourses) {
            aboutCourse.setJson(GeneralUtils.toGson(aboutCourse));
            JobCreator.createAboutCourseValidationJob(aboutCourse, false);
        }
    }

    public Call<ArrayList<AboutCourse>> fetchFromNetworkDigitalBook() {

        return mNetworkModel.getAboutCourseRecommendedDigitalBookOnline(5, 0);
    }

    public Call<ArrayList<AboutCourse>> fetchFromNetworkConceptMap() {

        return mNetworkModel.getAboutCourseRecommendedConceptMapOnline(5, 0);
    }

    public Call<ArrayList<AboutCourse>> fetchFromNetworkPopup() {

        return mNetworkModel.getAboutCourseRecommendedPopUpOnline(5, 0);
    }

    public Call<ArrayList<AboutCourse>> fetchFromNetworkInteractiveImage() {

        return mNetworkModel.getAboutCourseRecommendedInteractiveImageOnline(5, 0);
    }

    public Call<ArrayList<AboutCourse>> fetchFromNetworkVideoCourse() {

        return mNetworkModel.getAboutCourseRecommendedVideoCourseOnline(5, 0);
    }

    public ArrayList<AboutCourse> save(ArrayList<AboutCourse> aboutCourses) {
        for (AboutCourse aboutCourse :
                aboutCourses) {
            aboutCourse = mJobModel.saveAboutCourse(aboutCourse);
        }
        return aboutCourses;
    }

    /**
     * network call to send the list of object id
     * which were successfully synced with server
     *
     * @param objectIds list of object id to send to server
     */
    public void sendSyncSuccessToServer(List<String> objectIds) {
        /*send the list of id to network for successful sync*/
        mNetworkModel.sendSyncSuccess(objectIds);
    }
}
