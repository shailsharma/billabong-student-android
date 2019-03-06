package in.securelearning.lil.android.resources.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.VideoListItemBinding;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.resources.model.ResourcesMapModel;
import in.securelearning.lil.android.resources.view.InjectorYoutube;
import in.securelearning.lil.android.resources.view.activity.VideoPlayActivity;
import in.securelearning.lil.android.resources.view.activity.VimeoActivity;
import in.securelearning.lil.android.resources.view.activity.YoutubePlayActivity;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Secure on 08-06-2017.
 */

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.VideoViewHolder> {
    private List<FavouriteResource> mValues = new ArrayList<>();
    Context mContext;
    private ThumbnailListener thumbnailListener;
    private Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private boolean labelsVisible;
    private int mColumnCount = 1;
    private Observable<ArrayList<FavouriteResource>> favorites;
    @Inject
    ResourcesMapModel mYoutubeMapModel;

    public void dispose() {
        if (mValues != null) {
            mValues.clear();
            mValues = null;
        }
        if (thumbnailViewToLoaderMap != null) {
            thumbnailViewToLoaderMap.clear();
            thumbnailViewToLoaderMap = null;
        }
    }

    public RecommendedAdapter(List<FavouriteResource> videoList, Context context, int columnCount) {
        this.mValues = videoList;
        this.mContext = context;
        thumbnailViewToLoaderMap = new HashMap<>();
        this.thumbnailListener = new ThumbnailListener();
        labelsVisible = true;
        this.mColumnCount = columnCount;
        InjectorYoutube.INSTANCE.getComponent().inject(this);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VideoListItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_list_item, parent, false);
        return new VideoViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position) {
        final FavouriteResource video = mValues.get(position);
        holder.mBinding.imageViewThumbnail.setTag(video.getName());
        if (!TextUtils.isEmpty(video.getMetaInformation().getTopic().getName()))
            holder.mBinding.textViewTopic.setText(video.getMetaInformation().getTopic().getName());
        try {
            Picasso.with(mContext).load(video.getUrlThumbnail()).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(mContext).load(R.drawable.image_loading_thumbnail).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
        }

        holder.mBinding.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeneralUtils.isNetworkAvailable(mContext)) {
                    if (video.getType().equalsIgnoreCase("video")) {
                        mContext.startActivity(VideoPlayActivity.getStartIntent(mContext, video));
                    } else if (video.getType().equalsIgnoreCase("youtube#video")) {
                        mContext.startActivity(YoutubePlayActivity.getStartIntent(mContext, video, ""));
                    } else if (video.getType().equalsIgnoreCase("vimeo")) {
                        //mContext.startActivity(VimeoActivity.getStartIntent(mContext, video.getSourceURL(), video.getTitle(), video.getMetaInformation().getSubject().getName(), video.getMetaInformation().getTopic().getName(), video.getMetaInformation().getGrade().getName(), video.getMetaInformation().getLearningLevel().getName()));
                        mContext.startActivity(VimeoActivity.getStartIntent(mContext, video));
                    }
                } else {
                    SnackBarUtils.showNoInternetSnackBar(mContext, v);
                }
            }
        });
        holder.mBinding.text.setText(video.getTitle());
        double secondsInDouble = (int) Math.round(video.getDuration() * 100.0) / 100.0;
        int secondsInInt = (int) secondsInDouble;
        holder.mBinding.textViewVideoDuration.setText(DateUtils.convertSecondToHourMinuteSecond(secondsInInt));
        holder.mBinding.text.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
        holder.mBinding.favoriteImg.setVisibility(View.GONE);
        if (TextUtils.isEmpty(video.getDocId())) {
            checkFavoriteItem(video).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<FavouriteResource>() {
                @Override
                public void accept(final FavouriteResource favorite) throws Exception {
                    if (!TextUtils.isEmpty(favorite.getDocId()) && favorite.getObjectId().equals(video.getObjectId())) {
                        video.setDocId(favorite.getDocId());
                        if (!TextUtils.isEmpty(video.getDocId())) {
                            holder.mBinding.favoriteImg.setImageResource(R.drawable.action_favorite_solid_g);
                            holder.mBinding.favoriteImg.setTag("red");
                        }
                    } else {
                        holder.mBinding.favoriteImg.setImageResource(R.drawable.action_favorite_g);
                        holder.mBinding.favoriteImg.setTag("grey");
                    }
                    holder.mBinding.favoriteImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String tag = holder.mBinding.favoriteImg.getTag().toString();
                            if (tag.equalsIgnoreCase("grey")) {
                                FavouriteResource favouriteR = mYoutubeMapModel.saveFavouriteResource(video);
                                video.setDocId(favouriteR.getDocId());
                                holder.mBinding.favoriteImg.setImageResource(R.drawable.action_favorite_solid_g);
                                holder.mBinding.favoriteImg.setTag("red");
                            } else {
                                if (mYoutubeMapModel.delete(video.getDocId())) {
                                    video.setDocId("");
                                }
                                holder.mBinding.favoriteImg.setImageResource(R.drawable.action_favorite_g);
                                holder.mBinding.favoriteImg.setTag("grey");
                            }
                        }
                    });
                }
            });
        } else {
            holder.mBinding.favoriteImg.setImageResource(R.drawable.action_favorite_g);
            holder.mBinding.favoriteImg.setTag("grey");
        }
    }

    public Observable<FavouriteResource> checkFavoriteItem(final FavouriteResource checkProduct) {
        // Backgroung thread call
        return Observable.create(new ObservableOnSubscribe<FavouriteResource>() {
            @Override
            public void subscribe(ObservableEmitter<FavouriteResource> e) throws Exception {
                FavouriteResource favorite = mYoutubeMapModel.getFavouriteResource(checkProduct.getObjectId());
                if (favorite != null) {
                    e.onNext(favorite);
                }
                e.onComplete();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();

        }
        return 0;

    }

    public void addValues(ArrayList<FavouriteResource> videoList) {
        if (mValues != null) {
            mValues.addAll(videoList);
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


    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        VideoListItemBinding mBinding;

        VideoViewHolder(VideoListItemBinding binding) {
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

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.image_loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            view.setImageResource(R.drawable.image_placeholder);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
            view.setImageResource(R.drawable.image_placeholder);
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
