package in.securelearning.lil.android.player.microlearning.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutFeaturedCardItemBinding;
import in.securelearning.lil.android.app.databinding.LayoutRecyclerViewBinding;
import in.securelearning.lil.android.base.dataobjects.MicroLearningCourse;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.player.microlearning.InjectorPlayer;
import in.securelearning.lil.android.player.microlearning.model.PlayerModel;
import in.securelearning.lil.android.player.microlearning.view.activity.RapidLearningSectionListActivity;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 27-Feb-18.
 */

public class FeaturedCardListFragment extends Fragment {
    LayoutRecyclerViewBinding mBinding;

    @Inject
    PlayerModel mPlayerModel;

    public static FeaturedCardListFragment newInstance() {
        FeaturedCardListFragment fragment = new FeaturedCardListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorPlayer.INSTANCE.getComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_recycler_view, container, false);
        mBinding.layoutMain.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));

        getFeaturedCards();
        return mBinding.getRoot();
    }

    private void getFeaturedCards() {
        if (GeneralUtils.isNetworkAvailable(getContext())) {
            mBinding.list.setVisibility(View.GONE);
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mPlayerModel.getMicroLearningCourseList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ArrayList<MicroLearningCourse>>() {
                        @Override
                        public void accept(ArrayList<MicroLearningCourse> microLearningCourses) throws Exception {
                            mBinding.progressBar.setVisibility(View.GONE);
                            if (microLearningCourses != null && !microLearningCourses.isEmpty()) {
                                initializeRecyclerView(microLearningCourses);
                                mBinding.list.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (throwable.getMessage() != null && throwable.getMessage().equalsIgnoreCase(getString(R.string.messageFeaturedCardsNotFound))) {
                                noResultFound();
                            } else {
                                unableToFetch();
                            }
                        }
                    });
        } else {
            noInternet();
        }

    }

    private void unableToFetch() {
        mBinding.list.setVisibility(View.GONE);
        mBinding.progressBar.setVisibility(View.GONE);
        mBinding.layoutRetry.setVisibility(View.VISIBLE);
        mBinding.imageViewRetry.setImageResource(R.drawable.course_gray);
        mBinding.textViewRetry.setText(getContext().getString(R.string.messageFeaturedCardsUnableToNotFound));
        mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFeaturedCards();
            }
        });
    }

    private void noInternet() {
        mBinding.list.setVisibility(View.GONE);
        mBinding.layoutRetry.setVisibility(View.VISIBLE);
        mBinding.imageViewRetry.setImageResource(R.drawable.no_internet);
        mBinding.textViewRetry.setText(getContext().getString(R.string.error_message_no_internet));
        mBinding.buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFeaturedCards();
            }
        });
    }

    private void noResultFound() {
        mBinding.layoutNoResult.setVisibility(View.VISIBLE);
        mBinding.progressBar.setVisibility(View.GONE);
        mBinding.textViewNoResult.setText(R.string.messageFeaturedCardsNotFound);
        mBinding.imageViewNoResult.setImageResource(R.drawable.course_gray);
    }

    private void initializeRecyclerView(ArrayList<MicroLearningCourse> list) {
        mBinding.list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(list);
        mBinding.list.setAdapter(recyclerViewAdapter);
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        ArrayList<MicroLearningCourse> mList;

        public RecyclerViewAdapter(ArrayList<MicroLearningCourse> list) {
            mList = list;

        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutFeaturedCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_featured_card_item, parent, false);
            return new RecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            final MicroLearningCourse object = mList.get(position);
            setThumbnail(object.getThumbnail(), holder.mBinding.imageView);
            holder.mBinding.textViewTitle.setText(object.getTitle());
            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GeneralUtils.isNetworkAvailable(getContext())) {
                        startActivity(RapidLearningSectionListActivity.getStartIntent(getContext(), object.getObjectId()));
                    } else {
                        SnackBarUtils.showNoInternetSnackBar(getContext(), mBinding.list);
                    }
                }
            });
        }

        private void setThumbnail(Thumbnail thumbnail, ImageView imageView) {
            String imagePath = thumbnail.getUrl();
            if (imagePath.isEmpty()) {
                imagePath = thumbnail.getThumb();
            }

            try {
                if (!imagePath.isEmpty()) {
                    Picasso.with(getContext()).load(imagePath).into(imageView);
                } else {
                    Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(imageView);
                }
            } catch (Exception e) {
                try {
                    Picasso.with(getContext()).load(thumbnail.getThumb()).into(imageView);
                } catch (Exception e1) {
                    try {
                        Picasso.with(getContext()).load(in.securelearning.lil.android.base.R.drawable.image_large).into(imageView);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LayoutFeaturedCardItemBinding mBinding;

            public ViewHolder(LayoutFeaturedCardItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }
}
