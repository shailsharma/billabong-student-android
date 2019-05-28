package in.securelearning.lil.android.home.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.EventType;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityData;
import in.securelearning.lil.android.base.dataobjects.AnalysisActivityRecentlyRead;
import in.securelearning.lil.android.base.dataobjects.AnalysisTopicCovered;
import in.securelearning.lil.android.base.dataobjects.Board;
import in.securelearning.lil.android.base.dataobjects.CalendarDay;
import in.securelearning.lil.android.base.dataobjects.CalendarEvent;
import in.securelearning.lil.android.base.dataobjects.CuratorMapping;
import in.securelearning.lil.android.base.dataobjects.Curriculum;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.Language;
import in.securelearning.lil.android.base.dataobjects.LearningLevel;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.dataobjects.PerformanceResponseCount;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.Subject;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AnalysisActivityModel;
import in.securelearning.lil.android.base.model.AnalysisActivityRecentlyReadModel;
import in.securelearning.lil.android.base.model.AnalysisLearningModel;
import in.securelearning.lil.android.base.model.AnalysisTopicCoveredModel;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.CalEventModel;
import in.securelearning.lil.android.base.model.CuratorMappingModel;
import in.securelearning.lil.android.base.model.CurriculumModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.LearningMapModel;
import in.securelearning.lil.android.base.model.MicroLearningCourseModel;
import in.securelearning.lil.android.base.model.PerformanceResponseCountModel;
import in.securelearning.lil.android.base.model.PeriodicEventsModel;
import in.securelearning.lil.android.base.model.ResourceModel;
import in.securelearning.lil.android.base.model.TrainingModel;
import in.securelearning.lil.android.base.model.TrainingSessionModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.dataobjects.CalendarDayCounts;
import in.securelearning.lil.android.home.dataobjects.CalendarDayEventCounts;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AuthToken;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTP;
import in.securelearning.lil.android.syncadapter.dataobject.RequestOTPResponse;
import in.securelearning.lil.android.syncadapter.dataobject.TeacherGradeMapping;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.PrefManager;
import in.securelearning.lil.android.syncadapter.utils.PrefManagerStudentSubjectMapping;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Prabodh Dhabaria on 23-11-2016.
 */

public class HomeModel {
    @Inject
    GroupModel mGroupModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    ResourceModel mResourceModel;
    @Inject
    Context mContext;
    @Inject
    FtpFunctions mFtpFunctions;
    @Inject
    LearningMapModel learningMapModel;
    @Inject
    CalEventModel mCalEventModel;
    @Inject
    PeriodicEventsModel mPeriodicEventsModel;
    @Inject
    AssignmentModel mAssignmentModel;
    @Inject
    AssignmentResponseModel mAssignmentResponseModel;
    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;
    @Inject
    CuratorMappingModel mCuratorMappingModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    CurriculumModel mCurriculumModel;
    @Inject
    TrainingModel mTrainingModel;
    @Inject
    TrainingSessionModel mTrainingSessionModel;
    @Inject
    MicroLearningCourseModel mMicroLearningCourseModel;
    @Inject
    AnalysisActivityModel mAnalysisActivityModel;
    @Inject
    AnalysisLearningModel mAnalysisLearningModel;
    @Inject
    AnalysisActivityRecentlyReadModel mAnalysisActivityRecentlyReadModel;
    @Inject
    AnalysisTopicCoveredModel mAnalysisTopicCoveredModel;
    @Inject
    PerformanceResponseCountModel mAnalysisPerformanceCountModel;

    public HomeModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    public ArrayList<Resource> getResourceFileListFromInternalStorage() {
        return mResourceModel.getResourceListSync();
    }

    public ArrayList<Resource> getFtpFileList() {
        return mFtpFunctions.listFiles();
    }

    public ArrayList<LearningMap> getLearningMapList() {
        return learningMapModel.getCompleteList();
    }

    public LearningMap saveLearningMap(LearningMap obj) {
        return learningMapModel.saveObject(obj);
    }

    public ArrayList<CuratorMapping> getTeacherMapCompleteList() {
        return mCuratorMappingModel.getCompleteList();
    }

