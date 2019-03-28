package in.securelearning.lil.android.player.microlearning.view.activity;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;
import com.yuyakaido.android.cardstackview.internal.CardStackAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.TextViewMore;
import in.securelearning.lil.android.app.databinding.LayoutSectionItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutSectionViewBinding;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.CourseCardMedia;
import in.securelearning.lil.android.base.dataobjects.CourseProgress;
import in.securelearning.lil.android.base.dataobjects.CourseSectionCard;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.SectionItems;
import in.securelearning.lil.android.base.dataobjects.SectionProgress;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.events.QuizCompletedEvent;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.base.views.activity.WebPlayerLiveActivity;
import in.securelearning.lil.android.base.widget.CustomImageButton;
import in.securelearning.lil.android.home.views.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.home.views.activity.UserProfileActivity;
import in.securelearning.lil.android.player.microlearning.InjectorPlayer;
import in.securelearning.lil.android.player.microlearning.events.SpeakCompletedEvent;
import in.securelearning.lil.android.player.microlearning.model.PlayerModel;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity.mTextToSpeechUtils;


public class RapidLearningCardsActivity extends AppCompatActivity {
    @Inject
    PlayerModel mPlayerModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    RxBus mRxBus;
    LayoutSectionViewBinding mBinding;
    public static final String COURSE_ID = "id";
    public static final String SECTION_TITLE = "sectionTitle";
    public static final String SECTION_ID = "sectionId";
    public static final String COLOR = "color";
    public static final String CARDS = "cards";
    public static final String PROGRESS = "progress";
    private String mCourseId, mSectionId;
    private int mProgress, mTotal = -1;
    private ArrayList<String> mCompletedItems = new ArrayList<>();
    private ArrayList<CourseSectionCard> mCourseSectionCards = new ArrayList<>();
    private SectionCardStackAdapter mAdapter;
    private Disposable mDisposable;
    private CourseProgress mCourseProgress;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTTS();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProgress(true);
        stopTTS();

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
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public static Intent getStartIntent(Context context, String id, String title, String sectionId, String color, ArrayList<CourseSectionCard> cards, int progress) {
        Intent intent = new Intent(context, RapidLearningCardsActivity.class);
        intent.putExtra(COURSE_ID, id);
        intent.putExtra(SECTION_TITLE, title);
        intent.putExtra(SECTION_ID, sectionId);
        intent.putExtra(COLOR, color);
        intent.putExtra(CARDS, cards);
        intent.putExtra(PROGRESS, progress);
        return intent;
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mCourseId = getIntent().getStringExtra(COURSE_ID);
            mSectionId = getIntent().getStringExtra(SECTION_ID);
            mProgress = getIntent().getIntExtra(PROGRESS, 0);
            String title = getIntent().getStringExtra(SECTION_TITLE);
            if (!TextUtils.isEmpty(mCourseId)) {
                String color = getIntent().getStringExtra(COLOR);
                setUpToolbar(color, title);
                getPreviouslyCompletedItems(mCourseId);
                mCourseSectionCards = (ArrayList<CourseSectionCard>) getIntent().getSerializableExtra(CARDS);
//                List<CourseSectionCard> subList = list.subList(mProgress, list.size());
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
            CourseProgress newProgress = new CourseProgress();
            newProgress.setObjectId(mCourseId);
            ArrayList<SectionProgress> list = new ArrayList<>();
            SectionProgress sectionProgress = new SectionProgress();
            sectionProgress.setProgress(mBinding.cardStackView.getTopIndex());
            sectionProgress.setObjectId(mSectionId);
            sectionProgress.setCompletedItems(mCompletedItems);
            sectionProgress.setTotalCards(mTotal);
            sectionProgress.setAlias(null);
            sectionProgress.setSectionItems(getSectionItems(mCourseSectionCards));
            list.add(sectionProgress);
            newProgress.setSectionProgresses(list);
            newProgress.setType(getString(R.string.typeCourse));
            newProgress.setTypeName(getString(R.string.typeNameFeatureCourse));
            newProgress.setComplete(getIsComplete());
            newProgress.setUserId(mAppUserModel.getObjectId());
            newProgress.setAlias(null);
            mPlayerModel.saveCourseProgress(newProgress, createNotification);
        }

    }

    private void getPreviouslyCompletedItems(String courseId) {
        mCourseProgress = mPlayerModel.getCourseProgress(courseId);
        if (mCourseProgress != null && !TextUtils.isEmpty(mCourseProgress.getObjectId())) {
            ArrayList<SectionProgress> sectionProgresses = mCourseProgress.getSectionProgresses();
            for (SectionProgress sectionProgress : sectionProgresses) {
                if (!TextUtils.isEmpty(sectionProgress.getObjectId()) && sectionProgress.getObjectId().equals(mSectionId)) {
                    mCompletedItems.addAll(sectionProgress.getCompletedItems());
                }
            }
        }
    }

