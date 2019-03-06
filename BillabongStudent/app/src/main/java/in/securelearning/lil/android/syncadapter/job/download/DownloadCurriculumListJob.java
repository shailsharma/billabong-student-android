package in.securelearning.lil.android.syncadapter.job.download;

import android.text.TextUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import retrofit2.Call;

/**
 * Created by Prabodh Dhabaria on 21-12-2016.
 */

public class DownloadCurriculumListJob extends BaseDownloadArrayJob<ArrayList<Curriculum>> {
    public DownloadCurriculumListJob() {
        super("");

        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    @Override
    public void createValidationJobs(ArrayList<Curriculum> curriculums) {

    }

    @Override
    public Call<ArrayList<Curriculum>> fetchFromNetwork(String objectId) {
        return mNetworkModel.fetchCurriculums();
    }

    @Override
    public ArrayList<Curriculum> save(ArrayList<Curriculum> curriculums) {
        HashMap<String, Subject> subjects = new HashMap<>();
        HashMap<String, Grade> grades = new HashMap<>();
        for (Curriculum curriculum :
                curriculums) {
            curriculum.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
            curriculum = mJobModel.saveCurriculum(curriculum);
            subjects.put(curriculum.getSubject().getId(), curriculum.getSubject());
            if (curriculum.getGrade() != null && !TextUtils.isEmpty(curriculum.getGrade().getId())) {
                grades.put(curriculum.getGrade().getId(), curriculum.getGrade());
            }
        }

        mJobModel.saveSubjectList(subjects.values(), mContext);

        java.util.ArrayList<String> keys = new java.util.ArrayList<>(grades.keySet());
        java.util.ArrayList values = new java.util.ArrayList<>();
        Collections.sort(keys);
        for (String s :
                keys) {
            values.add(grades.get(s));
        }
        mJobModel.saveGradeList(values, mContext);

        return curriculums;
    }

    @Override
    public List<String> getObjectIdList(ArrayList<Curriculum> curriculums) {
        List<String> strings = new java.util.ArrayList<>();

        for (Curriculum curriculum :
                curriculums) {
            strings.add(curriculum.getObjectId());
        }

        return strings;
    }
}
