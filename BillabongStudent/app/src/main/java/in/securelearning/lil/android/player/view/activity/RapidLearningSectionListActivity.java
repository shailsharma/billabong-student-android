package in.securelearning.lil.android.player.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCourseDetailPageBinding;
import in.securelearning.lil.android.app.databinding.LayoutCourseSectionItemBinding;
import in.securelearning.lil.android.base.dataobjects.CourseSection;
import in.securelearning.lil.android.base.dataobjects.MetaInformation;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 19-Feb-18.
 */

public class RapidLearningSectionListActivity extends AppCompatActivity {

    @Inject
    PlayerModel mPlayerModel;

    LayoutCourseDetailPageBinding mBinding;

    public static final String OBJECT_ID = "id";
    public static final String SUFFIX_LESSON = "Lesson ";
    public static final String SUFFIX_RECAP = "Recap ";
    private String mId;
    private String mColor;
    private int mSavedIndex = -1;
//    public static TextToSpeechUtils mTextToSpeechUtils;

    private final int CHECK_CODE = 1;
    private String mCourseType;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent();

//        if (mAdapter != null) {
//            mAdapter.refreshItem(mSavedIndex);
//        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mTextToSpeechUtils != null) {
//            mTextToSpeechUtils.destroyTTS();
//            Log.d("TTS", "TTS destroyed");
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CHECK_CODE) {
//            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
//                mTextToSpeechUtils = new TextToSpeechUtils(getBaseContext());
//            } else {
//                Intent install = new Intent();
//                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//                startActivity(install);
//            }
//        }
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_course_detail_page);
//        checkTTS();
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mId = getIntent().getStringExtra(OBJECT_ID);
            if (!TextUtils.isEmpty(mId)) {
                getCourseDetail(mId);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /*Checking TTS availability on device.*/
//    private void checkTTS() {
//        Intent check = new Intent();
//        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//        startActivityForResult(check, CHECK_CODE);
//    }

    @SuppressLint("CheckResult")
    private void getCourseDetail(final String id) {
        if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.scrollView.setVisibility(View.GONE);
            mPlayerModel.getRapidLearningCourse(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MicroLearningCourse>() {
                        @Override
                        public void accept(MicroLearningCourse microLearningCourse) throws Exception {

                            if (microLearningCourse != null && microLearningCourse.getObjectId().equals(id)) {
                                initializeUiAndListeners(microLearningCourse);
                                setUpToolbar();
                            } else {
                                unableToFetchData();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mBinding.progressBar.setVisibility(View.GONE);
                            if (throwable instanceof SocketTimeoutException) {
                                unableToFetchData();

                            } else if (throwable.getMessage().equals(getString(R.string.messageCourseDetailNotFound))) {
                                unableToFetchData();

                            }
                        }
                    });
        } else {
            showRetryLayout(getString(R.string.error_message_no_internet));
        }
    }

    private void unableToFetchData() {
        Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
        finish();
    }

    public static Intent getStartIntent(Context context, String id) {
        Intent intent = new Intent(context, RapidLearningSectionListActivity.class);
        intent.putExtra(OBJECT_ID, id);
        return intent;
    }

    private void setUpToolbar() {

        mBinding.buttonReflection.setVisibility(View.GONE);

        mBinding.closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void initializeUiAndListeners(final MicroLearningCourse course) {
        mBinding.scrollView.setVisibility(View.VISIBLE);
        setSessionListTitle(course.getMetaInformation(), course.getCourseSections().size());
        setBackgroundColor(course);
        setThumbnail(course.getThumbnail());
        setGradeAndSubject(course.getMetaInformation().getGrade().getName(), course.getMetaInformation().getSubject().getName());
        setRating(course.getAvgRating());
        setCourseTitle(course.getTitle());
        setCourseCurator(course.getAuthor().getName());
        setCourseDescription(course.getDescription());
        initializeRecyclerView(course.getObjectId(), course.getCourseSections(), getString(R.string.label_section));
        mCourseType = course.getCourseType();

        mBinding.layoutRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   startActivity(CourseRatingAndFeedbackActivity.getStartIntent(getBaseContext(), course.getObjectId()));
            }
        });

    }

    private void setSessionListTitle(MetaInformation metaInformation, int sectionSize) {

        if (metaInformation != null) {
            String listTitle = getString(R.string.labelNoOfSessions) + " (" + metaInformation.getTotalSessions() + ")";
            mBinding.textViewListTitle.setText(listTitle);
        } else {
            mBinding.textViewListTitle.setText(R.string.labelSessions);
        }


    }

    private void setRating(double avgRating) {
        mBinding.textViewRatingValue.setText(String.valueOf(avgRating));
        mBinding.ratingBar.setRating((float) avgRating);
        if (avgRating != 0) {
            mBinding.layoutRating.setVisibility(View.VISIBLE);
            mBinding.textViewRatingValue.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
        } else {
            mBinding.layoutRating.setVisibility(View.GONE);
        }
    }

    private void setGradeAndSubject(String grade, String subject) {
        if (!TextUtils.isEmpty(grade) && !TextUtils.isEmpty(subject)) {
            String value = grade + " - " + subject;
            mBinding.textViewGradeSubject.setVisibility(View.VISIBLE);
            mBinding.textViewGradeSubject.setText(value);
        } else if (!TextUtils.isEmpty(grade)) {
            mBinding.textViewGradeSubject.setVisibility(View.VISIBLE);
            mBinding.textViewGradeSubject.setText(grade);
        } else if (!TextUtils.isEmpty(subject)) {
            mBinding.textViewGradeSubject.setVisibility(View.VISIBLE);
            mBinding.textViewGradeSubject.setText(subject);
        } else {
            mBinding.textViewGradeSubject.setVisibility(View.INVISIBLE);
        }

    }

    private void setThumbnail(Thumbnail thumbnail) {
        if (thumbnail != null) {
            String imagePath = thumbnail.getUrl();

            if (TextUtils.isEmpty(imagePath)) {
                imagePath = thumbnail.getThumb();
            }

            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getBaseContext()).load(imagePath).placeholder(R.drawable.image_placeholder).into(mBinding.imageViewThumbnail);
                } else {
                    mBinding.imageViewThumbnail.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getBaseContext()).load(thumbnail.getThumb()).into(mBinding.imageViewThumbnail);
                } catch (Exception e1) {
                    try {
                        mBinding.imageViewThumbnail.setVisibility(View.GONE);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            mBinding.imageViewThumbnail.setVisibility(View.GONE);
        }
    }

    private String getSuffix(String featureType) {
        if (!TextUtils.isEmpty(featureType)) {
            if (featureType.equalsIgnoreCase("lessonPlan")) {
                return SUFFIX_LESSON;
            } else {
                return SUFFIX_RECAP;
            }
        } else {
            return SUFFIX_LESSON;
        }
    }

    private void setBackgroundColor(MicroLearningCourse course) {
        if (course.getCourseSections() != null
                && !course.getCourseSections().isEmpty()
                && course.getCourseSections().get(0).getCourseSectionCards() != null
                && !course.getCourseSections().get(0).getCourseSectionCards().isEmpty()
                && course.getCourseSections().get(0).getCourseSectionCards().get(0).getCourseSectionCardSetting() != null
                && !TextUtils.isEmpty(course.getCourseSections().get(0).getCourseSectionCards().get(0).getCourseSectionCardSetting().getColor())) {
            mColor = course.getCourseSections().get(0).getCourseSectionCards().get(0).getCourseSectionCardSetting().getColor();
            mBinding.layoutMain.setBackgroundColor(Color.parseColor(mColor));
            getWindow().setStatusBarColor(Color.parseColor(mColor));
        } else {
            mColor = "#00796b";
            mBinding.layoutMain.setBackgroundColor(Color.parseColor(mColor));
            getWindow().setStatusBarColor(Color.parseColor(mColor));
        }
    }

    private void setCourseTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mBinding.textViewTitle.setText(title);
        } else {
            mBinding.textViewTitle.setText(getString(R.string.labelRapidLearning));
        }
    }

    private void setCourseCurator(String name) {
        if (!TextUtils.isEmpty(name)) {
            String value = getString(R.string.curatedBy) + name;
            mBinding.textViewCurator.setText(value);
        } else {
            mBinding.textViewCurator.setVisibility(View.GONE);
        }
    }

    private void setCourseDescription(String description) {

        if (!TextUtils.isEmpty(description)) {
            mBinding.textViewDescription.setVisibility(View.VISIBLE);
            mBinding.textViewDescription.setText(description);
        } else {
            mBinding.textViewDescription.setVisibility(View.GONE);
        }
    }

    /*show retry layout and hide main layout*/
    private void showRetryLayout(String message) {
        mBinding.scrollView.setVisibility(View.GONE);
        mBinding.layoutRetry.setVisibility(View.VISIBLE);
        mBinding.imageViewRetry.setImageResource(R.drawable.icon_no_internet_white);
        mBinding.textViewRetry.setText(message);
        mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.layoutRetry.setVisibility(View.GONE);
                mBinding.scrollView.setVisibility(View.VISIBLE);
                handleIntent();
            }
        });
    }

    private void initializeRecyclerView(String courseId, ArrayList<CourseSection> list, String suffix) {
        if (list != null && !list.isEmpty()) {
            mBinding.recyclerView.setNestedScrollingEnabled(false);
            if (getResources().getBoolean(R.bool.isTablet)) {
                mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2, GridLayoutManager.VERTICAL, false));
            } else {
                mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            }

            RecyclerViewAdapter adapter = new RecyclerViewAdapter(courseId, list, suffix);
            mBinding.recyclerView.setAdapter(adapter);

            mBinding.recyclerView.setVisibility(View.VISIBLE);
            mBinding.textViewNoLesson.setVisibility(View.GONE);
        } else {
            mBinding.recyclerView.setVisibility(View.GONE);
            mBinding.textViewNoLesson.setVisibility(View.VISIBLE);
        }

        mBinding.progressBar.setVisibility(View.GONE);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private ArrayList<CourseSection> mList;
        private String mSuffix, mCourseId;
        //private CourseProgress mCourseProgress;

        RecyclerViewAdapter(String courseId, ArrayList<CourseSection> list, String suffix) {
            mCourseId = courseId;
            mList = list;
            mSuffix = suffix;
            // mCourseProgress = mPlayerModel.getCourseProgress(mCourseId);

        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutCourseSectionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_course_section_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
            final CourseSection object = mList.get(position);
            setLessonNumber(String.valueOf(position + 1), String.valueOf(mList.size()), holder.mBinding.textViewSectionNumber);
            setSubTopic("", holder.mBinding);
            setTitle(object.getTitle(), holder.mBinding.textViewSectionTitle);
            setProgress(holder.mBinding, object, position);

        }

        private void refreshItem(int index) {
            if (index > -1 && index < mList.size()) {
                // mCourseProgress = mPlayerModel.getCourseProgress(mCourseId);
                notifyItemChanged(index);
            }

        }

        private void setProgress(LayoutCourseSectionItemBinding binding, final CourseSection object, final int position) {
            int progress;
            float textProgress;
            int max = object.getCourseSectionCards().size();
            binding.progressBar.setMax(max);
//            if (mCourseProgress != null && !TextUtils.isEmpty(mCourseProgress.getObjectId())) {
//                ArrayList<SectionProgress> list = mCourseProgress.getSectionProgresses();
//                if (list != null && !list.isEmpty()) {
//                    int index = list.indexOf(new SectionProgress(object.getObjectId(), 0));
//                    if (index > -1 && list.get(index).getProgress() > -1) {
            progress = object.getSectionProgress();
            binding.progressBar.setProgress(progress);

            textProgress = binding.progressBar.getProgress();
            float averageProgress = (textProgress / max) * 100;
            String progressValue = Math.round(averageProgress) + "%";
            binding.textViewProgress.setText(progressValue);
//                    }
//                }
//            }

            final int sectionProgress = progress;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        if (!TextUtils.isEmpty(mId) && !TextUtils.isEmpty(object.getObjectId()) && object.getCourseSectionCards() != null && !object.getCourseSectionCards().isEmpty()) {
//                        if (object.getCourseSectionCards().size() == sectionProgress) {
//                            mSavedIndex = -1;
//                            startActivity(RapidLearningCardsActivity.getStartIntent(getBaseContext(), mId, object.getTitle(), object.getObjectId(), mColor, object.getCourseSectionCards(), mCourseType, 0));
//                        } else {
//                            mSavedIndex = position;
//                            startActivity(RapidLearningCardsActivity.getStartIntent(getBaseContext(), mId, object.getTitle(), object.getObjectId(), mColor, object.getCourseSectionCards(), mCourseType, object.getLastCardIndex()));
//
//                        }
                            if (object.getLastCardIndex() == (object.getCourseSectionCards().size() - 1)) {
                                startActivity(RapidLearningCardsActivity.getStartIntent(getBaseContext(), mId, object.getTitle(), object.getObjectId(), mColor, object.getCourseSectionCards(), mCourseType, 0));

                            } else {
                                startActivity(RapidLearningCardsActivity.getStartIntent(getBaseContext(), mId, object.getTitle(), object.getObjectId(), mColor, object.getCourseSectionCards(), mCourseType, object.getLastCardIndex()));

                            }
                        } else {
                            SnackBarUtils.showSnackBar(getBaseContext(), mBinding.getRoot(), getString(R.string.error_something_went_wrong), SnackBarUtils.UNSUCCESSFUL);
                        }
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.getRoot());
                    }
                }
            });

        }

        private void setTitle(String title, TextViewCustom textView) {
            if (!TextUtils.isEmpty(title)) {
                textView.setText(title);
            } else {
                textView.setVisibility(View.INVISIBLE);
            }
        }

        private void setSubTopic(String subTopic, LayoutCourseSectionItemBinding binding) {
            if (!TextUtils.isEmpty(subTopic)) {
                binding.textViewSectionSubTopic.setText(subTopic);
            } else {
                binding.textViewSectionSubTopic.setVisibility(View.GONE);
            }
        }

        private void setLessonNumber(String number, String total, TextViewCustom textView) {
            String values = mSuffix + " " + number + "/" + total;
            textView.setText(values);
            textView.setTextColor(Color.parseColor(mColor));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LayoutCourseSectionItemBinding mBinding;

            ViewHolder(LayoutCourseSectionItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
