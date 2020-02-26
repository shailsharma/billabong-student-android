package in.securelearning.lil.android.lrpa.views.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.learningnetwork.views.fragment.PostListFragment;
import in.securelearning.lil.android.lrpa.views.fragment.ChaptersFragment;
import in.securelearning.lil.android.lrpa.views.fragment.SubjectDetailHomeFragment;
import in.securelearning.lil.android.lrpa.views.fragment.SubjectHomeworkFragment;

/*viewpager adapter to handle and attach the respective fragments of activity*/
public class SubjectDetailsTabAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> mTabTitles;
    private Context mContext;
    private String mSubjectId;
    private String mTopicId;
    private String mTopicName;
    private String mSubjectName;
    private String mGroupId;
    private String mBannerUrl;

    public SubjectDetailsTabAdapter(Context context, FragmentManager fragmentManager, ArrayList<String> tabTitles,
                                    String subjectId, String topicId, String topicName, String subjectName,
                                    String groupId, String bannerUrl) {
        super(fragmentManager);
        this.mContext = context;
        this.mTabTitles = tabTitles;
        this.mSubjectId = subjectId;
        this.mTopicId = topicId;
        this.mTopicName = topicName;
        this.mSubjectName = subjectName;
        this.mGroupId = groupId;
        this.mBannerUrl = bannerUrl;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        if (mTabTitles.get(position).equals(mContext.getString(R.string.label_home))) {
            return SubjectDetailHomeFragment.newInstance(mSubjectId, mTopicId, mTopicName, mSubjectName, mBannerUrl);
        } else if (mTabTitles.get(position).equals(mContext.getString(R.string.chapters))) {
            return ChaptersFragment.newInstance(mSubjectId);
        } else if (mTabTitles.get(position).equals(mContext.getString(R.string.homework))) {
            return SubjectHomeworkFragment.newInstance(mSubjectId);
        } else if (mTabTitles.get(position).equals(mContext.getString(R.string.string_post))) {
            return PostListFragment.newInstance(1, mGroupId, false, R.color.colorPrimary);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mTabTitles.size();
    }

}