package in.securelearning.lil.android.tracking.view.fragment;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.FragmentDialogueNewBinding;
import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.PostToGroup;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.tracking.InjectorTracking;
import in.securelearning.lil.android.tracking.model.TrackingMapModel;
import in.securelearning.lil.android.tracking.view.activity.TrackingActivityForTeacher;
import in.securelearning.lil.android.tracking.view.utils.ContactAdapter;
import in.securelearning.lil.android.tracking.view.utils.GroupMemberExt;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static in.securelearning.lil.android.tracking.view.activity.TrackingActivityForTeacher.intimationPostId;


public class DialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    String routeType;
    String selectedGroupId;
    String selectedRouteName;
    @Inject
    TrackingMapModel mTrackingModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    NetworkModel mNetworkModel;
    ContactAdapter ca;
    FragmentDialogueNewBinding binding;

    public static DialogFragment newInstance() {
        DialogFragment fragment = new DialogFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorTracking.INSTANCE.getComponent().inject(this);
        if (getArguments() != null) {
            routeType = getArguments().getString("routeType");
            selectedGroupId = getArguments().getString("selectedGroupId");
            selectedRouteName = getArguments().getString("selectedRouteName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialogue_new, container, false);
        binding.cardList.setHasFixedSize(true);
        GridLayoutManager llm = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        binding.cardList.setLayoutManager(llm);
        if (TrackingActivityForTeacher.stList != null) {
            ca = new ContactAdapter(getActivity(), routeType);
            binding.cardList.setAdapter(ca);
        }
        if (routeType != null && routeType.equalsIgnoreCase("Pick Up")) {
            binding.btnSelectStudent.setBackgroundColor(Color.parseColor("#FFBF00"));
            binding.btnSelectStudent.setText("Picked Up");
        } else if (routeType != null) {
            binding.btnSelectStudent.setBackgroundColor(Color.parseColor("#FF00FF"));
            binding.btnSelectStudent.setText("Dropped");
        }
        binding.btnSelectStudent.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_student:
                binding.progressBarCyclic.setVisibility(View.VISIBLE);
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        boolean success = true;
                        for (int i = 0; i < TrackingActivityForTeacher.stList.size(); i++) {
                            GroupMemberExt groupMemberExt = TrackingActivityForTeacher.stList.get(i);
                            if (groupMemberExt.isChecked() && !groupMemberExt.isDone()) {
                                success = success && sendMessage(groupMemberExt, i);
                            }
                        }
                        e.onNext(success);
                        e.onComplete();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            ca.notifyDataSetChanged();
                            binding.progressBarCyclic.setVisibility(View.INVISIBLE);
                            startSendMessageToStudent();
                        } else {
                            ca.notifyDataSetChanged();
                            binding.progressBarCyclic.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        }
    }

    private boolean sendMessage(final GroupMemberExt groupMemberExt, int i) {
        if (TextUtils.isEmpty(intimationPostId)) {
            PostData postData = createPost();
            if (sendPostFcm(postData)) {
                PostResponse postResponse = createPostResponse(groupMemberExt);
                if (sendPostResponseFcm(postResponse)) {
                    groupMemberExt.setDone(true);
                    return true;
                } else {
                    groupMemberExt.setDone(false);
                    return false;
                }
            }
        } else {
            PostResponse postResponse = createPostResponse(groupMemberExt);
            if (sendPostResponseFcm(postResponse)) {
                groupMemberExt.setDone(true);
                return true;
            } else {
                groupMemberExt.setDone(false);
                return false;
            }
        }
        return false;
    }

    private Observable<Boolean> sendPostResponse(final PostResponse postResponse) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                Call<PostResponse> call = mNetworkModel.uploadPostResponse(postResponse);
                Response<PostResponse> response = call.execute();
                if (response.isSuccessful()) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        });
    }

    private boolean sendPostResponseFcm(final PostResponse postResponse) {
        try {
            postResponse.setObjectId(UUID.randomUUID().toString());
            Call<MessageResponse> call = mNetworkModel.sendDataUsingFCM(selectedGroupId, postResponse, NetworkModel.TYPE_POST_RESPONSE, postResponse.getObjectId(), postResponse.getCreatedTime());
            Response<MessageResponse> response = call.execute();
            if (response.isSuccessful()) {
                if (response.body().getMessage() != null) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private PostData createPost() {
        String nowString = DateUtils.getCurrentDateTime();
        final PostData postData = new PostData();
        postData.setObjectId(null);
        postData.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getApplicationUser().getName());
        fromUser.setId(mAppUserModel.getObjectId());
        postData.setFrom(fromUser);
        PostToGroup toGroup = new PostToGroup();
        toGroup.setName(selectedRouteName);
        toGroup.setId(selectedGroupId);
        postData.setTo(toGroup);
        postData.setAlias(GeneralUtils.generateAlias("LNPostData", "" + mAppUserModel.getObjectId(), ""+System.currentTimeMillis()));
        postData.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        postData.setUnread(false);
        postData.setLastMessageTime(new Date());
        postData.setPostResources(null);
        String text = "";
        if (routeType != null && routeType.equalsIgnoreCase("Pick Up")) {
           text = "Picked up";
        } else if (routeType != null) {
            text = "Dropped off";
        }
        postData.setPostText(DateUtils.getFormatedDateFromDate(new Date()) + " : " + text + " Students");
        postData.setPostType(PostDataType.TYPE_REFERENCE_POST.toString());
        postData.setLastUpdationTime(new Date());
        return postData;
    }

    private Observable<Boolean> sendPost(final PostData postData) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                Call<PostData> call = mNetworkModel.postLearningNetworkPostData(postData);
                Response<PostData> response = call.execute();
                if (response.isSuccessful()) {
                    intimationPostId = response.body().getObjectId();
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        });
    }

    private boolean sendPostFcm(final PostData postData) {
        try {
            postData.setObjectId(UUID.randomUUID().toString());
            Call<MessageResponse> call = mNetworkModel.sendDataUsingFCM(selectedGroupId, postData, NetworkModel.TYPE_POST_DATA, postData.getObjectId(), postData.getCreatedTime());
            Response<MessageResponse> response = call.execute();
            if (response.isSuccessful()) {
                if (response.body().getMessage() != null) {
                    intimationPostId = postData.getObjectId();
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PostResponse createPostResponse(GroupMemberExt groupMemberExt) {
        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getApplicationUser().getName());
        fromUser.setId(mAppUserModel.getObjectId());
        postResponse.setFrom(fromUser);
        postResponse.setObjectId(null);
        postResponse.setPostID(intimationPostId);
        postResponse.setResources(null);
        postResponse.setText(groupMemberExt.getName());
        postResponse.setType(PostResponseType.TYPE_COMMENT.toString());
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUpdatedTime(new Date());
        postResponse.setGroupId(selectedGroupId);
        postResponse.setAlias(GeneralUtils.generateAlias("PostResponse", fromUser.getId(), "" + System.currentTimeMillis()));
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        return postResponse;
    }

    private void startSendMessageToStudent() {
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog_fragment");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}