    public ArrayList<CuratorMapping> getTeacherMapCompleteListFromNetwork() {
        ArrayList<CuratorMapping> curatorMappings = new ArrayList<>();
        final Call<TeacherGradeMapping> teacherMapDataCall = mNetworkModel.getTeacherMapData();
        Response<TeacherGradeMapping> teacherMapData;
        try {
            teacherMapData = teacherMapDataCall.execute();
            if (teacherMapData != null && teacherMapData.isSuccessful()) {
                curatorMappings = teacherMapData.body().getCuratorMappings();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return curatorMappings;
    }

    public void saveTeacherMap(ArrayList<CuratorMapping> curatorDataList) {
        for (CuratorMapping object :
                curatorDataList) {
            mCuratorMappingModel.saveObject(object);
        }
    }


    public ArrayList<Topic> getTopicListForSubjectIdAndGradeId(String subjectId, ArrayList<String> subjects, String gradeId) {
        if (subjects == null || subjects.size() <= 0) {
            subjects = new ArrayList<>();
        }
        subjects.add(subjectId);
        HashMap<String, Topic> hashMap = new HashMap<>();
        for (String id : subjects) {
            ArrayList<Curriculum> curricula = mCurriculumModel.getCurriculumList(gradeId, id, "", 0, 0);
            for (Curriculum curriculum : curricula) {
                hashMap.put(curriculum.getTopic().getId(), curriculum.getTopic());
            }
        }

        return new ArrayList<Topic>(hashMap.values());
    }

    public void downloadActivityGraphData(Context context) {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.DAY_OF_WEEK, c.getISO8601DateStringFromDate());
//        c.set(Calendar.HOUR,0);
//        c.set(Calendar.MINUTE,0);
//        c.set(Calendar.SECOND,0);
//        c.set(Calendar.MILLISECOND,0);
        String end_Date = DateUtils.getCurrentISO8601DateString();
        Date currentdate = DateUtils.convertrIsoDate(end_Date);
        Calendar c = Calendar.getInstance();
        c.setTime(currentdate);
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        Date edate = c.getTime();
        String endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(edate);
        //String endDate=DateUtils.getISO8601DateStringFromDate(edate);
        // Date endDate= DateUtils.getSecondsForMidnightFromDate(currentdate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentdate);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -20);
        Date startdate = calendar.getTime();
        String startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(startdate);
        //String startDate=DateUtils.getISO8601DateStringFromDate(startdate);
        ArrayList<PrefManager.SubjectExt> subjectList = new ArrayList<>();
        subjectList = PrefManagerStudentSubjectMapping.getSubjectExtList(context);
        for (int i = 0; i <= subjectList.size(); i++) {
            JobCreator.createDownloadActivityJob(subjectList.get(i).getSubjects().get(i).getId(), startDate, endDate).execute();
        }
    }


    public SubjectMap getLearningMapFromCurriculum(String subjectId, ArrayList<String> subjects, String subjectName, String idTopic, String gradeId) {
        if (subjects == null || subjects.size() <= 0) {
            subjects = new ArrayList<>();
        }
        subjects.add(subjectId);
        ArrayList<Curriculum> curriculumTopics = new ArrayList<>();
        for (String id :
                subjects) {
            ArrayList<Curriculum> curricula = mCurriculumModel.getCurriculumList(gradeId, id, idTopic, 0, 0);
            curriculumTopics.addAll(curricula);
        }
        if (curriculumTopics != null && curriculumTopics.size() > 0) {
            SubjectMap subjectMap = new SubjectMap(0, 0, subjectName, subjectId);
            for (Curriculum curriculum :
                    curriculumTopics) {
                if (curriculum.getLang().getName().toLowerCase().contains("english")) {
                    // get all learning maps
                    final String topicId = curriculum.getTopic().getId();
                    final String topicName = curriculum.getTopic().getName();
                    TopicMap topicMap = subjectMap.getmTopicMap().get(topicId);
                    if (topicMap == null) {
                        topicMap = new TopicMap(0, 0, topicName, topicId, subjectId);
                    }

                    int attemptedSkillCount = 0;
                    for (Skill skill :
                            curriculum.getSkills()) {

                        ArrayList<LearningMap> learningMapsall = learningMapModel.getCompleteList();
                        ArrayList<LearningMap> learningMaps = learningMapModel.getLearningMapList(gradeId, curriculum.getSubject().getId(), topicId, skill.getId(), 0, 0);
                        double score = 0;
                        int totalQuestionsAttempted = 0;
                        for (LearningMap learningMap1 :
                                learningMaps) {
                            score = learningMap1.getSkill().getMarksObtained();
                            totalQuestionsAttempted = new Double(learningMap1.getSkill().getTotalQuestions()).intValue();
                        }
                        SkillMap skillMap = new SkillMap(score * 100, totalQuestionsAttempted, skill.getSkillName(), skill.getId(), topicId, subjectId);
                        skillMap.setBoard(curriculum.getBoard());
                        skillMap.setGrade(curriculum.getGrade());
                        skillMap.setSubject(curriculum.getSubject());
                        skillMap.setTopic(curriculum.getTopic());
                        skillMap.setLanguage(curriculum.getLang());
                        skillMap.setLearningLevel(curriculum.getLearningLevel());

                        if (!topicMap.getmSkillMap().containsKey(skillMap.getId())) {
                            if (totalQuestionsAttempted > 0) {
                                {
                                    topicMap.setTotalQuestionsAttempted(topicMap.getTotalQuestionsAttempted() + totalQuestionsAttempted);
                                    final double oldScore = topicMap.getTotalObtained();
                                    final double newScore = ((oldScore * attemptedSkillCount) + skillMap.getTotalObtained()) / (attemptedSkillCount + 1);
                                    topicMap.setTotalObtained(newScore);
                                }
                                {
                                    subjectMap.setTotalQuestionsAttempted(subjectMap.getTotalQuestionsAttempted() + totalQuestionsAttempted);
                                    final double oldScore = subjectMap.getTotalObtained();
                                    final double newScore = ((oldScore * attemptedSkillCount) + skillMap.getTotalObtained()) / (attemptedSkillCount + 1);
                                    subjectMap.setTotalObtained(newScore);
                                }
                                attemptedSkillCount++;

                                if (skillMap.getTotalObtained() >= 60) {
                                    topicMap.getSkill()[0] = topicMap.getSkill()[0] + 1;
                                    subjectMap.getSkill()[0] = subjectMap.getSkill()[0] + 1;
                                } else if (skillMap.getTotalObtained() >= 36) {
                                    topicMap.getSkill()[1] = topicMap.getSkill()[1] + 1;
                                    subjectMap.getSkill()[1] = subjectMap.getSkill()[1] + 1;
                                } else {
                                    topicMap.getSkill()[2] = topicMap.getSkill()[2] + 1;
                                    subjectMap.getSkill()[2] = subjectMap.getSkill()[2] + 1;
                                }

                            } else {
                                topicMap.getSkill()[3] = topicMap.getSkill()[3] + 1;
                                subjectMap.getSkill()[3] = subjectMap.getSkill()[3] + 1;
                            }

                        }
                        topicMap.getmSkillMap().put(skill.getId(), skillMap);
                    }
                    subjectMap.getmTopicMap().put(topicId, topicMap);

                }
            }

            return subjectMap;
        } else {
            return null;
        }
    }

    public SubjectMap getLearningMapFromCurriculum(String subjectId, String subjectName, ArrayList<String> subjects) {

        final UserProfile userProfile = mAppUserModel.getApplicationUser();
        String gradeId = "";
        if (userProfile != null && userProfile.getGrade() != null && !TextUtils.isEmpty(userProfile.getGrade().getId())) {
            gradeId = userProfile.getGrade().getId();
        }

        return getLearningMapFromCurriculum(subjectId, subjects, subjectName, "", gradeId);
    }


    public SubjectMap getSubjectMapForTraining(ArrayList<Object> skillIdList, ArrayList<SkillMap> skillMapList) {
        SubjectMap subjectMap = new SubjectMap(0, 0, "", "");
        HashMap<String, TopicMap> topicMapList = new HashMap<>();
        if (skillMapList != null || skillMapList.size() >= 0) {

            for (int k = 0; k < skillMapList.size(); k++) {
                SkillMap skillMap = skillMapList.get(k);

                ArrayList<LearningMap> learningMap = learningMapModel.getLearningMapListBySkillIdList(new ArrayList<Object>(Collections.singleton(skillMap.getId())));

                if (learningMap != null && learningMap.size() > 0) {
                    skillMap.setId(learningMap.get(0).getSkill().getId());
                    skillMap.setTid(learningMap.get(0).getTopic().getId());
                    skillMap.setSid(learningMap.get(0).getSubject().getId());
                    skillMap.setName(learningMap.get(0).getSkill().getSkillName());
                    skillMap.setSkillLevel(learningMap.get(0).getSkill().getSkillLevel());
                    skillMap.setBoard(learningMap.get(0).getBoard());
                    skillMap.setGrade(learningMap.get(0).getGrade());
                    skillMap.setLanguage(learningMap.get(0).getLang());
                    skillMap.setLearningLevel(learningMap.get(0).getLearningLevel());
                    skillMap.setSubject(learningMap.get(0).getSubject());
                    skillMap.setTopic(learningMap.get(0).getTopic());
                    try {
                        int totalQuestionsAttempted = new Double(learningMap.get(0).getSkill().getTotalQuestions()).intValue();
                        skillMap.setTotalQuestionsAttempted(totalQuestionsAttempted);
                    } catch (Exception e) {
                        e.printStackTrace();
                        skillMap.setTotalQuestionsAttempted(0);
                    }
                    skillMap.setTotalObtained((learningMap.get(0).getSkill().getMarksObtained() * 100));
                }

                if (skillMap.getTotalQuestionsAttempted() > 0) {
                    if (skillMap.getTotalObtained() >= 60) {
                        subjectMap.getSkill()[0] = subjectMap.getSkill()[0] + 1;
                    } else if (skillMap.getTotalObtained() >= 36) {
                        subjectMap.getSkill()[1] = subjectMap.getSkill()[1] + 1;
                    } else {
                        subjectMap.getSkill()[2] = subjectMap.getSkill()[2] + 1;
                    }
                } else {
                    subjectMap.getSkill()[3] = subjectMap.getSkill()[3] + 1;

                }

                if (!subjectMap.getmTopicMap().containsKey(skillMap.getTopic().getId())) {

                    // TODO: 03-04-2017 add into avg

                    double score = subjectMap.getTotalObtained();
                    double count = subjectMap.getmTopicMap().size();

//                    score = (score * count) / (count + 1);
                    subjectMap.setTotalObtained(score);
                    subjectMap.getmTopicMap().put(skillMap.getTopic().getId(), new TopicMap(0, 0, skillMap.getTopic().getName(), skillMap.getTopic().getId(), skillMap.getSubject().getId()));

                }

                final TopicMap topicMap = subjectMap.getmTopicMap().get(skillMap.getTopic().getId());

                final double oldScore = topicMap.getTotalObtained();
                int count = topicMap.getmSkillMap().size();
                final double newScore = ((oldScore * count) + skillMap.getTotalObtained()) / (count + 1);
                topicMap.setTotalObtained(newScore);

                topicMap.getmSkillMap().put(skillMap.getId(), skillMap);
                topicMapList.put(topicMap.getTid(), topicMap);

                final double oldScoreSub = subjectMap.getTotalObtained();
                count = subjectMap.getmTopicMap().size();
                double newScoreSub = oldScoreSub;
                if (count > 0) {
                    if (topicMap.addedOnce) {
                        newScoreSub = (((oldScoreSub * count) - oldScore) + newScore) / count;
                        subjectMap.setTotalObtained(newScoreSub);
                    } else {
                        newScoreSub = (((oldScoreSub * (count - 1))) + newScore) / count;
                        subjectMap.setTotalObtained(newScoreSub);
                    }
                }

                if (skillMap.getTotalObtained() >= 60) {
                    topicMap.getSkill()[0] = topicMap.getSkill()[0] + 1;
                } else if (skillMap.getTotalObtained() >= 36) {
                    topicMap.getSkill()[1] = topicMap.getSkill()[1] + 1;
                } else {
                    topicMap.getSkill()[2] = topicMap.getSkill()[2] + 1;
                }

                if (topicMap.isAddedOnce()) {
                    if (oldScore >= 60 && subjectMap.getTopic()[0] > 0) {
                        subjectMap.getTopic()[0] = subjectMap.getTopic()[0] - 1;
                    } else if (oldScore >= 36 && subjectMap.getTopic()[1] > 0) {
                        subjectMap.getTopic()[1] = subjectMap.getTopic()[1] - 1;
                    } else if (subjectMap.getTopic()[2] > 0) {
                        subjectMap.getTopic()[2] = subjectMap.getTopic()[2] - 1;
                    }
                }


                if (newScore >= 60) {
                    subjectMap.getTopic()[0] = subjectMap.getTopic()[0] + 1;
                } else if (newScore >= 36) {
                    subjectMap.getTopic()[1] = subjectMap.getTopic()[1] + 1;
                } else {
                    subjectMap.getTopic()[2] = subjectMap.getTopic()[2] + 1;
                }

                topicMap.setAddedOnce(true);

                subjectMap.setAddedOnce(true);

            }

            return subjectMap;
        }
        return null;
    }

    public CalendarDayCounts getCalendarDayCounts(Calendar calendar, boolean isTeacher) {
        SimpleDateFormat format = new SimpleDateFormat("EEE', 'd MMM yyyy");
        SimpleDateFormat periodicEventDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        long startSeconds = DateUtils.getSecondsForMorningFromDate(calendar.getTime());
        long endSeconds = DateUtils.getSecondsForMidnightFromDate(calendar.getTime());
        String startDate = DateUtils.getISO8601DateStringFromSeconds(startSeconds);
        String endDate = DateUtils.getISO8601DateStringFromSeconds(endSeconds);

        Locale locale = Locale.getDefault();

        CalendarDayCounts calendarDayCounts = new CalendarDayCounts();
        CalendarDay calendarDay = new CalendarDay();
        calendarDay.setDayOfWeek(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
        calendarDay.setDayOfMonth(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendarDay.setMonthOfYear(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
        calendarDay.setDate(calendar.getTime());
        calendarDay.setDateFormatted(format.format(calendar.getTime()));
        calendarDay.setDatePeriodicEvent(periodicEventDateFormat.format(calendar.getTime()));
        calendarDayCounts.setCalendarDay(calendarDay);

        calendarDayCounts.setPersonalCounts(mCalEventModel.getEventCounts(EventType.TYPE_PERSONAL.getEventType(), startDate, endDate));
        calendarDayCounts.setActivitiesCounts(mCalEventModel.getEventCounts(EventType.TYPE_ACTIVITY.getEventType(), startDate, endDate));
        calendarDayCounts.setAnnouncementsCounts(mCalEventModel.getEventCounts(EventType.TYPE_ANNOUNCEMENT.getEventType(), startDate, endDate));
        calendarDayCounts.setExamEventCounts(mCalEventModel.getEventCounts(EventType.TYPE_EXAM.getEventType(), startDate, endDate));
        calendarDayCounts.setVacationEventCounts(mCalEventModel.getEventCounts(EventType.TYPE_VACATION.getEventType(), startDate, endDate));
        calendarDayCounts.setHolidayEventCounts(mCalEventModel.getEventCounts(EventType.TYPE_HOLIDAY.getEventType(), startDate, endDate));
        calendarDayCounts.setCelebrationEventCounts(mCalEventModel.getEventCounts(EventType.TYPE_CELEBRATION.getEventType(), startDate, endDate));
        calendarDayCounts.setTrainingSessionCounts(mTrainingSessionModel.getSessionsCount(startDate, endDate));
        if (isTeacher) {
            calendarDayCounts.setAssignmentCounts(mAssignmentModel.getIncompleteAssignmentsCount(mAppUserModel.getObjectId(), startDate, endDate, 0, 0));
        } else {
            calendarDayCounts.setAssignmentCounts(mAssignmentResponseModel.getDueAssignmentResponseCounts(startDate, endDate));
        }

        calendarDayCounts.setPeriodCounts(mPeriodicEventsModel.getPeriodCounts(startSeconds, endSeconds));
        return calendarDayCounts;
    }

    public CalendarDayEventCounts getCalendarDayEventCounts(String startDate, String endDate) {

        CalendarDayEventCounts calendarDayEventCounts = new CalendarDayEventCounts();
        calendarDayEventCounts.setPersonalCounts(mCalEventModel.getEventCounts(EventType.TYPE_PERSONAL.getEventType(), startDate, endDate));
        calendarDayEventCounts.setActivitiesCounts(mCalEventModel.getEventCounts(EventType.TYPE_ACTIVITY.getEventType(), startDate, endDate));
        calendarDayEventCounts.setAnnouncementsCounts(mCalEventModel.getEventCounts(EventType.TYPE_ANNOUNCEMENT.getEventType(), startDate, endDate));

        return calendarDayEventCounts;
    }

    public Observable<ArrayList<CalendarEvent>> getEventListOfSelectedDate(final String eventType, final String startDate, final String endDate) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<CalendarEvent>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<CalendarEvent>> subscriber) {

                        ArrayList<CalendarEvent> calendarEvents = mCalEventModel.getEventList(eventType, startDate, endDate);
                        subscriber.onNext(calendarEvents);
                        subscriber.onComplete();
                    }
                });
    }

