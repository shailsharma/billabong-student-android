package in.securelearning.lil.android.assignments.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import in.securelearning.lil.android.assignments.views.activity.QuizMetaDataActivity;
import in.securelearning.lil.android.assignments.views.fragment.InjectorAssignment;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.model.CurriculumModel;
import in.securelearning.lil.android.base.rxbus.RxBus;

/**
 * Created by Pushkar Raj on 5/20/2016.
 */

public class MetaDataScreenModel {

    @Inject
    RxBus mRxBus;

    @Inject
    CurriculumModel mCurriculumModel;

    public final String BOARDS = "boards";
    public final String GRADES = "grades";
    public final String SUBJECTS = "subjects";
    public final String LANGS = "langs";

    public MetaDataScreenModel() {
        InjectorAssignment.INSTANCE.getComponent().inject(this);
    }

    //    public void getBoardsList() {
//        mMetaDataModel.fetchBoardsListFromDb().subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<Board>>() {
//            @Override
//            public void accept(ArrayList<Board> boardArrayList) {
//                mRxBus.send(new LoadBoardsListEvent(boardArrayList));
//            }
//        });
//    }
//
//    public void getLanguagesList() {
//        mMetaDataModel.fetchLangagesListFromDb().subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<Language>>() {
//            @Override
//            public void accept(ArrayList<Language> languageArrayList) {
//                Log.e("META Data screen","Language List Size:"+languageArrayList.size());
//                mRxBus.send(new LoadLanguageListEvent(languageArrayList));
//            }
//        });
//    }
//
//    public void getTopicsForSubject(LilCurriculum lilCurriculum) {
//        mMetaDataModel.fetchTopicsForSubject(lilCurriculum).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<Topic>>() {
//            @Override
//            public void accept(ArrayList<Topic> topicArrayList) {
//                mRxBus.send(new LoadTopicListEvent(topicArrayList));
//            }
//        });
//    }
//
//    public void getGradesList() {
//        mMetaDataModel.fetchGradesListFromDb().subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<Grade>>() {
//            @Override
//            public void accept(ArrayList<Grade> gradeArrayList) {
//                mRxBus.send(new LoadGradeListEvent(gradeArrayList));
//            }
//        });
//    }
//
//    public void getSubjectsList() {
//        mMetaDataModel.fetchSubjectsListFromDb().subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<Subject>>() {
//            @Override
//            public void accept(ArrayList<Subject> subjectArrayList) {
//                mRxBus.send(new LoadSubjectListEvent(subjectArrayList));
//            }
//        });
//    }
    public HashMap<String, QuizMetaDataActivity.GradeExt> getData() {
        HashMap<String, QuizMetaDataActivity.GradeExt> gradeExtHashMap = new HashMap<>();
        ArrayList<Curriculum> list = mCurriculumModel.getCompleteList();
        for (Curriculum curriculum : list) {
            if (!gradeExtHashMap.containsKey(curriculum.getGrade().getId())) {
                gradeExtHashMap.put(curriculum.getGrade().getId(), new QuizMetaDataActivity.GradeExt(curriculum.getGrade()));
            }
            if (!gradeExtHashMap.get(curriculum.getGrade().getId()).getSubjectExtHashMap().containsKey(curriculum.getSubject().getId())) {
                gradeExtHashMap.get(curriculum.getGrade().getId()).getSubjectExtHashMap().put(curriculum.getSubject().getId(), new QuizMetaDataActivity.SubjectExt(curriculum.getSubject()));
            }

            QuizMetaDataActivity.TopicExt topic = new QuizMetaDataActivity.TopicExt(curriculum.getTopic());
            topic.setBoard(curriculum.getBoard());
            topic.setSubjectGroup(curriculum.getSubjectGroup());
            topic.setGrade(curriculum.getGrade());
            topic.setLang(curriculum.getLang());
            topic.setSkills(curriculum.getSkills());
            topic.setSubject(curriculum.getSubject());
            topic.setLearningLevel(curriculum.getLearningLevel());
            gradeExtHashMap.get(curriculum.getGrade().getId()).getSubjectExtHashMap().get(curriculum.getSubject().getId()).getTopicExts().add(topic);
        }

        return gradeExtHashMap;

    }
//    public QuizMetaDataActivity.QuizMetaData getData() {
//        QuizMetaDataActivity.QuizMetaData metaData = new QuizMetaDataActivity.QuizMetaData();
//        HashMap<String, Board> boards = new HashMap<>();
//        HashMap<String, Grade> grades = new HashMap<>();
//        HashMap<String, QuizMetaDataActivity.LearningLevelExt> learningLevels = new HashMap<>();
//        HashMap<String, Language> langs = new HashMap<>();
////        HashMap<String, Subject> subjects = new HashMap<>();
//        ArrayList<Curriculum> list = mCurriculumModel.getCompleteList();
//        for (Curriculum curriculum :
//                list) {
//            boards.put(curriculum.getBoard().getId(), curriculum.getBoard());
//            grades.put(curriculum.getGrade().getId(), curriculum.getGrade());
//            langs.put(curriculum.getLang().getId(), curriculum.getLang());
//            final QuizMetaDataActivity.LearningLevelExt ext = new QuizMetaDataActivity.LearningLevelExt(curriculum.getLearningLevel());
//            if (!learningLevels.containsKey(ext.getId())) {
//                learningLevels.put(ext.getId(), ext);
//            }
//            if (!learningLevels.get(ext.getId()).getSubjects().contains(curriculum.getSubject())) {
//                learningLevels.get(ext.getId()).getSubjects().add(new QuizMetaDataActivity.SubjectExt(curriculum.getSubject()));
//            }
//
//            QuizMetaDataActivity.TopicExt topic = new QuizMetaDataActivity.TopicExt(curriculum.getTopic());
//            topic.setBoard(curriculum.getBoard());
//            topic.setGrade(curriculum.getGrade());
//            topic.setLang(curriculum.getLang());
//            topic.setSkills(curriculum.getSkills());
//            topic.setSubject(curriculum.getSubject());
//            topic.setLearningLevel(curriculum.getLearningLevel());
////            subjects.get(curriculum.getSubject().getId()).getTopics().add(topic);
//            learningLevels.get(curriculum.getLearningLevel().getId()).getTopics().add(topic);
//            int index = learningLevels.get(ext.getId()).getSubjects().indexOf(curriculum.getSubject());
//            learningLevels.get(ext.getId()).getSubjects().get(index).getTopicExts().add(topic);
//
//        }
//        metaData.setBoardArrayList(new ArrayList<Board>(boards.values()));
//        metaData.setGradeArrayList(new ArrayList<Grade>(grades.values()));
//        metaData.setLanguageArrayList(new ArrayList<Language>(langs.values()));
////        metaData.setSubjectArrayList(new ArrayList<Subject>(subjects.values()));
//        metaData.setLearningLevelArrayList(new ArrayList<QuizMetaDataActivity.LearningLevelExt>(learningLevels.values()));
//        return metaData;
//
//    }


}
