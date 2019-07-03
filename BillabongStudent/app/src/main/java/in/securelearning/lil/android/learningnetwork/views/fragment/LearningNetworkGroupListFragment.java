package in.securelearning.lil.android.learningnetwork.views.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.LayoutFragmentLearningNetworkGroupListBinding;
import in.securelearning.lil.android.app.databinding.RyanLayoutItemLearningNetworkGroupBinding;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.Thumbnail;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.AnimationUtils;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.home.dataobjects.TimeUtils;
import in.securelearning.lil.android.home.events.AnimateFragmentEvent;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostCreatedEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadNewPostReceivedEvent;
import in.securelearning.lil.android.learningnetwork.events.RefreshGroupListUnreadCount;
import in.securelearning.lil.android.learningnetwork.model.PostDataLearningModel;
import in.securelearning.lil.android.learningnetwork.views.activity.PostListActivity;
import in.securelearning.lil.android.syncadapter.events.ObjectDownloadComplete;
import in.securelearning.lil.android.syncadapter.utils.CircleTransform;
import in.securelearning.lil.android.syncadapter.utils.CommonUtils;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chaitendra on 08-Aug-17.
 */

public class LearningNetworkGroupListFragment extends Fragment {
    @Inject
    RxBus mRxBus;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    PostDataLearningModel mPostDataLearningModel;

    @Inject
    GroupModel mGroupModel;

    private static String ARG_COLUMN_COUNT = "column-count";
    private LayoutFragmentLearningNetworkGroupListBinding mBinding;
    private boolean isTeacher = false;
    private int mColumnCount = 1;

    private GroupAdapter mGroupAdapter;
    private Disposable mSubscription;

