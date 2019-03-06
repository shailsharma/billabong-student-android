package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutFragmentGroupDetailBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;

/**
 * Created by Chaitendra on 11-Aug-17.
 */

public class GroupDetailFragment extends Fragment {

    @Inject
    GroupModel mGroupModel;

    @Inject
    AppUserModel mAppUserModel;
    private LayoutFragmentGroupDetailBinding mBinding;
    private static String GROUP_ID = "groupId";
    private String mGroupId = "";
    private Group mGroup;

    public static GroupDetailFragment newInstance(String groupId) {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        if (getArguments() != null) {
            mGroupId = getArguments().getString(GROUP_ID);
            mGroup = mGroupModel.getGroupFromUidSync(mGroupId);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_fragment_group_detail, container, false);
        setGroupDetail();
        setGroupMembersAndModerators();
        return mBinding.getRoot();
    }

    private void setGroupDetail() {


    }

    private void setGroupMembersAndModerators() {

    }


}
