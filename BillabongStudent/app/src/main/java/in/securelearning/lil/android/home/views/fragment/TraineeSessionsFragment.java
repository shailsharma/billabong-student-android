package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerviewSimpleItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutStudyReferenceItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutTrainingSessionItemBinding;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Topic;
import in.securelearning.lil.android.base.dataobjects.Training;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Chaitendra on 28-Dec-17.
 */

public class TraineeSessionsFragment extends Fragment {
    @Inject
    HomeModel mHomeModel;
    @Inject
    NetworkModel mNetworkModel;
    public static final String TRAINING_ID = "trainingId";
    public static final String COLUMN_COUNT = "columnCount";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String TYPE = "type";
    private int mColumnCount, mViewType;
    private String mTrainingId, mStartDate, mEndDate;
    LayoutRecyclerViewBinding mBinding;

    public static TraineeSessionsFragment newInstance(int columnCount, String trainingId, int type) {
        TraineeSessionsFragment traineeSessionsFragment = new TraineeSessionsFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        args.putString(TRAINING_ID, trainingId);
        args.putInt(TYPE, type);
        traineeSessionsFragment.setArguments(args);
        return traineeSessionsFragment;
    }

    public static TraineeSessionsFragment newInstance(int columnCount, String startDate, String endDate, int type) {
        TraineeSessionsFragment traineeSessionsFragment = new TraineeSessionsFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        args.putString(START_DATE, startDate);
        args.putString(END_DATE, endDate);
        args.putInt(TYPE, type);
        traineeSessionsFragment.setArguments(args);
        return traineeSessionsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_recycler_view, container, false);
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
            mTrainingId = getArguments().getString(TRAINING_ID);
            mStartDate = getArguments().getString(START_DATE);
            mEndDate = getArguments().getString(END_DATE);
            mViewType = getArguments().getInt(TYPE);
        }
        handleViewType(mViewType);
        return mBinding.getRoot();
    }

    private void handleViewType(int viewType) {
        if (viewType == 0) {
            if (!android.text.TextUtils.isEmpty(mTrainingId)) {
                getSessionsOffline(mTrainingId);
            }
        } else if (viewType == 1) {
            if (!android.text.TextUtils.isEmpty(mTrainingId)) {
                getSessionsOnline(mTrainingId);
            }
        } else if (viewType == 2) {
            if (!TextUtils.isEmpty(mStartDate) && !TextUtils.isEmpty(mEndDate)) {
                getSessionsByDateRange(mStartDate, mEndDate);

            }
        }
    }

    private void getSessionsOnline(final String trainingId) {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.layoutRetry.setVisibility(View.GONE);

            Observable.create(new ObservableOnSubscribe<Training>() {
                @Override
                public void subscribe(ObservableEmitter<Training> e) throws Exception {
                    Call<Training> call = mNetworkModel.fetchTraining(trainingId);
                    Response<Training> response = call.execute();
                    if (response != null && response.isSuccessful()) {
                        Training training = response.body();
                        Log.e("TrainingDetail1", "successful");
                        e.onNext(training);
                    } else if (response.code() == 404) {
                        throw new Exception(getString(R.string.messageNoTrainingsFound));
                    } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
                        Response<Training> response2 = call.clone().execute();
                        if (response2 != null && response2.isSuccessful()) {
                            Training training = response.body();
                            Log.e("TrainingDetail2--", "Successful");
                            e.onNext(training);
                        } else if ((response2.code() == 401)) {
                            startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
                        } else if (response2.code() == 404) {
                            throw new Exception(getString(R.string.messageNoTrainingsFound));
                        } else {
                            Log.e("TrainingDetail2--", "Failed");
                            throw new Exception(getString(R.string.messageTrainingsFetchFailed));
                        }
                    } else {
                        Log.e("TrainingDetail1--", "Failed");
                        throw new Exception(getString(R.string.messageTrainingsFetchFailed));
                    }

                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Training>() {
                        @Override
                        public void accept(Training training) throws Exception {
                            if (training != null && training.getSessions() != null && !training.getSessions().isEmpty()) {
                                mBinding.progressBar.setVisibility(View.GONE);
                                mBinding.layoutRetry.setVisibility(View.GONE);
                                initializeRecyclerView(training.getSessions());
                            } else {
                                noResultFound();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.layoutRetry.setVisibility(View.VISIBLE);

                            // Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void getSessionsOffline(final String trainingId) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.layoutRetry.setVisibility(View.GONE);

        Observable.create(new ObservableOnSubscribe<ArrayList<TrainingSession>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<TrainingSession>> e) throws Exception {
                ArrayList<TrainingSession> trainingSessions = mHomeModel.getTrainingSessionsByTrainingId(trainingId);
                e.onNext(trainingSessions);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<TrainingSession>>() {
                    @Override
                    public void accept(ArrayList<TrainingSession> trainingSessions) throws Exception {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.layoutRetry.setVisibility(View.GONE);

                        if (trainingSessions != null && !trainingSessions.isEmpty()) {
                            initializeRecyclerView(trainingSessions);
                        } else {
                            noResultFound();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getSessionsByDateRange(final String startDate, final String endDate) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.layoutRetry.setVisibility(View.GONE);

        Observable.create(new ObservableOnSubscribe<ArrayList<TrainingSession>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<TrainingSession>> e) throws Exception {
                ArrayList<TrainingSession> trainingSessions = mHomeModel.getTrainingSessionsByDateRange(startDate, endDate);
                e.onNext(trainingSessions);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<TrainingSession>>() {
                    @Override
                    public void accept(ArrayList<TrainingSession> trainingSessions) throws Exception {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.layoutRetry.setVisibility(View.GONE);

                        if (trainingSessions != null && !trainingSessions.isEmpty()) {
                            initializeRecyclerView(trainingSessions);
                        } else {
                            noResultFound();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void initializeRecyclerView(ArrayList<TrainingSession> trainingSessions) {
        TrainingSessionAdapter trainingSessionAdapter = new TrainingSessionAdapter(trainingSessions);
        mBinding.list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.list.setAdapter(trainingSessionAdapter);
        mBinding.list.setVisibility(View.VISIBLE);

    }

    private void noResultFound() {
        mBinding.list.setVisibility(View.GONE);
        mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        mBinding.imageViewNoResult.setImageResource(R.drawable.clock_g);
        mBinding.textViewNoResult.setText(getContext().getString(R.string.messageNoTrainingSessionsFound));
    }

    private class TrainingSessionAdapter extends RecyclerView.Adapter<TrainingSessionAdapter.ViewHolder> {
        private ArrayList<TrainingSession> mTrainingSessions = new ArrayList<>();

        public TrainingSessionAdapter(ArrayList<TrainingSession> trainingSessions) {
            mTrainingSessions = trainingSessions;
        }

        @Override
        public TrainingSessionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutTrainingSessionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_training_session_item, parent, false);
            return new TrainingSessionAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final TrainingSessionAdapter.ViewHolder holder, int position) {
            final TrainingSession trainingSession = mTrainingSessions.get(position);
            setSessionTitle(trainingSession.getTitle(), holder.mBinding.textViewName);
            holder.mBinding.textViewDate.setText(DateUtils.getFormatedDateFromDate(DateUtils.convertrIsoDate(trainingSession.getDate())));
            holder.mBinding.textViewTime.setText("From " + getTimeString(DateUtils.convertrIsoDate(trainingSession.getStartTime())) + " To " + getTimeString(DateUtils.convertrIsoDate(trainingSession.getEndTime())));

            setStudyReferences(trainingSession.getStudyReferences(), holder.mBinding);
            setTopicCovered(holder.mBinding, trainingSession.getTopics());
//            holder.mBinding.buttonViewMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    holder.mBinding.layoutViewMore.setVisibility(View.VISIBLE);
//                    holder.mBinding.buttonViewMore.setVisibility(View.GONE);
//                }
//            });

//            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    getContext().startActivity(SessionDetailActivity.getStartIntent(getContext(), holder.mBinding.textViewName.getText().toString(), trainingSession.getObjectId()));
//                }
//            });
        }

        private void setSessionTitle(String title, TextViewCustom textViewTitle) {
            if (!TextUtils.isEmpty(title)) {
                textViewTitle.setText(title);
            } else {
                textViewTitle.setText(getContext().getString(R.string.labelSession));
            }
        }

        private void setStudyReferences(ArrayList<AboutCourse> aboutCourses, LayoutTrainingSessionItemBinding binding) {
            if (aboutCourses != null && !aboutCourses.isEmpty()) {
                binding.layoutStudyReferences.setVisibility(View.VISIBLE);
                binding.recyclerViewStudyReferences.setNestedScrollingEnabled(false);
                StudyReferencesAdapter studyReferencesAdapter = new StudyReferencesAdapter(getContext(), aboutCourses);
                binding.recyclerViewStudyReferences.setAdapter(studyReferencesAdapter);
                binding.recyclerViewStudyReferences.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            }
        }

        private void setSessionTopic(Topic sessionTopic, LayoutTrainingSessionItemBinding binding) {
            if (sessionTopic != null && !TextUtils.isEmpty(sessionTopic.getName())) {
                binding.textViewTopic.setText(sessionTopic.getName());

            } else {
                binding.textViewTopic.setVisibility(View.INVISIBLE);
            }
        }

        private String getTimeString(Date date) {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String dateString = formatter.format(date);
            return dateString;
        }

        private void setSessionGoal(LayoutTrainingSessionItemBinding binding, String goal) {
            if (!TextUtils.isEmpty(goal)) {
                TextViewMore.viewMore(goal, binding.textViewGoal, binding.includeTextViewMoreLessGoal.textViewMoreLess);
            } else {
                binding.layoutGoal.setVisibility(View.GONE);
            }
        }

        private void setTopicCovered(LayoutTrainingSessionItemBinding binding, ArrayList<Topic> topics) {
            if (topics != null && !topics.isEmpty()) {
                binding.layoutTopicCovered.setVisibility(View.VISIBLE);
                binding.recyclerViewTopicCovered.setNestedScrollingEnabled(false);
                binding.recyclerViewTopicCovered.setLayoutManager(new GridLayoutManager(getContext(), 1));
                TopicCoveredAdapter topicCoveredAdapter = new TopicCoveredAdapter(topics);
                binding.recyclerViewTopicCovered.setAdapter(topicCoveredAdapter);

            }
        }

        @Override
        public int getItemCount() {
            return mTrainingSessions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutTrainingSessionItemBinding mBinding;

            public ViewHolder(LayoutTrainingSessionItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class TopicCoveredAdapter extends RecyclerView.Adapter<TopicCoveredAdapter.ViewHolder> {
        private ArrayList<Topic> mList;

        public TopicCoveredAdapter(ArrayList<Topic> topics) {
            mList = topics;
        }

        @Override
        public TopicCoveredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutRecyclerviewSimpleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_recyclerview_simple_item, parent, false);
            return new TopicCoveredAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(TopicCoveredAdapter.ViewHolder holder, int position) {
            Topic item = mList.get(position);
            if (item != null && !TextUtils.isEmpty(item.getName())) {
                holder.mBinding.textviewItem.setText(item.getName());
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutRecyclerviewSimpleItemBinding mBinding;

            public ViewHolder(LayoutRecyclerviewSimpleItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    private class StudyReferencesAdapter extends RecyclerView.Adapter<StudyReferencesAdapter.ViewHolder> {
        private ArrayList<AboutCourse> mAboutCourses;
        private Context mContext;

        public StudyReferencesAdapter(Context context, ArrayList<AboutCourse> aboutCourses) {
            mAboutCourses = aboutCourses;
            mContext = context;
        }

        @Override
        public StudyReferencesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutStudyReferenceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_study_reference_item, parent, false);
            return new StudyReferencesAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(StudyReferencesAdapter.ViewHolder holder, int position) {
            final AboutCourse video = mAboutCourses.get(position);

            holder.mBinding.imageViewThumbnail.setTag(video.getName());
            holder.mBinding.textViewTitle.setText(video.getTitle());

            if (!TextUtils.isEmpty(video.getMetaInformation().getTopic().getName())) {
                holder.mBinding.textViewTopic.setText(video.getMetaInformation().getTopic().getName());
            }

            String imagePath = video.getThumbnail().getThumb();
            if (imagePath.isEmpty()) {
                imagePath = video.getThumbnail().getUrl();
            }
            if (imagePath.isEmpty()) {
                imagePath = video.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(mContext).load(imagePath).into(holder.mBinding.imageViewThumbnail);
                } else {
                    Picasso.with(mContext).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(mContext).load(video.getThumbnail().getThumb()).into(holder.mBinding.imageViewThumbnail);
                } catch (Exception e1) {
                    try {
                        Picasso.with(mContext).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            Class objectClass = null;
            String type = "";
            int typeImage = R.drawable.digital_book;
            String typeExt = video.getMicroCourseType().toLowerCase();
            if (video.getCourseType().equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
                type = "Digital Book";
                typeImage = R.drawable.digital_book;
            } else if (video.getCourseType().equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
                type = "Video Course";
                typeImage = R.drawable.video_course;
            } else if (video.getCourseType().contains("feature")) {
                objectClass = MicroLearningCourse.class;
                typeImage = R.drawable.video_course;
            } else if (typeExt.contains("map")) {
                objectClass = ConceptMap.class;
                type = "Concept Map";
                typeImage = R.drawable.concept_map;
            } else if (typeExt.contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
                type = "Interactive Image";
                typeImage = R.drawable.interactive_image;
            } else if (typeExt.contains("video")) {
                objectClass = InteractiveVideo.class;
                type = "Interactive Video";
                typeImage = R.drawable.interactive_image;
            } else {
                type = "Pop Up";
                if (video.getPopUpType() != null && !TextUtils.isEmpty(video.getPopUpType().getValue())) {
                    objectClass = PopUps.class;
                    type = video.getPopUpType().getValue();
                    typeImage = R.drawable.popup;
                }
            }
            final Class finalObjectClass = objectClass;
            holder.mBinding.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalObjectClass != null) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(getContext(), video.getObjectId()));
                        } else {
                            mContext.startActivity(CourseDetailActivity.getStartActivityIntent(mContext, video.getObjectId(), finalObjectClass, ""));
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mAboutCourses.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutStudyReferenceItemBinding mBinding;

            public ViewHolder(LayoutStudyReferenceItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
