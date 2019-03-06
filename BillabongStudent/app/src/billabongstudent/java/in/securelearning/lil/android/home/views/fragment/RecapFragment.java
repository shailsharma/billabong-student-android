package in.securelearning.lil.android.home.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutLessonPlanItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.base.views.activity.WebPlayerLiveActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;

/**
 * Created by Chaitendra on 12-Feb-18.
 */

public class RecapFragment extends Fragment {

    @Inject
    NetworkModel mNetworkModel;
    LayoutRecyclerViewBinding mBinding;

    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECTS = "subjects";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String TOPIC_ID = "topic_id";
    public static final String GRADE_ID = "grade_id";
    public static final String SECTION_ID = "section_id";
    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String DATE = "date";

    private int mColumnCount = 1;
    private String mSubjectId;
    private ArrayList<String> mSubjects;
    private String mSubjectName;
    private String mTopicId;
    private String mGradeId;
    private String mSectionId;
    private String mDate;
    private CourseAdapter mCourseAdapter;
    private int mSkip = 0;

    public static Fragment newInstance(int columnCount, String subjectId, ArrayList<String> subjects, String subjectName, String topicId, String gradeId, String sectionId, String date) {
        RecapFragment fragment = new RecapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SUBJECT_ID, subjectId);
        args.putStringArrayList(SUBJECTS, subjects);
        args.putString(SUBJECT_NAME, subjectName);
        args.putString(TOPIC_ID, topicId);
        args.putString(GRADE_ID, gradeId);
        args.putString(SECTION_ID, sectionId);
        args.putString(DATE, date);
        fragment.setArguments(args);
        return fragment;
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
        initializeRecyclerView(new ArrayList<AboutCourseExt>());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mSubjectId = getArguments().getString(SUBJECT_ID);
            mSubjects = getArguments().getStringArrayList(SUBJECTS);
            mSubjectName = getArguments().getString(SUBJECT_NAME);
            mTopicId = getArguments().getString(TOPIC_ID);
            mGradeId = getArguments().getString(GRADE_ID);
            mSectionId = getArguments().getString(SECTION_ID);
            mDate = getArguments().getString(DATE);
        }
        if ((mSubjectId.equals("58c92576bddb93e4048f6124") || (mSubjects != null && mSubjects.contains("58c92576bddb93e4048f6124"))) && mTopicId.equals("593e7015dfe1391d00a26a4f")) {
//
            AboutCourseExt aboutCourseExt1 = new AboutCourseExt();
            aboutCourseExt1.setObjectId("5ae422a681215e1300a7718e");
            aboutCourseExt1.setTitle("Recap: Control and Co-ordination");
            aboutCourseExt1.setName("featuredcard");
            aboutCourseExt1.getMetaInformation().getTopic().setName("Living World");
            aboutCourseExt1.setThumbnailUrl("https://lil.azureedge.net/lil-upload/1524873600000/reading_recap_copy_a16ZrJEkqfsWpG-a70ZB1UycMsbpG.jpg");

//            AboutCourseExt aboutCourseExt2 = new AboutCourseExt();
//            aboutCourseExt2.setObjectId("5a80519339682c1200593d60");
//            aboutCourseExt2.setTitle("Recap :- 3 Cell Organelles");
//            aboutCourseExt2.setName("digitalbook");
//            aboutCourseExt2.getMetaInformation().getTopic().setName("Living things");
//            aboutCourseExt2.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518146412/hcvsn2r9oevn0m1omnvj.jpg");
//
//            AboutCourseExt aboutCourseExt3 = new AboutCourseExt();
//            aboutCourseExt3.setObjectId("5a8052c639682c1200593d7f");
//            aboutCourseExt3.setTitle("Recap :- 4 Cell Organelles");
//            aboutCourseExt3.setName("digitalbook");
//            aboutCourseExt3.getMetaInformation().getTopic().setName("Living things");
//            aboutCourseExt3.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518325656/y2fht2lpi2xxjgl5tsqe.jpg");
//
//            AboutCourseExt aboutCourseExt4 = new AboutCourseExt();
//            aboutCourseExt4.setObjectId("5a8054ae39682c1200593d9e");
//            aboutCourseExt4.setTitle("Recap :- 5 Golgi Bodies, Lysosome , Plastid and Vacuoles");
//            aboutCourseExt4.setName("digitalbook");
//            aboutCourseExt4.getMetaInformation().getTopic().setName("Living things");
//            aboutCourseExt4.setThumbnailUrl("https://res.cloudinary.com/learnindialearn/image/upload/v1518326317/s0ha6ay7h5udcdwhcs9g.jpg");

            ArrayList<AboutCourseExt> list = new ArrayList<>();
            list.add(aboutCourseExt1);
            sampleAdapter(list);


        } else if ((mSubjectId.equals("58c9269abddb93e4048f6185") || (mSubjects != null && mSubjects.contains("58c9269abddb93e4048f6185"))) && mTopicId.equals("593e671edfe1391d00a267b1")) {

            AboutCourseExt aboutCourseExt1 = new AboutCourseExt();
            aboutCourseExt1.setObjectId("5ae413ee81215e1300a7716e");
            aboutCourseExt1.setTitle("Recap: Number Systems");
            aboutCourseExt1.setName("featuredcard");
            aboutCourseExt1.getMetaInformation().getTopic().setName("Number System");
            aboutCourseExt1.setThumbnailUrl("https://lil.azureedge.net/lil-upload/1524873600000/reading_recap_copy_a28ZSymA2m9_aM-a46ZSJ1TmcW6f.jpg");

            ArrayList<AboutCourseExt> list = new ArrayList<>();
            list.add(aboutCourseExt1);
            sampleAdapter(list);
        } else {
            noResultFound(0);
        }
        return mBinding.getRoot();
    }

    private void sampleAdapter(ArrayList<AboutCourseExt> list) {
        mBinding.layoutRetry.setVisibility(View.GONE);
        mBinding.list.setVisibility(View.VISIBLE);
        if (mCourseAdapter != null) {
            mCourseAdapter.addValues(list);
        }
    }

    private void getPrerequisiteCourses(final ArrayList<String> ids, final int skip, final int limit) {
//        if (GeneralUtils.isNetworkAvailable(getContext())) {
//            if (skip > 0) {
//                mBinding.bottomProgress.setVisibility(View.VISIBLE);
//            } else {
//                mBinding.progressBar.setVisibility(View.VISIBLE);
//                mBinding.list.setVisibility(View.GONE);
//            }
//
//            Observable.create(new ObservableOnSubscribe<ArrayList<AboutCourseExt>>() {
//                @Override
//                public void subscribe(ObservableEmitter<ArrayList<AboutCourseExt>> subscriber) throws Exception {
//                    if (ids != null && !ids.isEmpty()) {
//                        PrerequisiteCoursesPostData prerequisiteCoursesPostData = new PrerequisiteCoursesPostData();
//                        prerequisiteCoursesPostData.setCourseIds(ids);
//                        final Call<ArrayList<AboutCourseExt>> call = mNetworkModel.getPrerequisiteCourses(prerequisiteCoursesPostData);
//                        Response<ArrayList<AboutCourseExt>> response = call.execute();
//
//                        if (response != null && response.isSuccessful()) {
//                            ArrayList<AboutCourseExt> aboutCourses = response.body();
//                            Log.e("LessonPlans1--", "Successful");
//                            subscriber.onNext(aboutCourses);
//                        } else if (response.code() == 404) {
//                            throw new Exception("No Lesson Plan found.");
//                        } else if ((response.code() == 401) && SyncServiceHelper.refreshToken(getContext())) {
//                            Response<ArrayList<AboutCourseExt>> response2 = call.clone().execute();
//                            if (response2 != null && response2.isSuccessful()) {
//                                ArrayList<AboutCourseExt> aboutCourses = response.body();
//                                Log.e("LessonPlans2--", "Successful");
//                                subscriber.onNext(aboutCourses);
//                            } else if ((response2.code() == 401)) {
//                                startActivity(LoginActivity.getUnauthorizedIntent(getContext()));
//                            } else if (response2.code() == 404) {
//                                throw new Exception("No Lesson Plan found.");
//                            } else {
//                                Log.e("LessonPlans2--", "Failed");
//                                throw new Exception("Unable to fetch Lesson Plan");
//                            }
//                        } else {
//                            Log.e("LessonPlans1--", "Failed");
//                            throw new Exception("Unable to fetch Lesson Plan");
//                        }
//                    } else {
//                        subscriber.onNext(new ArrayList<AboutCourseExt>());
//                    }
//
//                    subscriber.onComplete();
//                }
//            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<ArrayList<AboutCourseExt>>() {
//                        @Override
//                        public void accept(ArrayList<AboutCourseExt> list) throws Exception {
//                            mBinding.progressBar.setVisibility(View.GONE);
//                            mSkip += list.size();
//                            noResultFound(mSkip);
//                            if (mCourseAdapter != null) {
//                                mCourseAdapter.addValues(list);
//                            }
//                            if (list.size() < limit) {
//                                mBinding.list.clearOnScrollListeners();
//                            }
//
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            throwable.printStackTrace();
//                            if (throwable.getMessage().equals(getContext().getString(R.string.messageNoLessonPlanFound))) {
//                                noResultFound(skip);
//                            } else {
//                                unableToFetch(ids, skip, limit);
//                            }
//                        }
//                    });
//        } else {
//            noInternet(ids, skip, limit);
//        }

    }

    private void noInternet(final ArrayList<String> courseIds, final int skip, final int limit) {
        String message = getContext().getString(R.string.error_message_no_internet);
        if (skip > 0) {
            Snackbar.make(mBinding.list, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction((R.string.labelRetry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getPrerequisiteCourses(courseIds, skip, limit);
                        }
                    })
                    .show();
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutRetry.setVisibility(View.VISIBLE);
            mBinding.imageViewRetry.setImageResource(R.drawable.no_internet);
            mBinding.textViewRetry.setText(getContext().getString(R.string.error_message_no_internet));
            mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPrerequisiteCourses(courseIds, skip, limit);
                }
            });
        }

    }

    private void unableToFetch(final ArrayList<String> courseIds, final int skip, final int limit) {
        mBinding.list.setVisibility(View.GONE);
        mBinding.layoutRetry.setVisibility(View.VISIBLE);
        mBinding.imageViewRetry.setImageResource(R.drawable.course_gray);
        mBinding.textViewRetry.setText(getContext().getString(R.string.messageLessonPlanFailed));
        mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPrerequisiteCourses(courseIds, skip, limit);
            }
        });
    }

    private void noResultFound(int skip) {
        if (skip > 0) {
            mBinding.list.setVisibility(View.VISIBLE);
            mBinding.layoutNoResult.setVisibility(View.GONE);
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.layoutNoResult.setVisibility(View.VISIBLE);
            mBinding.imageViewNoResult.setImageResource(R.drawable.course_gray);
            mBinding.textViewNoResult.setText(getContext().getString(R.string.messageNoLessonPlanFound));
        }

    }

    private void initializeRecyclerView(ArrayList<AboutCourseExt> aboutCourses) {

        LinearLayoutManager layoutManager = null;
//        if (mColumnCount > 1) {
//            layoutManager = new GridLayoutManager(getActivity(), 2);
//            mBinding.list.setLayoutManager(layoutManager);
//        } else {
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.list.setLayoutManager(layoutManager);
        //       }

        mCourseAdapter = new CourseAdapter(aboutCourses, getContext());
        mBinding.list.setAdapter(mCourseAdapter);
//
//        if (layoutManager != null) {
//            final LinearLayoutManager finalLayoutManager = layoutManager;
//            mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                    if (dy > 0) {
//                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mSkip - 1) {
//
//                            getPrerequisiteCourses(mPrerequisiteCoursesIds, mSkip, mLimit);
//
//                        }
//                    }
//
//                }
//
//            });
//        }


    }

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
        private ArrayList<AboutCourseExt> mValues = new ArrayList<>();
        Context mContext;


        public void dispose() {
            if (mValues != null) {
                mValues.clear();
                mValues = null;
            }
        }

        public CourseAdapter(ArrayList<AboutCourseExt> aboutCourseExts, Context context) {
            this.mValues = aboutCourseExts;
            this.mContext = context;
        }

        @Override
        public CourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutLessonPlanItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_lesson_plan_item, parent, false);
            return new CourseAdapter.ViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(final CourseAdapter.ViewHolder holder, final int position) {
            final AboutCourseExt video = mValues.get(position);
            holder.mBinding.imageViewThumbnail.setTag(video.getName());
            if (!TextUtils.isEmpty(video.getMetaInformation().getTopic().getName()))
                holder.mBinding.textViewTopic.setText(video.getMetaInformation().getTopic().getName());

            String imagePath = video.getThumbnailUrl();
            if (imagePath.isEmpty()) {
                imagePath = video.getThumbnail().getThumb();
            }
            if (imagePath.isEmpty()) {
                imagePath = video.getMetaInformation().getBanner();
            }
            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(holder.mBinding.imageViewThumbnail);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(video.getThumbnail().getThumb()).into(holder.mBinding.imageViewThumbnail);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(holder.mBinding.imageViewThumbnail);
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
            String typeExt = video.getName().toLowerCase();
            if (typeExt.equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
                type = "Digital Book";
                typeImage = R.drawable.digital_book;
            } else if (typeExt.equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
                type = "Video Course";
                typeImage = R.drawable.video_course;
            } else if (typeExt.contains("map")) {
                objectClass = ConceptMap.class;
                type = "Concept Map";
                typeImage = R.drawable.concept_map;
            } else if (typeExt.contains("feature")) {
                objectClass = MicroLearningCourse.class;
                type = "Feature Card";
                typeImage = R.drawable.video_course;
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
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, video.getObjectId()));
                        } else if (finalObjectClass.equals(VideoCourse.class) || finalObjectClass.equals(InteractiveVideo.class)) {
                            WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), video.getObjectId(), video.getMetaInformation().getSubject().getId(), video.getMetaInformation().getTopic().getId(), finalObjectClass, "", false);
                        } else {
                            WebPlayerLiveActivity.startWebPlayer(getContext(), video.getObjectId(), video.getMetaInformation().getSubject().getId(), video.getMetaInformation().getTopic().getId(), finalObjectClass, "", false, true);
                        }
                    } else {
                        ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                    }
                }
            });
            holder.mBinding.text.setText(video.getTitle());
            holder.mBinding.textViewVideoDuration.setVisibility(View.GONE);
            holder.mBinding.favoriteImg.setVisibility(View.GONE);
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void addValues(ArrayList<AboutCourseExt> aboutCourseExts) {
            if (mValues != null) {
                mValues.addAll(aboutCourseExts);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (mValues != null) {
                mValues.clear();
                notifyDataSetChanged();
            }
        }

        public void refresh(String objectId) {
            for (int i = 0; i < mValues.size(); i++) {
                if (mValues.get(i).getObjectId().equalsIgnoreCase(objectId)) {
                    mValues.get(i).setDocId("");
                    notifyItemChanged(i);
                    break;
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            LayoutLessonPlanItemBinding mBinding;

            ViewHolder(LayoutLessonPlanItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                mBinding.imgShare.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.img_share:
                        shareVideo(mBinding.thumbnail.getTag().toString());
                        break;
                }
            }

            private void shareVideo(String path) {
                Toast.makeText(mContext, "in Share  mode " + path, Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(path);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                shareIntent.setType("video/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(Intent.createChooser(shareIntent, "send"));
            }
        }

        public String convert(int seconds) {
            String strMinutes = "";
            String strSeconds = "";
            int minutes;
            int hours;
            int newSeconds;
            int secondMod = (seconds % 3600);
            hours = seconds / 3600;
            minutes = secondMod / 60;
            newSeconds = secondMod % 60;

            strMinutes = String.valueOf(minutes);
            strSeconds = String.valueOf(newSeconds);

            if (minutes >= 0 && minutes <= 9) {
                strMinutes = "0" + strMinutes;
            }
            if (newSeconds >= 0 && newSeconds <= 9) {
                strSeconds = "0" + strSeconds;

            }
            if (hours == 0) {
                return strMinutes + ":" + strSeconds;

            } else {
                return String.valueOf(hours) + ":" + strMinutes + ":" + strSeconds;
            }

        }
    }

}
