package in.securelearning.lil.android.home.views.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
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
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.model.FlavorHomeModel;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/*This is a common adapter class created for Learn, Reinforce and Apply category
 * LRA here L=Learn, R=Reinforce, A=Apply*/
public class LRAAdapter extends RecyclerView.Adapter<LRAAdapter.ViewHolder> {

    @Inject
    FlavorHomeModel mFlavorHomeModel;

    private Context mContext;
    private ArrayList<AboutCourseMinimal> mList;

    public LRAAdapter(Context context, ArrayList<AboutCourseMinimal> list) {
        InjectorHome.INSTANCE.getComponent().inject(this);
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public LRAAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutCourseListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_course_list_item, parent, false);
        return new LRAAdapter.ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final LRAAdapter.ViewHolder holder, int position) {
        final AboutCourseMinimal course = mList.get(position);

        setThumbnail(course.getThumbnail(), holder.mBinding.imageViewBackground);
        setRewardPoint(course.getTotalMarks(), course.getCoverage(), course.getColorCode(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints, holder.mBinding.viewCourseStatus);

        holder.mBinding.textViewTitle.setText(course.getTitle());
        setCourseType(holder.mBinding.textViewType, course);

        final Class finalObjectClass = mFlavorHomeModel.getCourseClass(course);

        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(course.getId())) {
                    if (finalObjectClass.equals(MicroLearningCourse.class)) {
                        mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, course.getId()));
                    } else if (!TextUtils.isEmpty(course.getCourseType())
                            && course.getCourseType().equals(mContext.getString(R.string.label_wikiHow))) {
                        WebPlayerCordovaLiveActivity.startWebPlayerForWikiHow(mContext, course.getId());
                    } else {
                        WebPlayerCordovaLiveActivity.startWebPlayer(mContext, course.getId(), ConstantUtil.BLANK, ConstantUtil.BLANK, finalObjectClass, ConstantUtil.BLANK, false);
                    }
                } else {
                    GeneralUtils.showToastShort(mContext, mContext.getString(R.string.error_something_went_wrong));
                }

            }
        });
    }

    private void setCourseType(AppCompatTextView textView, AboutCourseMinimal course) {
        if (!TextUtils.isEmpty(course.getCourseType()) && course.getCourseType().equals(mContext.getString(R.string.label_wikiHow))) {
            textView.setText(course.getCourseType());
        } else {
            textView.setText(mFlavorHomeModel.getCourseType(course));
        }
    }

    private void setRewardPoint(int totalMarks, float coverage, String colorCode, LinearLayoutCompat layoutReward, AppCompatTextView textViewRewardPoints, AppCompatTextView viewCourseStatus) {
        if (totalMarks > 0) {
            layoutReward.setVisibility(View.VISIBLE);
            textViewRewardPoints.setText(String.valueOf(totalMarks));
            viewCourseStatus.setVisibility(View.VISIBLE);

            if (coverage > 0) {
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(mContext, R.drawable.circle_solid_primary);
                assert unwrappedDrawable != null;
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(colorCode));

                viewCourseStatus.setBackgroundDrawable(unwrappedDrawable);
            } else {
                viewCourseStatus.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.circle_solid_primary_outlined));

            }
        } else {
            layoutReward.setVisibility(View.GONE);
        }
    }


    private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {

        RoundedCornersTransformation transformation = new RoundedCornersTransformation(ConstantUtil.LRPA_CARD_CORNER_RADIUS, ConstantUtil.LRPA_CARD_MARGIN, RoundedCornersTransformation.CornerType.ALL);
        int placeholder = R.drawable.image_placeholder_curved;

        if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumbXL())) {
            Picasso.with(mContext)
                    .load(thumbnail.getThumbXL())
                    .placeholder(placeholder)
                    .transform(transformation)
                    .fit().centerCrop()
                    .into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getUrl())) {
            Picasso.with(mContext)
                    .load(thumbnail.getUrl())
                    .placeholder(placeholder)
                    .transform(transformation)
                    .fit().centerCrop()
                    .into(imageView);
        } else if (thumbnail != null && !TextUtils.isEmpty(thumbnail.getThumb())) {
            Picasso.with(mContext)
                    .load(thumbnail.getThumb())
                    .placeholder(placeholder)
                    .transform(transformation)
                    .fit().centerCrop()
                    .into(imageView);
        } else {
            Picasso.with(mContext)
                    .load(placeholder)
                    .transform(transformation)
                    .fit().centerCrop()
                    .into(imageView);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LayoutCourseListItemBinding mBinding;

        ViewHolder(LayoutCourseListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}