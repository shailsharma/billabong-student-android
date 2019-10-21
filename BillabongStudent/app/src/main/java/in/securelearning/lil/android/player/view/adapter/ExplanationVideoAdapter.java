package in.securelearning.lil.android.player.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutExplanationKhanAcademyVideoItemBinding;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.home.views.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideo;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideoDetail;
import in.securelearning.lil.android.player.dataobject.KhanAcademyVideoSnippet;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public class ExplanationVideoAdapter extends RecyclerView.Adapter<ExplanationVideoAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<KhanAcademyVideo> mList;

    public ExplanationVideoAdapter(Context context, ArrayList<KhanAcademyVideo> khanAcademyVideos) {
        this.mContext = context;
        this.mList = khanAcademyVideos;
    }

    @NonNull
    @Override
    public ExplanationVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutExplanationKhanAcademyVideoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_explanation_khan_academy_video_item, parent, false);
        return new ExplanationVideoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExplanationVideoAdapter.ViewHolder holder, int position) {
        KhanAcademyVideo khanAcademyVideo = mList.get(position);

        if (khanAcademyVideo != null && khanAcademyVideo.getVideoDetails() != null) {

            holder.mBinding.getRoot().setVisibility(View.VISIBLE);

            KhanAcademyVideoDetail khanAcademyVideoDetail = khanAcademyVideo.getVideoDetails();

            final String videoId = khanAcademyVideoDetail.getVideoId();
            KhanAcademyVideoSnippet khanAcademyVideoSnippet = khanAcademyVideoDetail.getSnippet();

            if (khanAcademyVideoSnippet != null) {
                setThumbnail(khanAcademyVideoSnippet, holder.mBinding.imageViewThumbnail);
            }

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(mContext)) {
                        FavouriteResource favouriteResource = new FavouriteResource();
                        favouriteResource.setName(videoId);
                        favouriteResource.setUrlThumbnail(ConstantUtil.BLANK);
                        mContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mContext, favouriteResource, false));
                    } else {
                        GeneralUtils.showToastShort(mContext, mContext.getString(R.string.connect_internet));
                    }
                }
            });

        } else {
            holder.mBinding.getRoot().setVisibility(View.GONE);
        }
    }

    private void setThumbnail(KhanAcademyVideoSnippet khanAcademyVideo, AppCompatImageView imageViewThumbnail) {
        if (khanAcademyVideo.getThumbnail() != null) {
            if (khanAcademyVideo.getThumbnail().getThumbnailStandard() != null
                    && !TextUtils.isEmpty(khanAcademyVideo.getThumbnail().getThumbnailStandard().getUrl())) {
                Picasso.with(mContext).load(khanAcademyVideo.getThumbnail().getThumbnailStandard().getUrl())
                        .placeholder(R.drawable.image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(imageViewThumbnail);
            } else if (khanAcademyVideo.getThumbnail().getThumbnailHigh() != null
                    && !TextUtils.isEmpty(khanAcademyVideo.getThumbnail().getThumbnailHigh().getUrl())) {
                Picasso.with(mContext).load(khanAcademyVideo.getThumbnail().getThumbnailHigh().getUrl())
                        .placeholder(R.drawable.image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(imageViewThumbnail);

            } else {
                Picasso.with(mContext).load(R.drawable.image_placeholder)
                        .placeholder(R.drawable.image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(imageViewThumbnail);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.image_placeholder)
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(imageViewThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LayoutExplanationKhanAcademyVideoItemBinding mBinding;

        public ViewHolder(LayoutExplanationKhanAcademyVideoItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
