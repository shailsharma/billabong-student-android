package in.securelearning.lil.android.home.views.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutItemGroupMembersBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerviewSimpleItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutSessionDetailBinding;
import in.securelearning.lil.android.app.databinding.LayoutStudyReferenceItemBinding;
import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Skill;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.dataobjects.TrainingSession;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.courses.views.activity.CourseDetailActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.profile.views.activity.UserPublicProfileActivity;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import in.securelearning.lil.android.syncadapter.utils.TextViewMore;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 06-Jan-18.
 */

public class SessionDetailActivity extends AppCompatActivity {

    @Inject
    HomeModel mHomeModel;

    public static final String SESSION_ID = "objectId";
    public static final String SESSION_NUMBER = "sessionNumber";

    private String mSessionId, mSessionNumber;
    LayoutSessionDetailBinding mBinding;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorHome.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_session_detail);
        handleIntent();
        setUpToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mSessionId = getIntent().getStringExtra(SESSION_ID);
            mSessionNumber = getIntent().getStringExtra(SESSION_NUMBER);
            if (!TextUtils.isEmpty(mSessionId)) {
                getSessionDetail(mSessionId);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setUpToolbar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.lableSessionDetail));
    }

    public static Intent getStartIntent(Context context, String sessionNumber, String objectId) {
        Intent intent = new Intent(context, SessionDetailActivity.class);
        intent.putExtra(SESSION_NUMBER, sessionNumber);
        intent.putExtra(SESSION_ID, objectId);
        return intent;
    }

    private void getSessionDetail(final String sessionId) {
        Observable.create(new ObservableOnSubscribe<TrainingSession>() {
            @Override
            public void subscribe(ObservableEmitter<TrainingSession> e) throws Exception {
                TrainingSession trainingSession = mHomeModel.getTrainingSessionById(sessionId);
                e.onNext(trainingSession);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TrainingSession>() {
                    @Override
                    public void accept(TrainingSession trainingSession) throws Exception {
                        initializeViewAndListeners(trainingSession);
                    }
                });
    }

    private void initializeViewAndListeners(TrainingSession trainingSession) {
        mBinding.textViewName.setText(mSessionNumber);
        //setSessionTopic(trainingSession.getSessionTopic().getName());
        setSessionDate(trainingSession.getDate());
        setSessionTime(trainingSession.getStartTime(), trainingSession.getEndTime());
        setSessionGoal(trainingSession.getGoal());
        setTopicCovered(trainingSession.getSkills());
        setStudyReferences(trainingSession.getStudyReferences());
        setTrainers(trainingSession.getTrainers());
    }

    private void setTopicCovered(ArrayList<Skill> skills) {
        if (skills != null && skills.isEmpty()) {
            mBinding.layoutTopicCovered.setVisibility(View.VISIBLE);
            mBinding.recyclerViewTopicCovered.setNestedScrollingEnabled(false);
            mBinding.recyclerViewTopicCovered.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            TopicCoveredAdapter topicCoveredAdapter = new TopicCoveredAdapter(skills);
            mBinding.recyclerViewTopicCovered.setAdapter(topicCoveredAdapter);

        }
    }

    private void setStudyReferences(ArrayList<AboutCourse> aboutCourses) {
        if (aboutCourses != null && !aboutCourses.isEmpty()) {
            mBinding.layoutStudyReferences.setVisibility(View.VISIBLE);
            mBinding.recyclerViewStudyReferences.setNestedScrollingEnabled(false);
            StudyReferencesAdapter studyReferencesAdapter = new StudyReferencesAdapter(getBaseContext(), aboutCourses);
            mBinding.recyclerViewStudyReferences.setAdapter(studyReferencesAdapter);
            mBinding.recyclerViewStudyReferences.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void setTrainers(ArrayList<Moderator> trainers) {
        if (!trainers.isEmpty()) {
            mBinding.recyclerViewTrainings.setNestedScrollingEnabled(false);
            mBinding.recyclerViewTrainings.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            TrainerAdapter trainerAdapter = new TrainerAdapter(getBaseContext(), trainers);
            mBinding.recyclerViewTrainings.setAdapter(trainerAdapter);
        }

    }

    private void setSessionTime(String startTime, String endTime) {
        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
            mBinding.textViewTime.setText(DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(startTime)) + " to " + DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(endTime)));
        } else {
            mBinding.textViewTime.setVisibility(View.GONE);
        }
    }

    private void setSessionDate(String date) {
        if (!TextUtils.isEmpty(date)) {
            mBinding.textViewDate.setText(DateUtils.getDateTimeStringWithDateMonthHourMinuteAMPM(DateUtils.convertrIsoDate(date)));
        } else {
            mBinding.textViewDate.setVisibility(View.GONE);
        }
    }

    private void setSessionTopic(String name) {
        if (!TextUtils.isEmpty(name)) {
            mBinding.textViewTopic.setText(name);
        } else {
            mBinding.textViewTopic.setVisibility(View.GONE);
        }
    }

    private void setSessionGoal(String goal) {
        if (!TextUtils.isEmpty(goal)) {
            TextViewMore.viewMore(goal, mBinding.textViewGoal, mBinding.includeTextViewMoreLessGoal.textViewMoreLess);
        } else {
            mBinding.layoutGoal.setVisibility(View.GONE);
        }
    }

    private class TopicCoveredAdapter extends RecyclerView.Adapter<TopicCoveredAdapter.ViewHolder> {
        private ArrayList<Skill> mSkills;

        public TopicCoveredAdapter(ArrayList<Skill> skills) {
            mSkills = skills;
        }

        @Override
        public TopicCoveredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutRecyclerviewSimpleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_recyclerview_simple_item, parent, false);
            return new TopicCoveredAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(TopicCoveredAdapter.ViewHolder holder, int position) {
            Skill skill = mSkills.get(position);
            if (skill.getTopic() != null && !TextUtils.isEmpty(skill.getTopic().getName())) {
                holder.mBinding.textviewItem.setText(skill.getTopic().getName());

            }

        }

        @Override
        public int getItemCount() {
            return mSkills.size();
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

            String imagePath = video.getThumbnail().getUrl();
            if (imagePath.isEmpty()) {
                imagePath = video.getThumbnail().getThumb();
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
                    mContext.startActivity(CourseDetailActivity.getStartActivityIntent(mContext, video.getObjectId(), finalObjectClass, ""));
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

    private class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.ViewHolder> {
        private ArrayList<Moderator> mModerators;
        private Context mContext;

        public TrainerAdapter(Context context, ArrayList<Moderator> moderators) {
            mModerators = moderators;
            mContext = context;
        }

        @Override
        public TrainerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutItemGroupMembersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_group_members, parent, false);
            return new TrainerAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(TrainerAdapter.ViewHolder holder, int position) {
            final Moderator moderator = mModerators.get(position);
            holder.mBinding.textViewUserName.setText(moderator.getName());

            setUserThumbnail(mContext, moderator.getName(), moderator.getPic(), holder.mBinding.imageViewUserIcon);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        startActivity(UserPublicProfileActivity.getStartIntent(mContext, moderator.getId()));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(mContext, view);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mModerators.size();
        }

        private void setUserThumbnail(Context context, String name, Thumbnail thumbnail, AppCompatImageView imageView) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getLocalUrl())) {
                Picasso.with(context).load(thumbnail.getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(context).load(thumbnail.getUrl()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(context).load(thumbnail.getThumb()).transform(new CircleTransform()).placeholder(R.drawable.icon_profile_large).resize(300, 300).centerCrop().into(imageView);
            } else {
                String firstWord = name.substring(0, 1).toUpperCase();
                TextDrawable textDrawable = TextDrawable.builder().buildRound(firstWord, R.color.colorPrimary);
                imageView.setImageDrawable(textDrawable);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutItemGroupMembersBinding mBinding;

            public ViewHolder(LayoutItemGroupMembersBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

}
