package in.securelearning.lil.android.player.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

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
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.widget.TextViewCustom;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.events.RapidLearningSectionListScreenRefreshEvent;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 19-Feb-18.
 */

public class RapidLearningSectionListActivity extends AppCompatActivity {

    @Inject
    PlayerModel mPlayerModel;

    @Inject
    RxBus mRxBus;

    LayoutCourseDetailPageBinding mBinding;

    public static final String OBJECT_ID = "id";
    private String mId;
    private String mColor;

    private String mCourseType, mCourseId;
    private long mStartTime;
    private Disposable mDisposable;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_course_detail_page);

        mStartTime = System.currentTimeMillis();
        handleIntent();
        listenRxEvent();
    }

    @Override
    protected void onDestroy() {
        mPlayerModel.uploadUserTimeSpent(mCourseId, mCourseType,
                null, null, mStartTime, System.currentTimeMillis());

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        super.onDestroy();
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

    private void listenRxEvent() {
        mDisposable = mRxBus.toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(Object event) throws Exception {
                        if (event instanceof RapidLearningSectionListScreenRefreshEvent) {
                            handleIntent();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

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
                        public void accept(MicroLearningCourse microLearningCourse) {

                            if (microLearningCourse != null && microLearningCourse.getObjectId().equals(id)) {
                                initializeUiAndListeners(microLearningCourse);
                                setUpToolbar();
                            } else {
                                unableToFetchData();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
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
        setThumbnail(course.getThumbnail(), mBinding.imageViewThumbnail);
        setGradeAndSubject(course.getMetaInformation().getGrade().getName(), course.getMetaInformation().getSubject().getName());
        setRating(course.getAvgRating());
        setCourseTitle(course.getTitle());
        setCourseCurator(course.getAuthor().getName());
        setCourseDescription(course.getDescription());
        initializeRecyclerView(course.getCourseSections(), getString(R.string.label_section));
        mCourseType = course.getCourseType();
        mCourseId = course.getObjectId();

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

    private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {

        int placeholder = R.drawable.image_placeholder;

        if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
            Picasso.with(getBaseContext()).load(thumbnail.getThumbXL()).placeholder(placeholder).fit().centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
            Picasso.with(getBaseContext()).load(thumbnail.getThumb()).placeholder(placeholder).fit().centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
            Picasso.with(getBaseContext()).load(thumbnail.getUrl()).placeholder(placeholder).fit().centerCrop().into(imageView);
        } else {
            Picasso.with(getBaseContext()).load(placeholder).fit().centerCrop().into(imageView);
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

    private void initializeRecyclerView(ArrayList<CourseSection> list, String suffix) {
        if (list != null && !list.isEmpty()) {
            mBinding.recyclerView.setNestedScrollingEnabled(false);
            if (getResources().getBoolean(R.bool.isTablet)) {
                mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2, GridLayoutManager.VERTICAL, false));
            } else {
                mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
            }

            RecyclerViewAdapter adapter = new RecyclerViewAdapter(list, suffix);
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
        private String mSuffix;

        RecyclerViewAdapter(ArrayList<CourseSection> list, String suffix) {
            mList = list;
            mSuffix = suffix;

        }

        @NotNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            LayoutCourseSectionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getBaseContext()), R.layout.layout_course_section_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NotNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
            final CourseSection object = mList.get(position);

            //setCardDisplayAsPerStatus(holder.mBinding, object.getTeachingStatus());
            setLessonNumber(String.valueOf(position + 1), String.valueOf(mList.size()), holder.mBinding.textViewSectionNumber);
            setSubTopic(ConstantUtil.BLANK, holder.mBinding);
            setTitle(object.getTitle(), holder.mBinding.textViewSectionTitle);
            setProgress(holder.mBinding, object, object.getTeachingStatus());

        }

        private void setCardDisplayAsPerStatus(LayoutCourseSectionItemBinding courseSectionItemBinding, int teachingStatus) {
            if (teachingStatus == ConstantUtil.RECAP_SECTION_TEACHING_STATUS_UNLOCKED) {
                courseSectionItemBinding.textViewSectionNumber.setTextColor(Color.parseColor(mColor));
                courseSectionItemBinding.textViewSectionTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGreyDark));
                courseSectionItemBinding.textViewProgress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGreyDark));
                courseSectionItemBinding.getRoot().setEnabled(true);
            } else {
                courseSectionItemBinding.textViewSectionNumber.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey));
                courseSectionItemBinding.textViewSectionTitle.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey));
                courseSectionItemBinding.textViewProgress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorGrey));
                courseSectionItemBinding.getRoot().setEnabled(false);

            }
        }

        private void setProgress(LayoutCourseSectionItemBinding binding, final CourseSection object, final int teachingStatus) {
            int progress;
            float textProgress;
            int max = object.getCourseSectionCards().size();
            binding.progressBar.setMax(max);
            progress = object.getSectionProgress();
            binding.progressBar.setProgress(progress);

            textProgress = binding.progressBar.getProgress();
            float averageProgress = (textProgress / max) * 100;
            String progressValue = Math.round(averageProgress) + "%";
            binding.textViewProgress.setText(progressValue);


            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                        if (!TextUtils.isEmpty(mId) && !TextUtils.isEmpty(object.getObjectId()) && object.getCourseSectionCards() != null && !object.getCourseSectionCards().isEmpty()) {

                            if (object.getLastCardIndex() == (object.getCourseSectionCards().size() - 1)) {

                                startActivity(RapidLearningCardsActivity.getStartIntent(getBaseContext(), mId, object.getTitle(), object.getObjectId(), mColor, object.getCourseSectionCards(), mCourseType, 0));

                            } else {

                                startActivity(RapidLearningCardsActivity.getStartIntent(getBaseContext(), mId, object.getTitle(), object.getObjectId(), mColor, object.getCourseSectionCards(), mCourseType, object.getLastCardIndex()));

                            }
                            alterUiOfThisActivity();
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

    /*To make this ui smooth when user transit to next screen and back
     * after blocking unnecessary api call */
    private void alterUiOfThisActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.scrollView.setVisibility(View.GONE);
                mBinding.progressBar.setVisibility(View.VISIBLE);
            }
        }, 300);

    }
}
