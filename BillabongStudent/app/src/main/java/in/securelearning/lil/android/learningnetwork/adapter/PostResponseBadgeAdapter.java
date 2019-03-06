package in.securelearning.lil.android.learningnetwork.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignBadgeModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.learningnetwork.InjectorLearningNetwork;
import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Adapter for binds badges data
 * Created by Cp on 9/1/2016.
 */
public class PostResponseBadgeAdapter extends RecyclerView.Adapter<PostResponseBadgeAdapter.ViewHolder> {
    Dialog mDialog;
    ArrayList<LILBadges> lilBadges;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    AssignBadgeModel mAssignBadgeModel;

    @Inject
    PostDataModel mPostDataModel;

    private Context mContext;
    private PostResponse mPostResponse;
    private String groupId;
    LearningNetworkDetailAdapter.BadgeAssignedInterface badgeAssignedInterface;

    public PostResponseBadgeAdapter(Context context, ArrayList<LILBadges> lilBadges, Dialog dialog, PostResponse postResponse, String groupId, LearningNetworkDetailAdapter.BadgeAssignedInterface badgeAssignedInterface) {
        this.mContext = context;
        this.lilBadges = lilBadges;
        this.mDialog = dialog;
        this.mPostResponse = postResponse;
        InjectorLearningNetwork.INSTANCE.getLearningNetworkComponent().inject(this);
        this.groupId = groupId;
        this.badgeAssignedInterface = badgeAssignedInterface;
    }

    public static int getRawId(String mName) {

        int resId = 0;
        try {
            Field field = R.drawable.class.getField(mName);
            resId = field.getInt(null);
        } catch (Exception e) {
            // TODO: handle exception
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
                showBadgeAssignedToast();
                addBadgeToUserProfile(holder.getAdapterPosition());


            }
        });
    }

    private void addBadgeToUserProfile(int adapterPosition) {
//        final AssignedBadges assignedBadges = new AssignedBadges();
//        assignedBadges.setAlias(GeneralUtils.generateAlias("PostResponseBadge", mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
//        assignedBadges.setAssignedFor(mPostResponse.getFrom().getId());
//        assignedBadges.setAssignedFrom(mAppUserModel.getObjectId());
//        assignedBadges.setBadgeId(lilBadges.get(adapterPosition).getId());
//        assignedBadges.setCreatedBy(mAppUserModel.getObjectId());
//        assignedBadges.setCreationTime("" + System.currentTimeMillis() / 1000);
//        assignedBadges.setLastUpdationTime("" + System.currentTimeMillis() / 1000);
//        assignedBadges.setLastUpdatedBy(mAppUserModel.getObjectId());
//        assignedBadges.setType(BadgesType.TYPE_POST_RESPONSE.getBadgesType());
//        assignedBadges.setSyncStatus(SyncStatus.NOT_SYNC.toString());
//
//        NotificationTo notificationTo = new NotificationTo();
//        notificationTo.setUserId(mPostResponse.getFrom().getId());
//        notificationTo.setGroupId(groupId);
//
//        assignedBadges.setTo(notificationTo);
//        assignedBadges.setObjectId(null);
//
//
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
            }
        });

        badgeAssignedInterface.onBadgeAssigned(postResponse);


    }


    private PostResponse getPostResponse(LILBadges lilBadges) {

        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getApplicationUser().getName());
        fromUser.setId(mAppUserModel.getObjectId());
        postResponse.setFrom(fromUser);
        postResponse.setTo(mPostResponse.getTo());
        postResponse.setObjectId(null);
        postResponse.setAlias(GeneralUtils.generateAlias("LNPostResponse", "" + mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));

        postResponse.setPostID(mPostResponse.getPostID());
        postResponse.setResources(new ArrayList<String>());
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        postResponse.setText("");
        postResponse.setType(PostResponseType.TYPE_BADGE.getPostResponseType());//Badge type etc
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUnread(false);
        postResponse.setUpdatedTime(new Date());
        postResponse.setAssignedPostResponseId(mPostResponse.getObjectId());

        postResponse.setAssignedBadgeId(lilBadges.getObjectId());

        return postResponse;
    }


    private void showBadgeAssignedToast() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastRoot = inflater.inflate(R.layout.layout_toast_with_imageview, null);
        Toast toast = new Toast(mContext);
        toast.setView(toastRoot);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
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
