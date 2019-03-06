package in.securelearning.lil.android.resources.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.securelearning.lil.android.resources.view.fragment.FavouriteListFragment;
import in.securelearning.lil.android.resources.view.fragment.RecommendedListFragment;

/**
 * Created by Secure on 09-06-2017.
 */

public class VideoPlayerPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public VideoPlayerPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof RecommendedListFragment) {
//            ((RecommendedListFragment) object).refresh();
        } else if (object instanceof FavouriteListFragment) {
            ((FavouriteListFragment) object).refresh();
        }
        return super.getItemPosition(object);

    }
}
