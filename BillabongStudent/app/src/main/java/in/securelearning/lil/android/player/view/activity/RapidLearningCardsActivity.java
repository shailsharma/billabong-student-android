package in.securelearning.lil.android.player.view.activity;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;
import com.yuyakaido.android.cardstackview.internal.CardStackAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutSectionItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutSectionViewBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.CourseCardMedia;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.CourseSectionCard;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.SectionItems;
import in.securelearning.lil.android.base.dataobjects.SectionProgress;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.events.QuizCompletedEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.home.views.activity.PlayFullScreenImageActivity;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.InjectorPlayer;
import in.securelearning.lil.android.player.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static in.securelearning.lil.android.player.view.activity.QuizPlayerActivity.TYPE_ASSESSMENT_OF_LEARNING;

public class RapidLearningCardsActivity extends AppCompatActivity {

    public static final String COURSE_ID = "id";
    public static final String SECTION_TITLE = "sectionTitle";
    public static final String SECTION_ID = "sectionId";
    public static final String COLOR = "color";
    public static final String CARDS = "cards";
    public static final String PROGRESS = "progress";
    public static final String COURSE_TYPE = "courseType";
    @Inject
    PlayerModel mPlayerModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    RxBus mRxBus;
    LayoutSectionViewBinding mBinding;
    private String mCourseId, mSectionId;
    private int mProgress, mTotal = -1;
    private ArrayList<String> mCompletedItems = new ArrayList<>();
    private ArrayList<CourseSectionCard> mCourseSectionCards = new ArrayList<>();
    private Disposable mDisposable;
    private String mStartTime;
    private String mCourseType;

