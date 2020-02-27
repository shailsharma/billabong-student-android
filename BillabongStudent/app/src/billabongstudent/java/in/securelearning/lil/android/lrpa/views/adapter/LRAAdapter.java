package in.securelearning.lil.android.lrpa.views.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutCourseListItemBinding;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.views.activity.WebPlayerCordovaLiveActivity;
import in.securelearning.lil.android.player.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.dataobjects.AboutCourseMinimal;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.thirdparty.views.activity.GeoGebraPlayerActivity;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/*This is a common adapter class created for Learn, Reinforce and Apply category
 * LRA here L=Learn, R=Reinforce, A=Apply*/
public class LRAAdapter extends RecyclerView.Adapter<LRAAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AboutCourseMinimal> mList;

    public LRAAdapter(Context context, ArrayList<AboutCourseMinimal> list) {
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
        final AboutCourseMinimal courseMinimal = mList.get(position);

        setThumbnail(courseMinimal.getThumbnail(), holder.mBinding.imageViewBackground);
        setRewardPoint(courseMinimal.getTotalMarks(), courseMinimal.getCoverage(), courseMinimal.getColorCode(), holder.mBinding.layoutReward, holder.mBinding.textViewRewardPoints, holder.mBinding.viewCourseStatus);

        holder.mBinding.textViewTitle.setText(courseMinimal.getTitle());
        setCourseType(holder.mBinding.textViewType, courseMinimal);

        final Class finalObjectClass = CommonUtils.getInstance().getCourseClass(courseMinimal);

        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GeneralUtils.isNetworkAvailable(mContext)) {

                    if (!TextUtils.isEmpty(courseMinimal.getTPId())) {
                        mContext.startActivity(GeoGebraPlayerActivity.getStartIntent(mContext, courseMinimal.getTPId(), courseMinimal.getTPDescription()));
                    } else if (!TextUtils.isEmpty(courseMinimal.getId())) {
                        if (finalObjectClass.equals(MicroLearningCourse.class)) {
                            mContext.startActivity(RapidLearningSectionListActivity.getStartIntent(mContext, courseMinimal.getId()));
                        } else if (!TextUtils.isEmpty(courseMinimal.getCourseType())
                                && courseMinimal.getCourseType().equals(mContext.getString(R.string.label_wikiHow))) {
                            WebPlayerCordovaLiveActivity.startWebPlayerForWikiHow(mContext, courseMinimal.getId());
                        } else {
                            WebPlayerCordovaLiveActivity.startWebPlayer(mContext, courseMinimal.getId(), ConstantUtil.BLANK, ConstantUtil.BLANK, finalObjectClass, ConstantUtil.BLANK, false);
                        }
                    } else {
                        GeneralUtils.showToastShort(mContext, mContext.getString(R.string.error_something_went_wrong));
                    }

                } else {
                    GeneralUtils.showToastShort(mContext, mContext.getString(R.string.error_message_no_internet));
                }

            }
        });
    }

    private void setCourseType(AppCompatTextView textView, AboutCourseMinimal course) {
        if (!TextUtils.isEmpty(course.getCourseType()) && course.getCourseType().equals(mContext.getString(R.string.label_wikiHow))) {
            textView.setText(course.getCourseType());
        } else if (!TextUtils.isEmpty(course.getCourseType()) && course.getCourseType().equals(mContext.getString(R.string.labelGeoGebra))) {
            textView.setText(course.getCourseType());
        } else {
            textView.setText(CommonUtils.getInstance().getCourseType(course));
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
        Drawable placeholder = ContextCompat.getDrawable(mContext, CommonUtils.getInstance().generateRandomGradient());

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
                    .load(R.drawable.image_placeholder_curved)
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