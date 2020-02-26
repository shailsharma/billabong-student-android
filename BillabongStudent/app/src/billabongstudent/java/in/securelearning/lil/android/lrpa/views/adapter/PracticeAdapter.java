package in.securelearning.lil.android.lrpa.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCourseListItemBinding;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.gamification.dataobject.GamificationEvent;
import in.securelearning.lil.android.gamification.model.MascotModel;
import in.securelearning.lil.android.gamification.utils.GamificationPrefs;
import in.securelearning.lil.android.gamification.views.activity.MascotActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.helper.OnStartPracticeActivityListener;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.ViewHolder> {

    @Inject
    MascotModel mMascotModel;

    private Context mContext;
    private ArrayList<AboutCourseMinimal> mList;
    private String mSubjectName;
    private OnStartPracticeActivityListener mStartPracticeActivityListener;

    public PracticeAdapter(Context context, ArrayList<AboutCourseMinimal> list, String subjectName, OnStartPracticeActivityListener startPracticeActivityListener) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        this.mContext = context;
        this.mList = list;
        this.mSubjectName = subjectName;
        this.mStartPracticeActivityListener = startPracticeActivityListener;
    }

    @NonNull
    @Override
    public PracticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutCourseListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_course_list_item, parent, false);
        return new PracticeAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PracticeAdapter.ViewHolder holder, int position) {
        final AboutCourseMinimal course = mList.get(position);

        holder.mBinding.textViewTitle.setText(course.getTitle());

        setThumbnail(course.getCourseType(), course.getThumbnail(), holder.mBinding.imageViewBackground);
        setCourseType(course.getCourseType(), course.getColorCode(), holder.mBinding.textViewType);

        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    try {
                        if (!TextUtils.isEmpty(mSubjectName)
                                && !mSubjectName.contains("Eng")
                                && !TextUtils.isEmpty(course.getCourseType())
                                && !course.getCourseType().equals(mContext.getString(R.string.labelWorksheet).toLowerCase())) {
                            ArrayList<GamificationEvent> eventList = mMascotModel.getGamificationEvent();
                            if (eventList != null && !eventList.isEmpty()) {
                                if (eventList.size() > 4) {
                                    GamificationEvent gamificationPracticeEvent = eventList.get(4);
                                    if (gamificationPracticeEvent != null
                                            && gamificationPracticeEvent.getActivity().equalsIgnoreCase("LRPA")
                                            && gamificationPracticeEvent.getSubActivity().equalsIgnoreCase("practise")) {
                                        if (gamificationPracticeEvent.getEventOccurrenceDate() == null && !gamificationPracticeEvent.isGamingEventDone()) {
                                            GamificationPrefs.savePractiseObject(mContext, course);
                                            mContext.startActivity(MascotActivity.getStartIntent(mContext, gamificationPracticeEvent.getMessage(), gamificationPracticeEvent));
                                        } else if (CommonUtils.getInstance().checkEventOccurrence(gamificationPracticeEvent.getFrequency(), gamificationPracticeEvent.getFrequencyUnit(), gamificationPracticeEvent.getEventOccurrenceDate())) {
                                            GamificationPrefs.savePractiseObject(mContext, course);
                                            mContext.startActivity(MascotActivity.getStartIntent(mContext, gamificationPracticeEvent.getMessage(), gamificationPracticeEvent));
                                        } else {
                                            mStartPracticeActivityListener.OnStartPracticeActivity(course);
                                        }
                                    } else {
                                        mStartPracticeActivityListener.OnStartPracticeActivity(course);
                                    }
                                } else {
                                    mStartPracticeActivityListener.OnStartPracticeActivity(course);
                                }
                            } else {
                                mStartPracticeActivityListener.OnStartPracticeActivity(course);
                            }
                        } else {
                            mStartPracticeActivityListener.OnStartPracticeActivity(course);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    GeneralUtils.showToastShort(mContext, mContext.getString(R.string.connect_internet));
                }

            }
        });
    }

    private void setCourseType(String courseType, String mindSparkUnitValue, AppCompatTextView textView) {
        if (courseType.equals(mContext.getString(R.string.labelPractice).toLowerCase())) {
            textView.setText(mContext.getString(R.string.labelPractice));
        } else if (courseType.equals(mContext.getString(R.string.labelWorksheet).toLowerCase())) {
            textView.setText(mContext.getString(R.string.labelWorksheet));
        } else if (courseType.equals(mContext.getString(R.string.mindspark))) {
            textView.setText(mindSparkUnitValue);
        } else {
            textView.setText(ConstantUtil.BLANK);
        }

    }

    private void setThumbnail(String courseType, Thumbnail thumbnail, ImageView imageView) {

        RoundedCornersTransformation transformation = new RoundedCornersTransformation(ConstantUtil.LRPA_CARD_CORNER_RADIUS, ConstantUtil.LRPA_CARD_MARGIN, RoundedCornersTransformation.CornerType.ALL);
        Drawable placeholder = ContextCompat.getDrawable(mContext, CommonUtils.getInstance().generateRandomGradient());

        if (courseType.equals(mContext.getString(R.string.freadom))) {
            imageView.setColorFilter(R.color.colorPrimary);
            Picasso.with(mContext).load(R.drawable.logo_freadom).placeholder(placeholder).transform(transformation).into(imageView);
        } else if (courseType.equals(mContext.getString(R.string.lightsail))) {
            Picasso.with(mContext).load(R.drawable.logo_lightsail).placeholder(placeholder).transform(transformation).into(imageView);
        } else if (courseType.equals(mContext.getString(R.string.mindspark))) {
            Picasso.with(mContext).load(R.drawable.background_thumb_mind_spark).placeholder(placeholder).transform(transformation).fit().centerCrop().into(imageView);
        } else if (courseType.equals(mContext.getString(R.string.labelWorksheet).toLowerCase())) {
            if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
                Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(placeholder).transform(transformation).into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
                Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(placeholder).transform(transformation).into(imageView);
            } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
                Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(placeholder).transform(transformation).into(imageView);
            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder_curved).transform(transformation).into(imageView);
            }
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
            Picasso.with(mContext).load(thumbnail.getThumb()).placeholder(placeholder).transform(transformation).fit().centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
            Picasso.with(mContext).load(thumbnail.getThumbXL()).placeholder(placeholder).transform(transformation).fit().centerCrop().into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
            Picasso.with(mContext).load(thumbnail.getUrl()).placeholder(placeholder).transform(transformation).fit().centerCrop().into(imageView);
        } else {
            Picasso.with(mContext).load(R.drawable.image_placeholder_curved).transform(transformation).fit().centerCrop().into(imageView);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutCourseListItemBinding mBinding;

        ViewHolder(LayoutCourseListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
