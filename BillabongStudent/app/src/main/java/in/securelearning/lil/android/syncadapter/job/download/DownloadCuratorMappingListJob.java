package in.securelearning.lil.android.syncadapter.job.download;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.dataobject.TeacherGradeMapping;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Job to download an TeacherGradeMapping from server
 */
public class DownloadCuratorMappingListJob extends BaseDownloadJob<TeacherGradeMapping> {
    private final String TAG = this.getClass().getCanonicalName();

    public DownloadCuratorMappingListJob(String objectId) {
        super(objectId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public DownloadCuratorMappingListJob(String objectId, String notificationId) {
        super(objectId, notificationId);

        /*perform injection*/
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public void execute() {
        try {
            Log.d(TAG, "Fetching Object");
            /*fetch the object from the server*/
            Response<TeacherGradeMapping> response = fetchFromNetwork(id).execute();

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

    @Override
    public TeacherGradeMapping get(String objectId) {
        return null;
    }

    /**
     * create validation job for the teacherGradeMapping
     *
     * @param teacherGradeMapping to validate
     */
    @Override
    public void createValidationJobs(TeacherGradeMapping teacherGradeMapping) {

    }

    /**
     * network call to fetch postData
     *
     * @param objectId id of the postData to fetch
     * @return call : the network call to fetch postData
     */
    @Override
    public Call<TeacherGradeMapping> fetchFromNetwork(String objectId) {
        /*fetch teacherGradeMapping from network*/
        return mNetworkModel.getTeacherMapData();
    }

    /**
     * persist teacherGradeMapping
     *
     * @param teacherGradeMapping to persist
     * @return the persisted postData
     */
    @Override
    public TeacherGradeMapping save(TeacherGradeMapping teacherGradeMapping) {
        /*save teacherGradeMapping to database*/
        for (CuratorMapping curatorMapping : teacherGradeMapping.getCuratorMappings()) {
            String subjectId = "", gradeId = "", sectionId = "";
            if (curatorMapping.getGrade() != null && !TextUtils.isEmpty(curatorMapping.getGrade().getId())) {
                gradeId = curatorMapping.getGrade().getId();
            }
            if (curatorMapping.getSection() != null && !TextUtils.isEmpty(curatorMapping.getSection().getId())) {
                sectionId = curatorMapping.getSection().getId();
            }
            if (curatorMapping.getSubject() != null && !TextUtils.isEmpty(curatorMapping.getSubject().getId())) {
                subjectId = curatorMapping.getSubject().getId();
            }
            curatorMapping.setObjectId(gradeId + sectionId + subjectId);
            mJobModel.saveCuratorMapping(curatorMapping);
        }
        return teacherGradeMapping;
    }

    /**
     * get the list of object id in the teacherGradeMapping
     *
     * @param teacherGradeMapping containing the id
     * @return list of id
     */
    @Override
    public List<String> getObjectIdList(TeacherGradeMapping teacherGradeMapping) {

        /*get id from the teacherGradeMapping*/
        return Collections.singletonList(teacherGradeMapping.getObjectId());
    }


}