    public static Intent getStartIntent(Context context, String id, String title, String sectionId, String color, ArrayList<CourseSectionCard> cards, String courseType, int progress) {
        Intent intent = new Intent(context, RapidLearningCardsActivity.class);
        intent.putExtra(COURSE_ID, id);
        intent.putExtra(SECTION_TITLE, title);
        intent.putExtra(SECTION_ID, sectionId);
        intent.putExtra(COLOR, color);
        intent.putExtra(CARDS, cards);
        intent.putExtra(COURSE_TYPE, courseType);
        intent.putExtra(PROGRESS, progress);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveProgress(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_section_view);
        handleIntent();
        listenRxEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mCourseId = getIntent().getStringExtra(COURSE_ID);
            mSectionId = getIntent().getStringExtra(SECTION_ID);
            mCourseType = getIntent().getStringExtra(COURSE_TYPE);
            mProgress = getIntent().getIntExtra(PROGRESS, 0);
            String title = getIntent().getStringExtra(SECTION_TITLE);
            if (!TextUtils.isEmpty(mCourseId)) {
                String color = getIntent().getStringExtra(COLOR);
                setUpToolbar(color, title);
                getPreviouslyCompletedItems(mCourseId);
                mCourseSectionCards = (ArrayList<CourseSectionCard>) getIntent().getSerializableExtra(CARDS);
                initializeCardStack(mCourseSectionCards);
                mTotal = mCourseSectionCards.size();
                moveToPosition(mProgress);
                setup(mTotal);
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
        mDisposable = mRxBus.toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                if (event instanceof QuizCompletedEvent) {
                    mBinding.cardStackView.getTopView().setDraggable(true);
                    setCompletedItems(((QuizCompletedEvent) event).getQuizId());

                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    /*
        private void saveProgress(boolean createNotification) {

            CourseProgress updateProgress = mPlayerModel.getCourseProgress(mCourseId);
            if (updateProgress != null && !TextUtils.isEmpty(updateProgress.getObjectId())) {
                ArrayList<SectionProgress> list = updateProgress.getSectionProgresses();
                SectionProgress sectionProgress = new SectionProgress();
                sectionProgress.setProgress(mBinding.cardStackView.getTopIndex());
                sectionProgress.setObjectId(mSectionId);
                sectionProgress.setCompletedItems(mCompletedItems);
                sectionProgress.setTotalCards(mTotal);
                sectionProgress.setAlias(null);
                int index = list.indexOf(sectionProgress);
                if (index > -1) {
                    list.set(index, sectionProgress);
                } else {
                    sectionProgress.setSectionItems(getSectionItems(mCourseSectionCards));
                    list.add(sectionProgress);
                }
                updateProgress.setSectionProgresses(list);
                updateProgress.setType(getString(R.string.typeCourse));
                updateProgress.setTypeName(getString(R.string.typeNameFeatureCourse));
                updateProgress.setComplete(getIsComplete());
                updateProgress.setUserId(mAppUserModel.getObjectId());
                updateProgress.setAlias(null);
                mPlayerModel.saveCourseProgress(updateProgress, createNotification);
            } else {
                CourseProgress courseProgress = new CourseProgress();
                courseProgress.setObjectId(mCourseId);
                ArrayList<SectionProgress> list = new ArrayList<>();
                SectionProgress sectionProgress = new SectionProgress();
                sectionProgress.setProgress(mBinding.cardStackView.getTopIndex());
                sectionProgress.setObjectId(mSectionId);
                sectionProgress.setCompletedItems(mCompletedItems);
                sectionProgress.setTotalCards(mTotal);
                sectionProgress.setAlias(null);
                sectionProgress.setSectionItems(getSectionItems(mCourseSectionCards));
                list.add(sectionProgress);
                courseProgress.setSectionProgresses(list);
                courseProgress.setType(getString(R.string.typeCourse));
                courseProgress.setTypeName(getString(R.string.typeNameFeatureCourse));
                courseProgress.setComplete(getIsComplete());
                courseProgress.setUserId(mAppUserModel.getObjectId());
                courseProgress.setAlias(null);
                mPlayerModel.saveCourseProgress(courseProgress, createNotification);
            }

        }
    */
    private void getPreviouslyCompletedItems(String courseId) {
        CourseProgress courseProgress = mPlayerModel.getCourseProgress(courseId);
        if (courseProgress != null && !TextUtils.isEmpty(courseProgress.getObjectId())) {
            ArrayList<SectionProgress> sectionProgresses = courseProgress.getSectionProgresses();
            for (SectionProgress sectionProgress : sectionProgresses) {
                if (!TextUtils.isEmpty(sectionProgress.getObjectId()) && sectionProgress.getObjectId().equals(mSectionId)) {
                    mCompletedItems.addAll(sectionProgress.getCompletedItems());
                }
            }
        }
    }

    private boolean getIsComplete() {
        return (mBinding.cardStackView.getTopIndex()) == mTotal;
    }

    private void setCompletedItems(String id) {
        if (!mCompletedItems.contains(id)) {
            mCompletedItems.add(id);
        }
    }

    private ArrayList<SectionItems> getSectionItems(List<CourseSectionCard> cards) {
        ArrayList<SectionItems> sectionItems = new ArrayList<>();
        for (CourseSectionCard courseSectionCard : cards) {
            SectionItems item = new SectionItems();
            item.setId(courseSectionCard.getObjectId());
            item.setType(courseSectionCard.getMedia().getType());
            item.setTypeName(courseSectionCard.getMedia().getMicroCourseType());
            sectionItems.add(item);
        }

        return sectionItems;
    }

    private void initializeCardStack(List<CourseSectionCard> cards) {
        if (cards != null && !cards.isEmpty()) {
            SectionCardStackAdapter adapter = new SectionCardStackAdapter(getBaseContext(), cards);
            mBinding.cardStackView.setAdapter(adapter);
        }

    }

    private void reverse(int index) {
        if (index == 0) {
            finish();
        } else {
            mBinding.cardStackView.reverse();
        }
    }

    private void setUpToolbar(String color, String title) {
        if (!TextUtils.isEmpty(title)) {
            mBinding.textViewTitle.setText(title);
        }

        if (!TextUtils.isEmpty(color)) {
            getWindow().setStatusBarColor(Color.parseColor(color));
            mBinding.layoutMain.setBackgroundColor(Color.parseColor(color));
        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        }

        mBinding.closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void moveToPosition(int position) {
        if (position > -1 && position < mTotal) {
            int currentPosition = mBinding.cardStackView.getTopIndex();
            SwipeDirection direction = null;
            if (position < currentPosition) {
                direction = SwipeDirection.Left;
                for (int i = currentPosition; i > position; i--) {
                    mBinding.cardStackView.reverse(new Point(0, 0), direction);
                }
            } else {
                direction = SwipeDirection.Right;
                for (int i = currentPosition; i < position; i++) {
                    mBinding.cardStackView.swipe(direction, new AnimatorSet());
                }
            }
        }
    }

    private void setup(final int size) {
        mStartTime = DateUtils.getCurrentISO8601DateString();
        mBinding.progressBar.setMax(size);
        mBinding.progressBar.setProgress(mProgress + 1);
        String value = (mProgress + 1) + "/" + size;
        mBinding.textViewProgressCount.setText(value);
        mBinding.cardStackView.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {
                Log.d("CardStackView", "onCardDragging");

            }

            @Override
            public void onCardSwiped(SwipeDirection direction) {
                Log.d("CardStackView", "onCardSwiped: " + direction.toString());
                Log.d("CardStackView", "topIndex: " + mBinding.cardStackView.getTopIndex());
                if ((mBinding.cardStackView.getTopIndex()) == size) {
                    generateUserCourseProgress(mCourseSectionCards.get(mBinding.cardStackView.getTopIndex() - 1).getObjectId(), mStartTime);
                    finish();

                } else {
                    generateUserCourseProgress(mCourseSectionCards.get(mBinding.cardStackView.getTopIndex() - 1).getObjectId(), mStartTime);
                    mStartTime = DateUtils.getCurrentISO8601DateString();
                    mBinding.progressBar.setProgress(mBinding.cardStackView.getTopIndex() + 1);
                    String value = (mBinding.cardStackView.getTopIndex() + 1) + "/" + size;
                    mBinding.textViewProgressCount.setText(value);
                    //saveProgress(false);
                }

            }

            @Override
            public void onCardReversed() {
                Log.d("CardStackView", "onCardReversed");
                if ((mBinding.cardStackView.getTopIndex()) == size) {
                    generateUserCourseProgress(mCourseSectionCards.get(mBinding.cardStackView.getTopIndex() - 1).getObjectId(), mStartTime);
                    finish();
                } else {
                    mBinding.progressBar.setProgress(mBinding.cardStackView.getTopIndex() + 1);
                    String text = (mBinding.cardStackView.getTopIndex() + 1) + "/" + size;
                    mBinding.textViewProgressCount.setText(text);
                    generateUserCourseProgress(mCourseSectionCards.get(mBinding.cardStackView.getTopIndex()).getObjectId(), mStartTime);
                    //saveProgress(false);
                }
            }

            @Override
            public void onCardMovedToOrigin() {
                Log.d("CardStackView", "onCardMovedToOrigin");
            }

            @Override
            public void onCardClicked(int index) {
                Log.d("CardStackView", "onCardClicked: " + index);
            }

        });
    }

    private void generateUserCourseProgress(String cardId, String startTime) {
        mPlayerModel.generateUserCourseProgress(mCourseId, mCourseType, false, startTime, DateUtils.getCurrentISO8601DateString(), "sections", mSectionId, "card", cardId);
    }

    public class SectionCardStackAdapter extends CardStackAdapter<CourseSectionCard> {
        private List<CourseSectionCard> mList;
        private int mCaptionSizeExtra = 0;

        SectionCardStackAdapter(Context context, List<CourseSectionCard> list) {
            super(context, 0);
            mList = list;
        }

        @NonNull
        @Override
        public View getView(int position, View contentView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (contentView == null) {
                LayoutSectionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_section_item, parent, false);
                contentView = binding.getRoot();
                holder = new ViewHolder(binding);
                contentView.setTag(holder);
            } else {
                holder = (ViewHolder) contentView.getTag();
            }

            final CourseSectionCard object = mList.get(position);
            final CourseCardMedia media = object.getMedia();
            final CourseCardMedia mediaSecondary = object.getMediaSecondary();

            String templateId = object.getTemplateId();

            setCardTitle(object.getTitle(), holder.mBinding.textViewTitle);

            switch (templateId) {
                case "T1":
                    holder.mBinding.layoutMedia.setVisibility(View.VISIBLE);
                    holder.mBinding.layoutMediaPrimary.setVisibility(View.GONE);
                    holder.mBinding.layoutMediaSecondary.setVisibility(View.GONE);

                    setCaption(object.getCaption(), holder.mBinding.textViewContent);
                    setMediaThumbnailPrimary(media, holder.mBinding.imageViewMedia);

                    if (setFloatingButtonIconPrimary(media, holder.mBinding.buttonPlay)) {
                        mCaptionSizeExtra -= 1;
                    }

                    holder.mBinding.buttonPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (media.getType().equalsIgnoreCase(getString(R.string.typePdf))) {
                                playPdf(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeImage))) {
                                playImage(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVideo))) {
                                playVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))) {
                                playYouTubeVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVimeoVideo))) {
                                playVimeoVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeMicroCourse))) {
                                if (!TextUtils.isEmpty(media.getMicroCourseType()) && media.getMicroCourseType().equalsIgnoreCase(getString(R.string.typeQuiz))) {
                                    playQuiz(media, object.getObjectId());
                                } else {
                                    playMicroCourse(media);
                                }
                            }
                        }
                    });
                    break;
                case "T2":
                    holder.mBinding.layoutMediaSecondary.setVisibility(View.GONE);
                    holder.mBinding.layoutMedia.setVisibility(View.GONE);
                    holder.mBinding.layoutMediaPrimary.setVisibility(View.VISIBLE);

                    setCaption(object.getCaption(), holder.mBinding.textViewContent);
                    setMediaThumbnailPrimary(media, holder.mBinding.imageViewMediaPrimary);

                    if (setFloatingButtonIconPrimary(media, holder.mBinding.buttonPlayPrimary)) {
                        mCaptionSizeExtra -= 1;
                    }

                    holder.mBinding.buttonPlayPrimary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (media.getType().equalsIgnoreCase(getString(R.string.typePdf))) {
                                playPdf(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeImage))) {
                                playImage(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVideo))) {
                                playVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))) {
                                playYouTubeVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVimeoVideo))) {
                                playVimeoVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeMicroCourse))) {
                                if (!TextUtils.isEmpty(media.getMicroCourseType()) && media.getMicroCourseType().equalsIgnoreCase(getString(R.string.typeQuiz))) {
                                    playQuiz(media, object.getObjectId());
                                } else {
                                    playMicroCourse(media);
                                }
                            }
                        }
                    });
                    break;
                case "T3":
                    holder.mBinding.layoutMedia.setVisibility(View.GONE);
                    holder.mBinding.layoutMediaPrimary.setVisibility(View.GONE);
                    holder.mBinding.layoutMediaSecondary.setVisibility(View.GONE);
                    setCaption(object.getCaption(), holder.mBinding.textViewContent);
                    break;
                case "T4":
                    holder.mBinding.textViewContent.setVisibility(View.GONE);
                    holder.mBinding.layoutMedia.setVisibility(View.GONE);
                    holder.mBinding.layoutMediaPrimary.setVisibility(View.VISIBLE);
                    holder.mBinding.layoutMediaSecondary.setVisibility(View.VISIBLE);
                    setMediaThumbnailPrimary(media, holder.mBinding.imageViewMediaPrimary);

                    if (setFloatingButtonIconPrimary(media, holder.mBinding.buttonPlayPrimary)) {
                        mCaptionSizeExtra -= 1;
                    }

                    holder.mBinding.buttonPlayPrimary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (media.getType().equalsIgnoreCase(getString(R.string.typePdf))) {
                                playPdf(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeImage))) {
                                playImage(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVideo))) {
                                playVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))) {
                                playYouTubeVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVimeoVideo))) {
                                playVimeoVideo(media);
                            } else if (media.getType().equalsIgnoreCase(getString(R.string.typeMicroCourse))) {
                                if (!TextUtils.isEmpty(media.getMicroCourseType()) && media.getMicroCourseType().equalsIgnoreCase(getString(R.string.typeQuiz))) {
                                    playQuiz(media, object.getObjectId());
                                } else {
                                    playMicroCourse(media);
                                }
                            }
                        }
                    });

                    if (mediaSecondary != null) {
                        setMediaThumbnailSecondary(mediaSecondary, holder.mBinding.imageViewMediaSecondary);

                        if (setFloatingButtonIconSecondary(mediaSecondary, holder.mBinding.buttonMediaPlaySecondary)) {
                            mCaptionSizeExtra -= 1;
                        }

                        holder.mBinding.buttonMediaPlaySecondary.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mediaSecondary.getType().equalsIgnoreCase(getString(R.string.typePdf))) {
                                    playPdf(mediaSecondary);
                                } else if (mediaSecondary.getType().equalsIgnoreCase(getString(R.string.typeImage))) {
                                    playImage(mediaSecondary);
                                } else if (mediaSecondary.getType().equalsIgnoreCase(getString(R.string.typeVideo))) {
                                    playVideo(mediaSecondary);
                                } else if (mediaSecondary.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))) {
                                    playYouTubeVideo(mediaSecondary);
                                } else if (mediaSecondary.getType().equalsIgnoreCase(getString(R.string.typeVimeoVideo))) {
                                    playVimeoVideo(mediaSecondary);
                                } else if (mediaSecondary.getType().equalsIgnoreCase(getString(R.string.typeMicroCourse))) {
                                    if (!TextUtils.isEmpty(mediaSecondary.getMicroCourseType()) && mediaSecondary.getMicroCourseType().equalsIgnoreCase(getString(R.string.typeQuiz))) {
                                        playQuiz(mediaSecondary, object.getObjectId());
                                    } else {
                                        playMicroCourse(mediaSecondary);
                                    }
                                }
                            }
                        });
                    }

                    break;
            }


            return contentView;
        }

        private void setCaption(String caption, AppCompatTextView textView) {
            if (!TextUtils.isEmpty(caption)) {
                textView.setVisibility(View.VISIBLE);
                if (caption.contains("<li>")) {
                    String temp = caption
                            .replaceAll("<li>", "&#9679;\n")
                            .replaceAll("</li>\n", "<br/><br/>");
                    textView.setText(Html.fromHtml(temp));
                } else
                    textView.setText(Html.fromHtml(caption));
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void setCardTitle(String title, AppCompatTextView textView) {
            if (!TextUtils.isEmpty(title)) {
                if (title.length() > 50) {
                    mCaptionSizeExtra--;
                } else {
                    title.length();
                }
                textView.setText(Html.fromHtml(title));
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        @Override
        public String getNoDragMessage(int position) {
            return getString(R.string.messageNoDrag);
        }

        @Override
        public boolean isDraggable(int position) {
            if (mList != null && !mList.isEmpty() && position < mList.size()) {
                CourseCardMedia media = mList.get(position).getMedia();
                CourseSectionCard card = mList.get(position);
                if (media != null && !TextUtils.isEmpty(media.getObjectId())
                        && card.isMandatory()
                        && (mCompletedItems == null || !mCompletedItems.contains(media.getObjectId()))) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean isReverseAllowed(int position) {
            return position > 0;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        private void playPdf(CourseCardMedia media) {
            try {
                String url = media.getUrlMain();
                if (!TextUtils.isEmpty(url)) {
                    url = url.trim();
                    if (url.endsWith(".pdf")) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                        Intent chooser = Intent.createChooser(intent, "Choose");
                        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        RapidLearningCardsActivity.this.startActivity(chooser);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setMediaThumbnailPrimary(CourseCardMedia media, AppCompatImageView imageView) {
            if (media != null) {
                if (!TextUtils.isEmpty(media.getThumbXL())) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getThumbXL()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else if (!TextUtils.isEmpty(media.getUrlThumbnail())) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getUrlThumbnail()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else if (!TextUtils.isEmpty(media.getUrlMain())) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getUrlMain()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else if (!TextUtils.isEmpty(media.getThumb())) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getThumb()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        private void setMediaThumbnailSecondary(CourseCardMedia media, AppCompatImageView imageView) {
            if (media != null) {
                if (!TextUtils.isEmpty(media.getThumbXL())) {

                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getThumbXL()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else if (!TextUtils.isEmpty(media.getUrlThumbnail())) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getUrlThumbnail()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else if (!TextUtils.isEmpty(media.getUrlMain())) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getUrlMain()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else if (!TextUtils.isEmpty(media.getThumb())) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(media.getThumb()).placeholder(R.drawable.image_placeholder)
                            .into(imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        private boolean setFloatingButtonIconPrimary(CourseCardMedia media, FloatingActionButton buttonPlay) {
            if (media != null) {
                if (media.getType().equalsIgnoreCase(getString(R.string.typeImage)) || media.getType().equalsIgnoreCase(getString(R.string.typePdf))) {
                    buttonPlay.setImageResource(R.drawable.search_white);
                    return false;
                } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVideo))
                        || media.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))
                        || media.getType().equalsIgnoreCase(getString(R.string.typeVimeoVideo))) {
                    buttonPlay.setImageResource(R.drawable.action_video_w);
                    return false;
                } else if (media.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))) {
                    buttonPlay.setImageResource(R.drawable.action_video_w);
                    return true;
                } else if (media.getType().equalsIgnoreCase(getString(R.string.typeMicroCourse))) {
                    buttonPlay.setImageResource(R.drawable.chevron_right);
                    return false;
                } else {
                    buttonPlay.setVisibility(View.GONE);
                    return false;
                }
            }
            buttonPlay.setVisibility(View.GONE);
            return false;

        }

        private boolean setFloatingButtonIconSecondary(CourseCardMedia media, FloatingActionButton buttonPlay) {
            if (media != null) {
                if (media.getType().equalsIgnoreCase(getString(R.string.typeImage)) || media.getType().equalsIgnoreCase(getString(R.string.typePdf))) {
                    buttonPlay.setImageResource(R.drawable.search_white);
                    return false;
                } else if (media.getType().equalsIgnoreCase(getString(R.string.typeVideo))
                        || media.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))
                        || media.getType().equalsIgnoreCase(getString(R.string.typeVimeoVideo))) {
                    buttonPlay.setImageResource(R.drawable.action_video_w);
                    return false;
                } else if (media.getType().equalsIgnoreCase(getString(R.string.typeYouTubeVideo))) {
                    buttonPlay.setImageResource(R.drawable.action_video_w);
                    return true;
                } else if (media.getType().equalsIgnoreCase(getString(R.string.typeMicroCourse))) {
                    buttonPlay.setImageResource(R.drawable.chevron_right);
                    return false;
                } else {
                    buttonPlay.setVisibility(View.GONE);
                    return false;
                }
            }
            buttonPlay.setVisibility(View.GONE);
            return false;

        }

        private void playMicroCourse(CourseCardMedia media) {
            String type = media.getMicroCourseType().toLowerCase();
            Class objectClass = null;
            if (type.equalsIgnoreCase("digitalbook")) {
                objectClass = DigitalBook.class;
            } else if (type.equalsIgnoreCase("videocourse")) {
                objectClass = VideoCourse.class;
            } else if (type.contains("conceptmap") || type.contains("map")) {
                objectClass = ConceptMap.class;
            } else if (type.contains("interactiveimage")) {
                objectClass = InteractiveImage.class;
            } else if (type.contains("video")) {
                objectClass = InteractiveVideo.class;
            } else if (type.contains("popup")) {
                objectClass = PopUps.class;
            }

            final Class finalObjectClass = objectClass;
            if (GeneralUtils.isNetworkAvailable(getContext())) {
                //always need to send subject and topic id empty.
                if (finalObjectClass.equals(VideoCourse.class) || finalObjectClass.equals(InteractiveVideo.class)) {
                    WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), media.getObjectId(), "", "", finalObjectClass, "", false);
                } else {
                    WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), media.getObjectId(), "", "", finalObjectClass, "", false);
                }
                setCompletedItems(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);

            }
        }

        private void playQuiz(CourseCardMedia media, String cardId) {
            String type = media.getMicroCourseType().toLowerCase();
            if (type.equalsIgnoreCase(getString(R.string.quiz).toLowerCase())) {

                if (GeneralUtils.isNetworkAvailable(getContext())) {
                    startActivity(QuizPlayerActivity.getStartIntentFromRapidLearning(getBaseContext(), media.getObjectId(),
                            mCourseId, mCourseType, cardId, mSectionId));
                } else {
                    ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }


        private void playVimeoVideo(CourseCardMedia media) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                Gson gson = new GsonBuilder().create();

                /*Adding sourceURL to url to play vimeo video in web player.*/
                if (!TextUtils.isEmpty(media.getSourceURL())) {
                    media.setUrl(media.getSourceURL());
                } else {
                    media.setUrl(media.getUrlMain());
                }

                String json = gson.toJson(media);
                WebPlayerCordovaLiveActivity.startWebPlayerForResourcePreview(getBaseContext(), mAppUserModel.getObjectId(), json);
                setCompletedItems(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);
            }
        }

        private void playYouTubeVideo(CourseCardMedia media) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                Gson gson = new GsonBuilder().create();

                /*Adding videoId to objectId for web player*/
                if (!TextUtils.isEmpty(media.getUrlMain())) {
                    media.setObjectId(media.getUrlMain());
                }

                String json = gson.toJson(media);
                WebPlayerCordovaLiveActivity.startWebPlayerForResourcePreview(getBaseContext(), mAppUserModel.getObjectId(), json);
                setCompletedItems(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);
            }
        }

        private void playVideo(CourseCardMedia media) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                Resource item = new Resource();
                item.setType(getString(R.string.typeVideo));
                item.setUrlMain(media.getUrlMain());
                startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(getContext(), PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
                setCompletedItems(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);
            }
        }

        private void playImage(CourseCardMedia media) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                startActivity(PlayFullScreenImageActivity.getStartIntent(getBaseContext(), media.getUrlMain(), true));
                mCompletedItems.add(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);
            }
        }

        private class ViewHolder {
            LayoutSectionItemBinding mBinding;

            ViewHolder(LayoutSectionItemBinding binding) {
                mBinding = binding;
            }
        }

    }

}