    public Observable<ArrayList<CalendarEvent>> getEventsByStartAndEndDate(final String startDate, final String endDate) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<CalendarEvent>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<CalendarEvent>> subscriber) {

                        ArrayList<CalendarEvent> calendarEvents = mCalEventModel.getAllEventsByStartAndEndDate(startDate, endDate);
                        subscriber.onNext(calendarEvents);
                        subscriber.onComplete();
                    }
                });
    }


    public Observable<ArrayList<PeriodNew>> getPeriodOfSelectedDate(final long startSecond, final long endSecond) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<PeriodNew>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<PeriodNew>> subscriber) {

                        ArrayList<PeriodNew> periodListSync = mPeriodicEventsModel.getPeriodListSync(startSecond, endSecond);
                        subscriber.onNext(periodListSync);
                        subscriber.onComplete();
                    }
                });
    }

    public Group getGroupFromId(String groupId) {
        return mGroupModel.getGroupFromUidSync(groupId);
    }

//    public StudentScore getMapValue() {
//        StudentScore map = new StudentScore();
//        ArrayList<ClassMap> classMap = new ArrayList<>();
//        SubjectTeacherMap subjectTeacherMap = new SubjectTeacherMap();
//        ArrayList<Group> groups;
//        groups =  mGroupModel.getModeratedGroups(mAppUserModel.getLoggedInUserId());
//        for (int i = 0; i < groups.size(); i++) {
//            ClassMap cMap = new ClassMap();
//            cMap.setGrade(groups.get(i).getGrade());
//            cMap.setSection(groups.get(i).getSection());
//            classMap.set(i,cMap);
//            subjectTeacherMap.setClassMap(classMap);
//            subjectTeacherMap.setSubjectId(groups.get(i).getSubject().getId());
//            subjectTeacherMap.setSubjectId(groups.get(i).getSubject().getName());
//        }
//        return map;
//    }


    public static class StudentScore {
        @SerializedName("id")
        @Expose
        private String mStudentId;

        @SerializedName("fullName")
        @Expose
        private String mName;

        @SerializedName("average")
        @Expose
        private double mScore;


        @SerializedName("userThumbnail")
        @Expose
        private Thumbnail mThumbnail;

        @SerializedName("count")
        @Expose
        private int mCount;

        public String getStudentId() {
            return mStudentId;
        }

        public void setStudentId(String studentId) {
            mStudentId = studentId;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public double getScore() {
            return mScore;
        }

        public void setScore(double score) {
            mScore = score;
        }

        public int getCount() {
            return mCount;
        }

        public void setCount(int count) {
            mCount = count;
        }

        public Thumbnail getThumbnail() {
            return mThumbnail;
        }

        public void setThumbnail(Thumbnail thumbnail) {
            mThumbnail = thumbnail;
        }
    }

//    public static class SubjectTeacherMap implements java.io.Serializable{
//        private String subjectId;
//        private String subjectName;
//        private ArrayList<ClassMap> classMap = new ArrayList<>();
//
//        public String getSubjectId() {
//            return subjectId;
//        }
//
//        public void setSubjectId(String subjectId) {
//            this.subjectId = subjectId;
//        }
//
//        public String getSubjectName() {
//            return subjectName;
//        }
//
//        public void setSubjectName(String subjectName) {
//            this.subjectName = subjectName;
//        }
//
//        public ArrayList<ClassMap> getClassMap() {
//            return classMap;
//        }
//
//        public void setClassMap(ArrayList<ClassMap> classMap) {
//            this.classMap = classMap;
//        }
//    }
//
//    public static class ClassMap implements java.io.Serializable{
//        private Grade mGrade;
//        private GradeSectionSuper mSection;
//
//        public Grade getGrade() {
//            return mGrade;
//        }
//
//        public void setGrade(Grade grade) {
//            mGrade = mGrade;
//        }
//
//        public GradeSectionSuper getSection() {
//            return mSection;
//        }
//
//        public void setSection(GradeSectionSuper section) {
//            mSection = section;
//        }
//    }


    public static class SubjectMap implements java.io.Serializable {
        private HashMap<String, TopicMap> mTopicMap = new HashMap<>();
        private double totalObtained;
        private int totalQuestionsAttempted;
        private String name;
        private String sid;
        private int[] topicCount = {0, 0, 0, 0};
        private int[] skillCount = {0, 0, 0, 0};
        private boolean addedOnce = false;

        public boolean isAddedOnce() {
            return addedOnce;
        }

        public void setAddedOnce(boolean addedOnce) {
            this.addedOnce = addedOnce;
        }

        public int[] getTopic() {
            return topicCount;
        }

        public void setTopic(int[] topicCount) {
            this.topicCount = topicCount;
        }

        public int getTotalQuestionsAttempted() {
            return totalQuestionsAttempted;
        }

        public void setTotalQuestionsAttempted(int totalQuestionsAttempted) {
            this.totalQuestionsAttempted = totalQuestionsAttempted;
        }

        public SubjectMap(double totalObtained, int totalQuestionsAttempted, String name, String sid) {
            this.totalObtained = totalObtained;
            this.totalQuestionsAttempted = totalQuestionsAttempted;
            this.name = name;
            this.sid = sid;
        }

        @Override
        public boolean equals(Object o) {
            return this == o ? true : (!(o instanceof SubjectMap) ? false : this.getSid().equals(((SubjectMap) o).getSid()));
        }

        public int[] getSkill() {
            return skillCount;
        }

        public void setSkill(int[] skillCount) {
            this.skillCount = skillCount;
        }

        public HashMap<String, TopicMap> getmTopicMap() {
            return mTopicMap;
        }

        public void setmTopicMap(HashMap<String, TopicMap> mTopicMap) {
            this.mTopicMap = mTopicMap;
        }

        public double getTotalObtained() {
            return totalObtained;
        }

        public void setTotalObtained(double totalObtained) {
            this.totalObtained = totalObtained;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }
    }

    public static class TopicMap implements java.io.Serializable {
        private HashMap<String, SkillMap> mSkillMap = new HashMap<>();
        private double totalObtained;
        private int totalQuestionsAttempted;
        private String name;
        private String tid;
        private String sid;
        private int[] skillCount = {0, 0, 0, 0};
        private boolean addedOnce = false;

        public boolean isAddedOnce() {
            return addedOnce;
        }

        public void setAddedOnce(boolean addedOnce) {
            this.addedOnce = addedOnce;
        }

        public int[] getSkill() {
            return skillCount;
        }

        public void setSkill(int[] skillCount) {
            this.skillCount = skillCount;
        }

        public int getTotalQuestionsAttempted() {
            return totalQuestionsAttempted;
        }

        public void setTotalQuestionsAttempted(int totalQuestionsAttempted) {
            this.totalQuestionsAttempted = totalQuestionsAttempted;
        }

        @Override
        public boolean equals(Object o) {
            return this == o ? true : (!(o instanceof TopicMap) ? false : this.getTid().equals(((TopicMap) o).getTid()));
        }

        public TopicMap(double totalObtained, int totalQuestionsAttempted, String name, String tid, String sid) {
            this.totalObtained = totalObtained;
            this.totalQuestionsAttempted = totalQuestionsAttempted;
            this.name = name;
            this.tid = tid;
            this.sid = sid;
        }

        public HashMap<String, SkillMap> getmSkillMap() {
            return mSkillMap;
        }

        public void setmSkillMap(HashMap<String, SkillMap> mSkillMap) {
            this.mSkillMap = mSkillMap;
        }

        public double getTotalObtained() {
            return totalObtained;
        }

        public void setTotalObtained(double totalObtained) {
            this.totalObtained = totalObtained;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }
    }


    public static class SkillMap implements java.io.Serializable {
        private double mTotalObtained = 0d;
        private int mTotalQuestionsAttempted = -1;
        private String mName;
        private String mId;
        private String mTid;
        private String mSid;
        private String mSkillLevel;
        private LearningLevel mLearningLevel = new LearningLevel();
        private Subject mSubject = new Subject();
        private Language mLanguage = new Language();
        private Board mBoard = new Board();
        private Grade mGrade = new Grade();
        private Topic mTopic = new Topic();

        public SkillMap() {
        }

        public SkillMap(double totalObtained, int totalQuestionsAttempted, String name, String id, String tid, String sid) {
            this.mTotalObtained = totalObtained;
            this.mTotalQuestionsAttempted = totalQuestionsAttempted;
            this.mName = name;
            this.mId = id;
            this.mTid = tid;
            this.mSid = sid;
        }

        public int getTotalQuestionsAttempted() {
            return mTotalQuestionsAttempted;
        }

        public void setTotalQuestionsAttempted(int totalQuestionsAttempted) {
            this.mTotalQuestionsAttempted = totalQuestionsAttempted;
        }

        @Override
        public boolean equals(Object o) {
            return this == o ? true : (!(o instanceof SkillMap) ? false : this.getId().equals(((SkillMap) o).getId()));
        }

        public String getSkillLevel() {
            return mSkillLevel;
        }

        public void setSkillLevel(String skillLevel) {
            this.mSkillLevel = skillLevel;
        }

        public double getTotalObtained() {
            return mTotalObtained;
        }

        public void setTotalObtained(double totalObtained) {
            this.mTotalObtained = totalObtained;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            this.mName = name;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            this.mId = id;
        }

        public String getTid() {
            return mTid;
        }

        public void setTid(String tid) {
            this.mTid = tid;
        }

        public String getSid() {
            return mSid;
        }

        public void setSid(String sid) {
            this.mSid = sid;
        }

        public LearningLevel getLearningLevel() {
            return mLearningLevel;
        }

        public void setLearningLevel(LearningLevel learningLevel) {
            this.mLearningLevel = learningLevel;
        }

        public Subject getSubject() {
            return mSubject;
        }

        public void setSubject(Subject subject) {
            this.mSubject = subject;
        }

        public Language getLanguage() {
            return mLanguage;
        }

        public void setLanguage(Language language) {
            this.mLanguage = language;
        }

        public Board getBoard() {
            return mBoard;
        }

        public void setBoard(Board board) {
            this.mBoard = board;
        }

        public Grade getGrade() {
            return mGrade;
        }

        public void setGrade(Grade grade) {
            this.mGrade = grade;
        }

        public Topic getTopic() {
            return mTopic;
        }

        public void setTopic(Topic topic) {
            this.mTopic = topic;
        }
    }

    public String getGroupId(String gradeId, String sectionId, String subjectId) {
        Group group = mGroupModel.getGroupByGradeSectionSubject(gradeId, subjectId, sectionId);
        if (group != null && !TextUtils.isEmpty(group.getObjectId()))
            return group.getObjectId();
        else return "";
    }

    public ArrayList<Training> getTrainingList() {
        return mTrainingModel.getActiveCompleteList();
    }


    public Training getTrainingById(String objectId) {
        return mTrainingModel.getObjectById(objectId);
    }

    public PerformanceResponseCount getPerformanceCount(String subId) {
        return mAnalysisPerformanceCountModel.getDataBySubjectId(subId);
        // return mAnalysisActivityModel.getCompleteList();
    }

    public ArrayList<AnalysisActivityData> getActivityDataList(String subId, String startDate, String endDate) {
        return mAnalysisActivityModel.getDataBySubjectId(subId, startDate, endDate);
        // return mAnalysisActivityModel.getCompleteList();
    }

    public ArrayList<AnalysisActivityData> getActivityAllDataList(String subId, String startDate, String endDate) {
        return mAnalysisActivityModel.getAllDataBySubjectId(subId);
        //return mAnalysisActivityModel.getCompleteList();
    }

    public ArrayList<AnalysisActivityData> getLearningDataList(String subId, String startDate, String endDate) {
        return mAnalysisLearningModel.getDataBySubjectId(subId, startDate, endDate);
        // return mAnalysisActivityModel.getCompleteList();
    }

    public ArrayList<AnalysisActivityData> getAllLearningDataList(String subId, String startDate, String endDate) {
        //return mAnalysisLearningModel.getAllDataBySubjectId(subId);
        return mAnalysisLearningModel.getCompleteList();
    }

    public ArrayList<AnalysisActivityRecentlyRead> getRecentReadData(String subId, final int skip, final int limit) {
        // return mAnalysisActivityRecentlyReadModel.getDataBySubjectId(subId, skip, limit);
        return mAnalysisActivityRecentlyReadModel.getCompleteListByLimit(skip, limit);
    }

    public ArrayList<AnalysisTopicCovered> getTopicCovered(String subId, final int skip, final int limit) {
        // return mAnalysisTopicCoveredModel.getDataBySubjectId(subId, skip, limit);
        return mAnalysisTopicCoveredModel.getCompleteListByLimit(skip, limit);
    }

    public AnalysisActivityData getActivityDataById(String objectId) {
        return mAnalysisActivityModel.getObjectById(objectId);
    }

    public TrainingSession getTrainingSessionById(String objectId) {
        return mTrainingSessionModel.getObjectById(objectId);
    }

    public ArrayList<TrainingSession> getTrainingSessionsByDateRange(String startDate, String endDate) {
        return mTrainingSessionModel.getSessionByDateRange(startDate, endDate);
    }

    public ArrayList<TrainingSession> getTrainingSessionsByTrainingId(String trainingId) {
        return mTrainingSessionModel.getSessionByTrainingId(trainingId);
    }

    public Observable<ArrayList<Topic>> getTopicListFromCurriculum(final String gradeId, final ArrayList<String> subjectIds, final String topicId) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Topic>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Topic>> subscriber) {
                        ArrayList<Topic> curriculumTopics = new ArrayList<>();
                        for (int i = 0; i < subjectIds.size(); i++) {
                            ArrayList<Curriculum> list = mCurriculumModel.getCurriculumList(gradeId, subjectIds.get(i), topicId, 0, 0);
                            for (int j = 0; j < list.size(); j++) {
                                curriculumTopics.add(list.get(j).getTopic());
                            }
                        }
                        subscriber.onNext(curriculumTopics);
                        subscriber.onComplete();

                    }
                });
    }

    public Observable<ArrayList<Curriculum>> getCurriculumList(final String gradeId, final ArrayList<String> subjectIds, final String topicId) {

        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Curriculum>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Curriculum>> subscriber) {
                        ArrayList<Curriculum> list = new ArrayList<>();
                        for (int i = 0; i < subjectIds.size(); i++) {
                            list.addAll(mCurriculumModel.getCurriculumList(gradeId, subjectIds.get(i), topicId, 0, 0));

                        }
                        subscriber.onNext(list);
                        subscriber.onComplete();

                    }
                });
    }

    /*To request OTP sms on mobile*/
    public Observable<RequestOTPResponse> requestOTP(final String mobileNumber) {
        return Observable.create(new ObservableOnSubscribe<RequestOTPResponse>() {
            @Override
            public void subscribe(ObservableEmitter<RequestOTPResponse> e) throws Exception {
                RequestOTP requestOTP = new RequestOTP();
                requestOTP.setCode(null);
                requestOTP.setMobile(mobileNumber);
                Call<RequestOTPResponse> call = mNetworkModel.requestOTP(requestOTP);
                Response<RequestOTPResponse> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    RequestOTPResponse body = response.body();
                    Log.e("RequestOTPResponse--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 400) {
                    throw new Exception(mContext.getString(R.string.enrollment_number_not_exist_in_database));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<RequestOTPResponse> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        RequestOTPResponse course = response2.body();
                        Log.e("RequestOTPResponse--", "Successful");
                        e.onNext(course);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response.code() == 400) {
                        throw new Exception(mContext.getString(R.string.enrollment_number_not_exist_in_database));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("RequestOTPResponse--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("RequestOTPResponse--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));

//
//                    ResponseBody responseBody = response.errorBody();
//                    try {
//                        assert responseBody != null;
//                        String json = responseBody.string().replaceAll("\\\\", "");
//                        JSONObject jsonObject = new JSONObject(json);
//                        JSONObject error = jsonObject.getJSONObject("error");
//                        String message = error.getString("message");
//                        String code = error.getString("code");
//                        if (!TextUtils.isEmpty(code) && code.contains("INVALID_MOBILE")) {
//                        }
//
//                    } catch (JSONException e1) {
//                        e1.printStackTrace();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
                }

                e.onComplete();
            }
        });
    }

    /*To verify OTP code*/
    public Observable<AuthToken> verifyOTP(final String mobileNumber, final String verificationCode) {
        return Observable.create(new ObservableOnSubscribe<AuthToken>() {
            @Override
            public void subscribe(ObservableEmitter<AuthToken> e) throws Exception {
                RequestOTP requestOTP = new RequestOTP();
                requestOTP.setCode(verificationCode);
                requestOTP.setMobile(mobileNumber);
                Call<AuthToken> call = mNetworkModel.verifyOTP(requestOTP);
                Response<AuthToken> response = call.execute();
                if (response != null && response.isSuccessful()) {
                    AuthToken body = response.body();
                    Log.e("VerifyOTPResponse--", "Successful");
                    e.onNext(body);
                } else if (response.code() == 404) {
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                } else if (response.code() == 400) {
                    throw new Exception(mContext.getString(R.string.invalid_code));
                } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(mContext)) {
                    Response<AuthToken> response2 = call.clone().execute();
                    if (response2 != null && response2.isSuccessful()) {
                        AuthToken course = response2.body();
                        Log.e("VerifyOTPResponse--", "Successful");
                        e.onNext(course);
                    } else if ((response2.code() == 401)) {
                        mContext.startActivity(LoginActivity.getUnauthorizedIntent(mContext));
                    } else if (response.code() == 400) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else if (response2.code() == 404) {
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    } else {
                        Log.e("VerifyOTPResponse--", "Failed");
                        throw new Exception(mContext.getString(R.string.messageUnableToGetData));
                    }
                } else {
                    Log.e("VerifyOTPResponse--", "Failed");
                    throw new Exception(mContext.getString(R.string.messageUnableToGetData));

                }

                e.onComplete();
            }
        });
    }

    public Observable<String> checkForNewVersionOnPlayStore() {

        return
                io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        String newVersion = Jsoup.connect(
                                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "&hl=en")
                                .timeout(30000)
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                .referrer("http://www.google.com")
                                .get()
                                .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                                .first()
                                .ownText();
                        e.onNext(newVersion);
                        e.onComplete();
                        Log.e("new Version", newVersion);


                    }
                });
    }


}
