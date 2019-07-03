package in.securelearning.lil.android.learningnetwork.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AppUser;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignBadgeModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.utils.PermissionPrefsCommon;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Adapter for binds badges data
 * Created by Cp on 9/1/2016.
 */
public class PostDataBadgeAdapter extends RecyclerView.Adapter<PostDataBadgeAdapter.ViewHolder> {
    Dialog mDialog;
    ArrayList<LILBadges> lilBadges;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    PostDataModel mPostDataModel;
    @Inject
    AssignBadgeModel mAssignBadgeModel;
    private Context mContext;
    private PostData mPostData;
    LearningNetworkDetailAdapter.BadgeAssignedInterface badgeAssignedInterface;

    public PostDataBadgeAdapter(Context context, ArrayList<LILBadges> lilBadges, Dialog dialog, PostData postData, LearningNetworkDetailAdapter.BadgeAssignedInterface badgeAssignedInterface) {
        this.mContext = context;
        this.lilBadges = lilBadges;
        this.mPostData = postData;
        this.mDialog = dialog;
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);

        this.badgeAssignedInterface = badgeAssignedInterface;
    }

    public static int getRawId(String mName) {

        int resId = 0;
        try {
            Field field = R.drawable.class.getField(mName);
            resId = field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resId;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_badge_imageview, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBadgeImageView.setTag(lilBadges.get(position).getThumbnail());
        Picasso.with(mContext).load(getRawId(lilBadges.get(position).getThumbnail())).into(holder.mBadgeImageView);
        holder.mBadgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                ToastUtils.showToastSuccess(mContext, mContext.getString(R.string.badge_ass));
                addBadgeToUserProfile(holder.getAdapterPosition());

            }
        });
    }

    private void addBadgeToUserProfile(int adapterPosition) {

//        final AssignedBadges assignedBadges = new AssignedBadges();
//        assignedBadges.setAlias(GeneralUtils.generateAlias("PostBadge", mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
//        assignedBadges.setAssignedFor(mPostData.getFrom().getId());
//        assignedBadges.setAssignedFrom(mAppUserModel.getObjectId());
//        assignedBadges.setBadgeId(lilBadges.get(adapterPosition).getObjectId());
//        assignedBadges.setCreatedBy(mAppUserModel.getObjectId());
//        assignedBadges.setCreationTime("" + System.currentTimeMillis() / 1000);
//        assignedBadges.setLastUpdationTime("" + System.currentTimeMillis() / 1000);
//        assignedBadges.setLastUpdatedBy(mAppUserModel.getObjectId());
//
//        NotificationTo notificationTo = new NotificationTo();
//        notificationTo.setUserId(mPostData.getFrom().getId());
//        notificationTo.setGroupId(mPostData.getTo().getId());
//
//        assignedBadges.setTo(notificationTo);
//        assignedBadges.setType(BadgesType.TYPE_POST.getBadgesType());
//        assignedBadges.setSyncStatus(SyncStatus.NOT_SYNC.toString());
//
//        assignedBadges.setObjectId(null);
//        Observable.just(null).subscribeOn(Schedulers.computation()).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) {
//                //FitnessUserProfile user = mAppUserModel.fetchUserByUserUId(mPostData.getFrom().getId());
//                // user.getBadgesEarned().add(lilBadges);
//                // mAppUserModel.updateUserProfileInDB(user);
//                mAssignBadgeModel.saveAssignedBadges(assignedBadges);
//            }
//        });

        final PostResponse postResponse = getPostResponse(lilBadges.get(adapterPosition));

        Completable.complete().subscribeOn(Schedulers.computation()).subscribe(new Action() {
            @Override
            public void run() {

                mPostDataModel.savePostResponse(postResponse);
                //SyncServiceHelper.startUploadPostResponse(mContext, postResponse.getAlias());
            }
        });

        badgeAssignedInterface.onBadgeAssigned(postResponse);

    }


    private PostResponse getPostResponse(LILBadges lilBadges) {

        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getApplicationUser().getName());
        fromUser.setId(mAppUserModel.getObjectId());
        fromUser.setRole(setUserRole());
        postResponse.setFrom(fromUser);
        postResponse.setTo(mPostData.getTo());
        postResponse.setObjectId(null);
        postResponse.setAlias(GeneralUtils.generateAlias("LNPostResponse", "" + mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
        postResponse.setPostID(mPostData.getObjectId());
        postResponse.setResources(new ArrayList<String>());
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        postResponse.setText("");
        postResponse.setType(PostResponseType.TYPE_BADGE.getPostResponseType());//Badge type etc
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUnread(false);
        postResponse.setUpdatedTime(new Date());
        postResponse.setAssignedPostResponseId(null);
        postResponse.setAssignedBadgeId(lilBadges.getObjectId());

        return postResponse;
    }

    private String setUserRole() {
        String strRole = "";
        if (PermissionPrefsCommon.getPostBadgeAssignPermission(mContext)) {
            strRole = AppUser.USERTYPE.TEACHER.toString();
        } else {
            strRole = AppUser.USERTYPE.STUDENT.toString();
        }
        return strRole;
    }

    @Override
    public int getItemCount() {
        return lilBadges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mRootView;
        private ImageButton mBadgeImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mBadgeImageView = (ImageButton) mRootView.findViewById(R.id.imageViewBadge);
        }
    }

}
