package in.securelearning.lil.android.resources.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentVideoPlayerBinding;
import in.securelearning.lil.android.resources.adapter.VideoPlayerPagerAdapter;

/**
 * Created by Secure on 12-06-2017.
 */

public class VideoPlayerFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    VideoPlayerPagerAdapter adapterViewPager;
    FragmentVideoPlayerBinding binding;
    private int mColumnCount = 1;


    public static VideoPlayerFragment newInstance(int columnCount) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.fragment_video_player, container, false);
        adapterViewPager = new VideoPlayerPagerAdapter(getActivity().getSupportFragmentManager());
        adapterViewPager.addFragment(RecommendedListFragment.newInstance(mColumnCount),"Recommended Resource");
        adapterViewPager.addFragment(FavouriteListFragment.newInstance(mColumnCount),"Favourite");
        binding.vpPager.setAdapter(adapterViewPager);
        binding.tabs.setupWithViewPager(binding.vpPager);
        binding.tabs.setTabTextColors(ContextCompat.getColor(getContext(), R.color.colorWhite66),ContextCompat.getColor(getContext(), R.color.colorWhite));
        binding.tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.tabIndicatorLN));
        return binding.getRoot();
    }

    public void filter() {

    }


}

