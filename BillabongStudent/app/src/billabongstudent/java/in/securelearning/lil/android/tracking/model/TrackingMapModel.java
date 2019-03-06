package in.securelearning.lil.android.tracking.model;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.SparseArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.constants.PostDataType;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.Moderator;
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.PostToGroup;
import in.securelearning.lil.android.base.dataobjects.TrackingLocation;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.model.TrackingModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.fcmservices.MessageResponse;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.tracking.InjectorTracking;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Secure on 26-04-2017.
 */

public class TrackingMapModel {

    @Inject
    Context mContext;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    RxBus rxBus;
    @Inject
    TrackingModel trackingModel;
    @Inject
    PostDataModel mPostDataModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    GroupModel mGroupModel;


    public TrackingMapModel() {
        InjectorTracking.INSTANCE.getComponent().inject(this);
    }

//    public PostResponse isRouteEnable() {
//        PostData pData = trackingModel.getTrackingEventByDate(DateUtils.getSecondsForMorningFromDate(new Date()), DateUtils.getSecondsForMidnightFromDate(new Date()));
//        if (pData != null && !TextUtils.isEmpty(pData.getObjectId())) {
//            ArrayList<PostResponse> list = mPostDataModel.getPostResponsesFromPostId(pData.getObjectId());
//            Collections.sort(list, new SortPostResponseByDate.CreatedDateSorter());
//            if (list.size() > 0 && list.get(list.size() - 1).getText().toLowerCase().contains("stop")) {
//                return null;
//            } else if (list.size() > 0) {
//                return list.get(list.size() - 1);
//            }
//        }
//
//        return null;
//    }

    public TrackingRoute getRoute(double[] lat, double[] lang) {
        TrackingRoute route = new TrackingRoute();
        ArrayList<TrackingLocation> location = new ArrayList<>();
        TrackingLocation trackingLocation;
        for (int i = 0; i < lat.length; i++) {
            trackingLocation = new TrackingLocation();
            trackingLocation.setLocationLatitude(lat[i] );
            trackingLocation.setLocationLongitude(lang[i]);
            location.add(i, trackingLocation);
        }
        route.setLocation(location);
        return route;
    }

    public SparseArray<TrackingRoute> getRouteDataFromDataBase(String routeId){
        return trackingModel.getRouteByGroupId(routeId);
    }

    public HashMap<String, String> getGroupListFromDataBase() {
        HashMap<String, String> groupInfoMap = new HashMap<>();
        List<GroupAbstract> memberGroupList = mAppUserModel.getApplicationUser().getModeratedGroups();
        for (GroupAbstract groupAbstract : memberGroupList) {
            groupInfoMap.put(groupAbstract.getObjectId(), groupAbstract.getName());
        }
        return groupInfoMap;
    }

    public ArrayList<GroupMember> getStudentListFromDataBase(String selectedGroupId) {
        final Group group = mGroupModel.getGroupFromUidSync(selectedGroupId);
        for (Moderator moderator :
                group.getModerators()
                ) {
            group.getMembers().remove(new GroupMember(moderator.getId()));
        }
        group.getMembers().removeAll(group.getModerators());
        return group.getMembers();
    }