    private boolean getIsComplete() {
        if ((mBinding.cardStackView.getTopIndex()) == mTotal) {
            return true;
        } else {
            return false;
        }
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
            mAdapter = new SectionCardStackAdapter(getBaseContext(), cards);
            mBinding.cardStackView.setAdapter(mAdapter);
        }

    }

    private void reverse(int index) {
        if (index == 0) {
            finish();
        } else {
            mBinding.cardStackView.reverse();
        }
        //  mBinding.progressBar.setProgress(index);
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
        mBinding.progressBar.setMax(size);
        mBinding.progressBar.setProgress(mProgress + 1);
        mBinding.textViewProgressCount.setText(String.valueOf(mProgress + 1) + "/" + String.valueOf(size));
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
                    finish();
                } else {
                    mBinding.progressBar.setProgress(mBinding.cardStackView.getTopIndex() + 1);
                    mBinding.textViewProgressCount.setText(String.valueOf(mBinding.cardStackView.getTopIndex() + 1) + "/" + String.valueOf(size));
                    saveProgress(false);
                }

                stopTTS();

            }

            @Override
            public void onCardReversed() {
                Log.d("CardStackView", "onCardReversed");
                if ((mBinding.cardStackView.getTopIndex()) == size) {
                    finish();
                } else {
                    mBinding.progressBar.setProgress(mBinding.cardStackView.getTopIndex() + 1);
                    mBinding.textViewProgressCount.setText(String.valueOf(mBinding.cardStackView.getTopIndex() + 1) + "/" + String.valueOf(size));
                    saveProgress(false);
                }
                stopTTS();
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

    private void stopTTS() {
        if (mTextToSpeechUtils != null) {
            mTextToSpeechUtils.stop();
            mRxBus.send(new SpeakCompletedEvent(true));

        }
    }

    public class SectionCardStackAdapter extends CardStackAdapter<CourseSectionCard> {
        private List<CourseSectionCard> mList;

        public SectionCardStackAdapter(Context context, List<CourseSectionCard> list) {
            super(context, 0);
            mList = list;

        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            final ViewHolder holder;
            final CourseSectionCard object = mList.get(position);
            final CourseCardMedia media = object.getMedia();
            if (contentView == null) {
                LayoutSectionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_section_item, parent, false);
                contentView = binding.getRoot();
                holder = new ViewHolder(binding);
                contentView.setTag(holder);
            } else {
                holder = (ViewHolder) contentView.getTag();
            }

            setMediaThumbnail(media, holder.mBinding.imageViewThumbnail);

            int captionSizeExtra = 0;
            String title = object.getTitle();
            if (!TextUtils.isEmpty(title)) {
                if (title.length() > 50) {
                    captionSizeExtra--;
                } else if (title.length() > 80) {
                    captionSizeExtra -= 2;
                }
                holder.mBinding.textViewTitle.setText(Html.fromHtml(title));
            } else {
                holder.mBinding.textViewTitle.setVisibility(View.GONE);
            }

            if (setFloatingButtonIcon(media, holder.mBinding.buttonPlay)) {
                captionSizeExtra -= 1;
            }

            final String caption = object.getCaption();
            setCaption(caption, captionSizeExtra, holder.mBinding.textViewContent);

            holder.mBinding.buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopTTS();

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
                            playAssessment(media);
                        } else {
                            playMicroCourse(media);
                        }
                    }
                }
            });

            holder.mBinding.buttonTextToSpeech.setTag(getString(R.string.play));
            holder.mBinding.buttonTextToSpeech.setImageResource(R.drawable.action_speaker_g);
            holder.mBinding.buttonTextToSpeech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mBinding.buttonTextToSpeech.getTag().equals(getString(R.string.play))) {
                        mTextToSpeechUtils.allow(true);
                        mTextToSpeechUtils.speakParagraph(Html.fromHtml(caption).toString());
                        mTextToSpeechUtils.setDuration(3000);
                        holder.mBinding.buttonTextToSpeech.setTag(getString(R.string.stop));
                        holder.mBinding.buttonTextToSpeech.setImageResource(R.drawable.action_stop_g);
                    } else if (holder.mBinding.buttonTextToSpeech.getTag().equals(getString(R.string.stop))) {
                        mTextToSpeechUtils.allow(false);
                        mTextToSpeechUtils.stop();
                        holder.mBinding.buttonTextToSpeech.setTag(getString(R.string.play));
                        holder.mBinding.buttonTextToSpeech.setImageResource(R.drawable.action_speaker_g);
                    }
                }
            });

            listenTextToSpeechEvents(holder.mBinding.buttonTextToSpeech);
            return contentView;
        }

        private void setCaption(String caption, int captionSizeExtra, AppCompatTextView textViewContent) {
            if (!TextUtils.isEmpty(caption)) {
                if (caption.length() > 280) {
                    textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14 + captionSizeExtra);
                } else if (caption.length() > 250) {
                    textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15 + captionSizeExtra);
                } else if (caption.length() > 200) {
                    textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16 + captionSizeExtra);
                } else if (caption.length() > 150) {
                    textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17 + captionSizeExtra);
                } else if (caption.length() > 100) {
                    textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 + captionSizeExtra);
                } else if (caption.length() > 70) {
                    textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22 + captionSizeExtra);
                } else {
                    textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26 + captionSizeExtra);
                }
                textViewContent.setText(Html.fromHtml(caption, null, new TextViewMore.UlTagHandler()));

            } else {
                textViewContent.setVisibility(View.GONE);
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

        private void setMediaThumbnail(CourseCardMedia media, AppCompatImageView imageView) {
            if (media != null) {
                if (!TextUtils.isEmpty(media.getUrlThumbnail())) {
                    Picasso.with(getContext()).load(media.getUrlThumbnail()).into(imageView);
                } else if (!TextUtils.isEmpty(media.getUrlMain())) {
                    Picasso.with(getContext()).load(media.getUrlMain()).into(imageView);
                } else if (!TextUtils.isEmpty(media.getThumbXL())) {
                    Picasso.with(getContext()).load(media.getThumbXL()).into(imageView);
                } else if (!TextUtils.isEmpty(media.getThumb())) {
                    Picasso.with(getContext()).load(media.getThumb()).into(imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        private boolean setFloatingButtonIcon(CourseCardMedia media, FloatingActionButton buttonPlay) {
            if (media != null) {
                if (media.getType().equalsIgnoreCase(getString(R.string.typeImage))) {
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
                WebPlayerCordovaLiveActivity.startWebPlayer(getContext(), media.getObjectId(), "", "", finalObjectClass, "", false);

                setCompletedItems(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);

            }
        }

        private void playAssessment(CourseCardMedia media) {
            String type = media.getMicroCourseType().toLowerCase();
            if (type.equalsIgnoreCase("quiz")) {
                //startActivity(PracticePlayerActivity.getStartIntentForQuizOnline(RapidLearningCardsActivity.this, media.getObjectId()));

                if (GeneralUtils.isNetworkAvailable(getContext())) {
                    WebPlayerLiveActivity.startWebPlayer(getContext(), media.getObjectId(), "", "", Quiz.class, "", false,false);
                } else {
                    ToastUtils.showToastAlert(getContext(), getString(R.string.connect_internet));
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }

        private void playVimeoVideo(CourseCardMedia media) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                if (!TextUtils.isEmpty(media.getSourceURL())) {
                    startActivity(PlayVimeoFullScreenActivity.getStartIntent(getContext(), media.getSourceURL()));
                } else {
                    startActivity(PlayVimeoFullScreenActivity.getStartIntent(getContext(), media.getReference()));
                }
                setCompletedItems(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);
            }
        }

        private void playYouTubeVideo(CourseCardMedia media) {
            if (GeneralUtils.isNetworkAvailable(getBaseContext())) {
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(media.getName());
                favouriteResource.setUrlThumbnail(media.getUrlThumbnail());
                startActivity(PlayYouTubeFullScreenActivity.getStartIntent(getContext(), favouriteResource, false));
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
                UserProfileActivity.showFullImage(media.getUrlMain(), RapidLearningCardsActivity.this);
                mCompletedItems.add(media.getObjectId());
                mBinding.cardStackView.getTopView().setDraggable(true);
            } else {
                SnackBarUtils.showNoInternetSnackBar(getBaseContext(), mBinding.layoutMain);
            }
        }

        public void listenTextToSpeechEvents(final CustomImageButton buttonTextToSpeech) {
            mDisposable = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object event) throws Exception {
                    if (event instanceof SpeakCompletedEvent) {
                        boolean isSpeakCompleted = ((SpeakCompletedEvent) event).isSpeakCompleted();
                        if (isSpeakCompleted) {
                            mTextToSpeechUtils.allow(false);
                            buttonTextToSpeech.setTag(getString(R.string.play));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    buttonTextToSpeech.setImageResource(R.drawable.action_speaker_g);
                                }
                            });
                        }
                    }
//                    else if (event instanceof SpeakMandatoryInstruction) {
//                        if (mTextToSpeechUtils != null) {
//                            mTextToSpeechUtils.readMandatoryInstruction();
//                        }
//                    }

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });


        }

        private class ViewHolder {
            LayoutSectionItemBinding mBinding;

            public ViewHolder(LayoutSectionItemBinding binding) {
                mBinding = binding;
            }
        }

    }

}