    public static LearningNetworkGroupListFragment newInstance(int columnCount) {
        LearningNetworkGroupListFragment fragment = new LearningNetworkGroupListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) mSubscription.dispose();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        isTeacher = PermissionPrefsCommon.getPostBadgeAssignPermission(getContext());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_fragment_learning_network_group_list, container, false);
        setDefault();
        listenRxBusEvent();
        getData();
        return mBinding.getRoot();
    }

    private void getData() {
        mPostDataLearningModel.getGroupForUser().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Group>() {
            @Override
            public void accept(Group group) throws Exception {

                mGroupAdapter.addItem(group);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                mGroupAdapter.sortItem();
                mGroupAdapter.notifyDataSetChanged();
            }
        });
    }

    private void listenRxBusEvent() {
        mSubscription = mRxBus.toFlowable().observeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
            @SuppressLint("CheckResult")
            @Override
            public void accept(final Object event) throws Exception {
                if (event instanceof LoadNewPostCreatedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    getData();
                                }
                            });

                } else if (event instanceof LoadNewPostReceivedEvent) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    getData();
                                }
                            });


                } else if (event instanceof ObjectDownloadComplete && ((ObjectDownloadComplete) event).getObjectClass().equals(Group.class)) {
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    getData();
                                }
                            });

                } else if (event instanceof RefreshGroupListUnreadCount) {
                    final String alias = ((RefreshGroupListUnreadCount) event).getAlias();
                    Completable.complete().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() {
                                    mGroupAdapter.updateCount(alias);
                                }
                            });
                } else if (event instanceof AnimateFragmentEvent) {
                    int id = ((AnimateFragmentEvent) event).getId();
                    if (id == R.id.nav_learning_network) {
                        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                AnimationUtils.fadeInFast(getContext(), mBinding.layoutNoResult);
                                AnimationUtils.fadeInFast(getContext(), mBinding.recyclerView);


                            }
                        });

                    }
                }

            }
        });
    }

    private void setDefault() {

        initializeRecyclerView(new ArrayList<Group>());

    }

    private void initializeRecyclerView(ArrayList<Group> groups) {

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mGroupAdapter = new GroupAdapter(groups);
        mBinding.recyclerView.setAdapter(mGroupAdapter);
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
        ArrayList<Group> mGroups = new ArrayList<>();

        public GroupAdapter(ArrayList<Group> groups) {
            this.mGroups = groups;
        }

        @Override
        public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RyanLayoutItemLearningNetworkGroupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ryan_layout_item_learning_network_group, parent, false);
            return new GroupAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final GroupAdapter.ViewHolder holder, final int position) {
            final Group group = mGroups.get(position);
            //setGroupThumbnail(group, holder.mBinding.imageViewGroupThumbnail);

            String name = group.getGroupName();
            if (isTeacher) {
                if (!TextUtils.isEmpty(group.getNameTeacher())) {
                    name = group.getNameTeacher();
                }
            } else if (!TextUtils.isEmpty(group.getNameStudent())) {
                name = group.getNameStudent();
            }
            holder.mBinding.textViewGroupName.setText(name);
            //Need to set group name according to Two Letter
            CommonUtils.getInstance().
                    setGroupThumbnail(getContext(), name, group.getThumbnail(), holder.mBinding.imageViewGroupThumbnail) ;

            setLatestPost(group, holder.mBinding);

//            setDividerVisibility(holder.mBinding.viewDivider, position);

            holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(PostListActivity.getIntentForPostList(getContext(), group.getObjectId(), false));
                    holder.mBinding.textViewUnreadCount.setVisibility(View.GONE);
                    //     mPostDataLearningModel.deleteAllNewPostByGroupId(group.getObjectId());
                }
            });


        }

        private void setDividerVisibility(View viewDivider, int position) {
            if (position == mGroups.size() - 1) {
                viewDivider.setVisibility(View.GONE);
            } else {
                viewDivider.setVisibility(View.VISIBLE);
            }
        }

        private void setLatestPost(final Group group, final RyanLayoutItemLearningNetworkGroupBinding binding) {
            PostData postData = group.getPostData();
            if (postData != null) {

                binding.textViewPostByName.setText(postData.getFrom().getName() + ":");
                binding.textViewPostByName.setVisibility(View.VISIBLE);
                String postedText = String.valueOf(Html.fromHtml(postData.getPostText()));
                postedText = postedText.replaceAll("\n", " ");
                binding.textViewPostText.setText(postedText);
                binding.textViewLastActiveTime.setText(TimeUtils.getRealTimeString(postData.getCreatedTime()));
                if (postData.getPostResources() != null && !postData.getPostResources().isEmpty()) {
                    final ArrayList<Resource> resources = (ArrayList<Resource>) postData.getPostResources();
                    String mimeType = URLConnection.guessContentTypeFromName(resources.get(0).getDeviceURL());
                    if (mimeType != null && mimeType.contains("image")) {
                        binding.imageViewPostType.setVisibility(View.VISIBLE);
                        binding.imageViewPostType.setImageResource(R.drawable.image);
                    } else if (mimeType != null && mimeType.contains("video")) {
                        binding.imageViewPostType.setVisibility(View.VISIBLE);
                        binding.imageViewPostType.setImageResource(R.drawable.video);
                    } else {
                        binding.imageViewPostType.setVisibility(View.GONE);
                    }
                } else {
                    binding.imageViewPostType.setVisibility(View.GONE);
                }
                group.setLastMessageTime(DateUtils.convertrIsoDate(postData.getCreatedTime()));
            } else {
                binding.imageViewPostType.setVisibility(View.GONE);
                binding.textViewPostByName.setVisibility(View.GONE);
                binding.textViewPostText.setText(R.string.string_you_are_now_member_of_his_group);
                binding.textViewLastActiveTime.setText(TimeUtils.getRealTimeString(group.getLastMessageTime()));
            }

            mPostDataLearningModel.getUnreadPostCountForGroup(group.getObjectId())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer unreadCount) throws Exception {

                    if (unreadCount > 0) {
                        binding.textViewUnreadCount.setVisibility(View.VISIBLE);
                        binding.textViewUnreadCount.setText(String.valueOf(unreadCount));
                    } else {
                        binding.textViewUnreadCount.setVisibility(View.GONE);
                    }
                }
            });


        }

        private void setGroupThumbnail(Group group, AppCompatImageView imageView) {
            if (group.getThumbnail().getLocalUrl() != null && !group.getThumbnail().getLocalUrl().isEmpty()) {
                Picasso.with(getContext()).load(group.getThumbnail().getLocalUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(imageView);
            } else if (group.getThumbnail().getUrl() != null && !group.getThumbnail().getUrl().isEmpty()) {
                Picasso.with(getContext()).load(group.getThumbnail().getUrl()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(imageView);
            } else if (group.getThumbnail().getThumb() != null && !group.getThumbnail().getThumb().isEmpty()) {
                Picasso.with(getContext()).load(group.getThumbnail().getThumb()).transform(new CircleTransform()).placeholder(R.drawable.audience_g_w).resize(300, 300).centerCrop().into(imageView);
            } else {
                Picasso.with(getContext()).load(R.drawable.audience_g_w).transform(new CircleTransform()).into(imageView);
            }
        }

        @Override
        public int getItemCount() {
            return mGroups.size();
        }

        public void addItem(Group group) {
            if (mGroups != null) {
                int index = mGroups.indexOf(group);
                if (index > -1) {
                    mGroups.set(index, group);
                } else {
                    mGroups.add(group);
                }
            }

        }

        public void clear() {
            if (mGroups != null) {
                mGroups.clear();
            }
        }

        public void updateCount(String alias) {
            for (int i = 0; i < mGroups.size(); i++) {
                if (mGroups.get(i).getAlias().equals(alias)) {

                    notifyItemChanged(i);
                }
            }
        }

        public void sortItem() {
            Collections.sort(mGroups, new SortGroupListByLatestActivity());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RyanLayoutItemLearningNetworkGroupBinding mBinding;

            public ViewHolder(RyanLayoutItemLearningNetworkGroupBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
    }

    public static class SortGroupListByLatestActivity implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            if (lhs != null && rhs != null && ((Group) lhs).getLastMessageTime() != null && ((Group) rhs).getLastMessageTime() != null) {

                long secondDate = ((Group) rhs).getLastMessageTime().getTime();
                long firstDate = ((Group) lhs).getLastMessageTime().getTime();

                if (firstDate == 0 || secondDate == 0) {
                    return 0;
                }
                if (firstDate > secondDate) return -1;
                else if (firstDate < (secondDate)) return 1;
                else return 0;
            } else {
                if (lhs != null && ((Group) lhs).getLastMessageTime() != null && ((Group) lhs).getLastMessageTime().getTime() > 0)
                    return -1;
                if (rhs != null && ((Group) rhs).getLastMessageTime() != null && ((Group) rhs).getLastMessageTime().getTime() > 0)
                    return 1;

                return 0;
            }
        }
    }

}
