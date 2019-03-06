package in.securelearning.lil.android.resources.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentFavouriteBinding;
import in.securelearning.lil.android.app.databinding.VideoListItemBinding;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.resources.model.ResourcesMapModel;
import in.securelearning.lil.android.resources.view.InjectorYoutube;
import in.securelearning.lil.android.resources.view.activity.YoutubePlayActivity;
import in.securelearning.lil.android.syncadapter.events.FavouriteResourceEvent;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Secure on 09-06-2017.
 */

public class FavouriteListFragment extends Fragment implements YouTubePlayer.OnFullscreenListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private FavouriteAdapter mAdapter;
    private boolean isFullscreen;
    FragmentFavouriteBinding binding;
    private int mColumnCount = 1;
    private int mLimit = 10;
    private int mSkip = 0;
    private int mPreviousTotal = 0;

    @Inject
    ResourcesMapModel mYoutubeMapModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    public RxBus mRxBus;


    public static FavouriteListFragment newInstance(int columnCount) {
        FavouriteListFragment fragment = new FavouriteListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        InjectorYoutube.INSTANCE.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_favourite, container, false);
        getFavouriteVideoList(mSkip, mLimit);
        setUpFavoriteRecyclerView(new ArrayList<FavouriteResource>());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.dispose();
            mAdapter = null;
        }
    }

    private void setUpFavoriteRecyclerView(ArrayList<FavouriteResource> favouriteResources) {
        mAdapter = new FavouriteAdapter(favouriteResources, getActivity());
        LinearLayoutManager layoutManager = null;
        if (mColumnCount > 1) {
            layoutManager = new GridLayoutManager(getActivity(), mColumnCount, GridLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        binding.videoList.setLayoutManager(layoutManager);
        binding.videoList.setItemAnimator(new DefaultItemAnimator());
        binding.videoList.setAdapter(mAdapter);

        if (layoutManager != null) {
            final LinearLayoutManager finalLayoutManager = layoutManager;

            binding.videoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == mPreviousTotal - 1) {
                            getFavouriteVideoList(mSkip, mLimit);
                        }
                    }

                }

            });
        }
    }

    @Override
    public void onFullscreen(boolean b) {
        this.isFullscreen = b;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refresh() {
//        binding.layoutNoResult.setVisibility(View.VISIBLE);
    }

    private void getFavouriteVideoList(final int skip, final int limit) {
        Observable.create(new ObservableOnSubscribe<ArrayList<FavouriteResource>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<FavouriteResource>> e) throws Exception {
                ArrayList<FavouriteResource> list;
                list = mYoutubeMapModel.getCompleteListOfFavoriteVideos(skip, limit);
                if (list != null) {
                    mSkip += list.size();
                    mPreviousTotal = list.size();
                    e.onNext(list);
                }
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<FavouriteResource>>() {
                    @Override
                    public void accept(ArrayList<FavouriteResource> list) throws Exception {
                        if (list.size() > 0) {
                            addValuesToRecyclerView(list);
                            if (mSkip > 0) {
                                binding.layoutNoResult.setVisibility(View.GONE);
                                binding.videoList.setVisibility(View.VISIBLE);
                            } else {
                                binding.videoList.setVisibility(View.GONE);
                                binding.layoutNoResult.setVisibility(View.VISIBLE);
                            }
                        }else if (mSkip <= 0) {
                            binding.videoList.setVisibility(View.GONE);
                            binding.layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (mSkip <= 0) {
                            binding.videoList.setVisibility(View.GONE);
                            binding.layoutNoResult.setVisibility(View.VISIBLE);
                        } else {
                            binding.videoList.setVisibility(View.VISIBLE);
                            binding.layoutNoResult.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void addValuesToRecyclerView(ArrayList<FavouriteResource> list) {
        if (list.size() > 0) {
            mAdapter.addValues(list);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.VideoViewHolder> {
        private List<FavouriteResource> mValues = new ArrayList<>();
        Context mContext;
        private boolean labelsVisible;
        @Inject
        ResourcesMapModel mYoutubeMapModel;
        @Inject
        RxBus mRxBus;

        public void dispose() {
            if (mValues != null) {
                mValues.clear();
                mValues = null;
            }
        }

        public FavouriteAdapter(List<FavouriteResource> videoList, Context context) {
            this.mValues = videoList;
            this.mContext = context;
            labelsVisible = true;
            InjectorYoutube.INSTANCE.getComponent().inject(this);
        }

        @Override
        public FavouriteAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            VideoListItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_list_item, parent, false);
            return new FavouriteAdapter.VideoViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, final int position) {
            final FavouriteResource video = mValues.get(position);
            holder.mBinding.imageViewThumbnail.setTag(video.getName());
            try {
            Picasso.with(mContext).load(video.getUrlThumbnail()).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(mContext).load(R.drawable.image_loading_thumbnail).placeholder(R.drawable.image_loading_thumbnail).into(holder.mBinding.imageViewThumbnail);
            }
            holder.mBinding.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(YoutubePlayActivity.getStartIntent(mContext, video, ""));
                }
            });

            holder.mBinding.text.setText(video.getTitle());
            if (!TextUtils.isEmpty(video.getMetaInformation().getTopic().getName()))
                holder.mBinding.textViewTopic.setText(video.getMetaInformation().getTopic().getName());
            double secondsInDouble = (int) Math.round(video.getDuration() * 100.0) / 100.0;
            int secondsInInt = (int) secondsInDouble;
            holder.mBinding.textViewVideoDuration.setText(DateUtils.convertSecondToHourMinuteSecond(secondsInInt));
            holder.mBinding.text.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
            holder.mBinding.favoriteImg.setImageResource(R.drawable.action_favorite_solid_g);
            holder.mBinding.favoriteImg.setTag("red");
            holder.mBinding.favoriteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mYoutubeMapModel.delete(video.getDocId());
                    mValues.remove(position);
                    notifyDataSetChanged();
                    if(!(mValues.size()>0)){
                        binding.layoutNoResult.setVisibility(View.VISIBLE);
                    }
                    mRxBus.send(new FavouriteResourceEvent(video.getObjectId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            int size = 0;
            if (mValues != null) {
                size = mValues.size();
            }
            return size;
        }

        public void addValues(List<FavouriteResource> videoList) {
            if (mValues != null && videoList.size() > 0) {
                mValues.addAll(videoList);
                notifyDataSetChanged();
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

    }


}