    public Observable<String> traceRoute(final Location location, final String status, final String groupId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> sub) throws Exception {
                if (status != null) {
                    if (status.equalsIgnoreCase("start")) { // For start route
                        try {
                            sub.onNext(sendRouteStartNotificationData("start", location, groupId));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (status.equalsIgnoreCase("stop")) {// for stop the route
                        try {
                            sub.onNext(sendRouteStartNotificationData("stop", location, groupId));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        sub.onNext(sendRouteData(location, groupId));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String sendRouteStartNotificationData(String data, Location location, String groupId) throws IOException {
        String text = data + "#" + location.getLatitude() + "#" + location.getLongitude() + "#" + location.getAltitude() + "#" + location.getBearing();
//        Call<MessageResponse> call = mNetworkModel.sendNotificationForTrackingUsingFCM("mds_route", "Trip " + data, text);
        Call<MessageResponse> call = mNetworkModel.sendNotificationForTrackingUsingFCM(groupId, "Trip " + data, text);
        Response<MessageResponse> response = call.execute();
        if (response != null) {
            if (response.isSuccessful()) {
                if (response.body().getMessage() != null) {
                    return response.body().getMessage();
                }
            }
        }
        return "";
    }

    private String sendRouteData(Location location, String groupId) throws IOException {
        String start = "location#";
        String text = start + location.getLatitude() + "#" + location.getLongitude() + "#" + location.getAltitude() + "#" + location.getBearing();
//        Call<MessageResponse> call = mNetworkModel.sendDataForTrackingUsingFCM("mds_route", text);
        Call<MessageResponse> call = mNetworkModel.sendDataForTrackingUsingFCM(groupId, text);
        Response<MessageResponse> response = call.execute();
        if (response != null) {
            if (response.isSuccessful()) {
                return response.body().getMessage();
            }
        }
        return "";
    }

    private String createAndUploadPost(String groupIds, Location location) throws IOException {
        String text = "start#" + location.getLatitude() + "#" + location.getLongitude() + "#" + location.getAltitude() + "#" + location.getBearing();
        PostData postData = getPostData(groupIds, text);
        Call<PostData> call = mNetworkModel.postLearningNetworkPostData(postData);
        Response<PostData> response = call.execute();
        if (response != null && response.isSuccessful()) {
            return savePostData(response.body());
        } else if (response != null && response.code() == 401 || response.code() == 403) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                Call<PostData> call2 = call.clone();
                response = call2.execute();
                if (response != null && response.isSuccessful()) {
                    return savePostData(response.body());

                }
            }
        }
        return null;
    }

    public PostResponse getPostResponseById(String id) {
        return mPostDataModel.getPostResponseFromUidSync(id);
    }

    public PostData getPostDataById(String id) {
        return mPostDataModel.getPostDataFromUidSync(id);
    }

    private String createAndUploadPostResponse(String postId, String start, Location location) throws IOException {
        if (TextUtils.isEmpty(start)) {
            start = "location#";
        } else {
            start = start + "#";
        }
        String text = start + location.getLatitude() + "#" + location.getLongitude() + "#" + location.getAltitude() + "#" + location.getBearing();
        PostResponse postResponse = getPostResponse(postId, text);
        Call<PostResponse> call = mNetworkModel.uploadPostResponse(postResponse);
        Response<PostResponse> response = call.execute();
        if (response != null && response.isSuccessful()) {
            return savePostResponse(response.body());
        } else if (response != null && response.code() == 401 || response.code() == 403) {
            if (SyncServiceHelper.refreshToken(mContext)) {
                Call<PostResponse> call2 = call.clone();
                response = call2.execute();
                if (response != null && response.isSuccessful()) {
                    return savePostResponse(response.body());
                }
            }
        }
        return null;
    }

    private PostResponse getPostResponse(String postId, String text) {
        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getApplicationUser().getName());
        fromUser.setId(mAppUserModel.getObjectId());
        postResponse.setFrom(fromUser);

        postResponse.setObjectId(null);
        postResponse.setPostID(postId);
        postResponse.setResources(null);
        postResponse.setText(text);
        postResponse.setType(PostResponseType.TYPE_TRACKING.toString());
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUpdatedTime(new Date());

        postResponse.setAlias(GeneralUtils.generateAlias("PostResponse", fromUser.getId(), "" + System.currentTimeMillis()));
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());

        return postResponse;
    }

    private String savePostResponse(PostResponse body) {
        body.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
        mPostDataModel.savePostResponse(body);
        return body.getObjectId();
    }

    public Location getLocationFromPostText(String text) {
        Location location = new Location("");
        String[] locationData = text.split("#");
        location.setLatitude(Double.parseDouble(locationData[1]));
        location.setLongitude(Double.parseDouble(locationData[2]));
        location.setAltitude(Double.parseDouble(locationData[3]));
        location.setBearing(Float.parseFloat(locationData[4]));
        return location;
    }

    public PostData getPostData(String groupIds, String text) {
        PostData postData = new PostData();
        postData.setPostText(text);
        String nowString = DateUtils.getCurrentDateTime();
        postData.setObjectId(null);
        postData.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));

        postData.setFrom(getPostByUser());

        PostToGroup toGroup = new PostToGroup();
        toGroup.setId(groupIds);
        postData.setTo(toGroup);

        postData.setAlias(GeneralUtils.generateAlias("LNPostData", "" + mAppUserModel.getObjectId(), ""+System.currentTimeMillis()));
        postData.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        postData.setUnread(false);
        postData.setLastMessageTime(new Date());
        postData.setPostType(PostDataType.TYPE_TRACKING.getPostDataType());
        postData.setLastUpdationTime(new Date());
        return postData;
    }

    public String savePostData(PostData body) {
        body.setSyncStatus(SyncStatus.COMPLETE_SYNC.toString());
        body.setLastMessageTime(DateUtils.convertrIsoDate(body.getCreatedTime()));
        mPostDataModel.savePostData(body);
        return body.getObjectId();
    }

    public PostByUser getPostByUser() {
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getApplicationUser().getName());
        fromUser.setId(mAppUserModel.getObjectId());
        return fromUser;
    }

}
