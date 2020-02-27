package in.securelearning.lil.android.learningnetwork.model;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.constants.PostResponseType;
import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.AppUser;
import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.GroupAbstract;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.InternalNotification;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.PostByUser;
import in.securelearning.lil.android.base.dataobjects.PostData;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;
import in.securelearning.lil.android.base.dataobjects.PostResponse;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.UserProfile;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.model.DeleteObjectModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.InternalNotificationModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.model.PostModel;
import in.securelearning.lil.android.base.model.PostResponseModel;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.base.utils.DateUtils;
import in.securelearning.lil.android.base.utils.DocumentUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.permission.PermissionPrefsCommon;
import in.securelearning.lil.android.player.view.activity.PlayVideoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayVimeoFullScreenActivity;
import in.securelearning.lil.android.player.view.activity.PlayYouTubeFullScreenActivity;
import in.securelearning.lil.android.learningnetwork.events.EventLatestCommentAdded;
import in.securelearning.lil.android.learningnetwork.events.LoadGroupListEvent;
import in.securelearning.lil.android.learningnetwork.events.LoadPostCountsEvent;
import in.securelearning.lil.android.learningnetwork.events.NewPostResponseAdded;
import in.securelearning.lil.android.learningnetwork.events.PostRemovedFromFavorite;
import in.securelearning.lil.android.learningnetwork.views.activity.CreatePostActivity;
import in.securelearning.lil.android.syncadapter.service.MessageService;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;
import in.securelearning.lil.android.syncadapter.utils.OgUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.ACTION_TYPE_NETWORK_UPLOAD;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_POST;
import static in.securelearning.lil.android.syncadapter.utils.InternalNotificationActionUtils.OBJECT_TYPE_POST_RESPONSE;

/**
 * Created by Pushkar Raj on 9/1/2016.
 */
public class PostDataLearningModel extends BaseModelLearningNetwork {

    @Inject
    RxBus mRxBus;

    @Inject
    Context mAppContext;

    @Inject
    PostDataModel mPostDataModel;

    @Inject
    PostModel mPostModel;

    @Inject
    PostResponseModel mPostResponseModel;

    @Inject
    GroupModel mGroupModel;

    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;

    @Inject
    InternalNotificationModel mInternalNotificationModel;

    @Inject
    BadgesModel mBadgesModel;

    @Inject
    AppUserModel mAppUserModel;

    @Inject
    DeleteObjectModel mDeleteObjectModel;

    @Inject
    OgUtils mOgUtils;

    public PostDataLearningModel() {
        super();
        getLearningNetworkComponent().inject(this);
    }

    public void savePost(PostData postData) {
        postData = mPostModel.saveObject(postData);
        createInternalNotificationForPost(postData, ACTION_TYPE_NETWORK_UPLOAD);
    }

    public PostData savePostForOg(PostData postData) {
        return mPostModel.saveObject(postData);
    }

    public PostResponse savePostResponseForOg(PostResponse postResponse) {
        return mPostResponseModel.saveObject(postResponse);
    }


    public void fetchGroupsByUSerUId(String userUid) {

        Observable<ArrayList<Group>> fetchGroupsFromDb = mGroupModel.getGroupsByUserUId(userUid);
        fetchGroupsFromDb.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<Group>>() {
            @Override
            public void accept(ArrayList<Group> groups) {
                Collections.sort(groups, new in.securelearning.lil.android.learningnetwork.comparator.SortGroup.LastConversationTimeSorter());
                mRxBus.send(new LoadGroupListEvent(groups));
            }
        });
    }

    public void fetchPostCountsByUSerUId(String userUid) {
        Observable<ArrayList<Group>> fetchGroupsFromDb = mGroupModel.getGroupsByUserUId(userUid);
        fetchGroupsFromDb.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(new Consumer<ArrayList<Group>>() {
            @Override
            public void accept(ArrayList<Group> groups) {
                int postCounts = 0;
                for (Group group : groups) {
                    String count = mPostDataModel.fetchPostCountForGroupSync(group.getObjectId());
                    group.setUnreadRefeneceCount(Integer.parseInt(count.split("-")[0]));
                    group.setUnreadQueryCount(Integer.parseInt(count.split("-")[1]));
                    postCounts = postCounts + Integer.parseInt(count.split("-")[0]) + Integer.parseInt(count.split("-")[1]);
                }
                Collections.sort(groups, new in.securelearning.lil.android.learningnetwork.comparator.SortGroup.LastConversationTimeSorter());
                mRxBus.send(new LoadPostCountsEvent(postCounts));
            }
        });
    }

    public void deletePost(PostDataDetail postDataDetail) {
        for (Resource resource : postDataDetail.getPostData().getPostResources()) {
            FileUtils.deleteFile(resource.getDeviceURL());
        }
        for (PostResponse postResponse : postDataDetail.getPostResponses()) {
            mPostDataModel.deletePostResponse(postResponse.getDocId());
        }
        mPostDataModel.deletePost(postDataDetail.getPostData().getDocId());
    }


    /**
     * create internal notification for post upload
     *
     * @param postData
     * @param action
     */
    public void createInternalNotificationForPost(PostData postData, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, postData.getAlias());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(postData.getPostType());
            internalNotification.setObjectDocId(postData.getDocId());
            internalNotification.setObjectId(postData.getAlias());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_POST);
            internalNotification.setTitle("post");

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        MessageService.startActionFetchInternalNotification(mAppContext, internalNotification.getDocId());
    }

    /**
     * create internal notification for post response upload
     *
     * @param postResponse
     * @param action
     */
    private void createInternalNotificationForPostResponse(PostResponse postResponse, int action) {
        InternalNotification internalNotification = mInternalNotificationModel.getObjectByActionAndId(action, postResponse.getAlias());

        if (internalNotification != null && !TextUtils.isEmpty(internalNotification.getDocId())) {
            internalNotification.setObjectAction(action);
        } else {
            internalNotification = new InternalNotification();
            internalNotification.setObjectType(postResponse.getType());
            internalNotification.setObjectDocId(postResponse.getDocId());
            internalNotification.setObjectId(postResponse.getAlias());
            internalNotification.setObjectAction(action);
            internalNotification.setDataObjectType(OBJECT_TYPE_POST_RESPONSE);
            internalNotification.setTitle("post response");

        }
        internalNotification = mInternalNotificationModel.saveObject(internalNotification);
        MessageService.startActionFetchInternalNotification(mAppContext, internalNotification.getDocId());
    }

    /**
     * load posts for selected group
     */
    public Observable<ArrayList<PostData>> getPostListForGroup(final String groupId, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<PostData>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<PostData>> subscriber) {

                        ArrayList<PostData> posts = mPostModel.getPostListByGroupId(groupId, skip, limit);
                        subscriber.onNext(posts);
                        subscriber.onComplete();
                    }
                });

    }

    /**
     * load comments for selected post
     */
    public Observable<ArrayList<PostResponse>> getPostResponseListForPost(final String postId, final String responseType, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<PostResponse>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<PostResponse>> subscriber) {

                        ArrayList<PostResponse> responses = getPostResponseListForPostByPostIdAndType(postId, responseType, skip, limit);
                        subscriber.onNext(responses);
                        subscriber.onComplete();
                    }
                });

    }

    public ArrayList<PostResponse> getPostResponseListForPostByPostIdAndType(String postId, String responseType, int skip, int limit) {
        ArrayList<PostResponse> objectList = new ArrayList();
        try {
            Object[] startKey = null;
            Object[] endKey = null;
            Query query = mDatabaseQueryHelper.getPostResponseListByPostIdAndResponseTypeQuery();
            if (!TextUtils.isEmpty(responseType)) {
                startKey = new Object[]{postId, responseType};
                endKey = new Object[]{postId, responseType, Collections.EMPTY_MAP};
            } else {
                startKey = new Object[]{postId};
                endKey = new Object[]{postId, Collections.EMPTY_MAP};
            }
            query.setStartKey(endKey);
            query.setEndKey(startKey);
            query.setMapOnly(true);
            query.setDescending(true);
            query.setSkip(skip);
            query.setLimit(limit);

            QueryEnumerator queryRows = query.run();
            DocumentUtils documentUtils = new DocumentUtils();
            for (QueryRow queryRow : queryRows) {
                Document document = queryRow.getDocument();
                PostResponse object = GeneralUtils.getObjectFromMap(documentUtils.getObjectMapFromDocument(document), PostResponse.class);
                objectList.add(object);

            }

            documentUtils = null;
            queryRows = null;
            query = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return objectList;


    }

    /**
     * load favorite posts for selected group
     */
    public Observable<ArrayList<PostData>> getFavoritePostListForGroup(final String groupId, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<PostData>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<PostData>> subscriber) {

                        ArrayList<PostData> posts = mPostModel.getFavoritePostListGroupIdAndType(groupId, "", skip, limit);
                        subscriber.onNext(posts);
                        subscriber.onComplete();
                    }
                });

    }

    public Observable<ArrayList<PostData>> getNewPostListByGroupId(final String groupId, final int skip, final int limit) {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<PostData>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<PostData>> subscriber) {

                        ArrayList<PostData> posts = mPostModel.getNewPostListByGroupIdQuery(groupId, skip, limit);
                        subscriber.onNext(posts);
                        subscriber.onComplete();
                    }
                });

    }


    public ArrayList<LILBadges> getBadges() {

        return mBadgesModel.fetchLilBadgesListSync();
    }

    public PostResponse setPostResponseForBadge(PostData postData, LILBadges lilBadges) {
        PostResponse postResponse = new PostResponse();
        PostByUser fromUser = new PostByUser();
        fromUser.setName(mAppUserModel.getApplicationUser().getName());
        fromUser.setId(mAppUserModel.getObjectId());
        fromUser.setRole(mAppUserModel.getApplicationUser().getRole().getName());
        postResponse.setFrom(fromUser);
        postResponse.setTo(postData.getTo());
        postResponse.setObjectId(null);
        postResponse.setAlias(GeneralUtils.generateAlias("LNPostResponse", "" + mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
        postResponse.setPostID(postData.getObjectId());
        postResponse.setResources(new ArrayList<String>());
        postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
        postResponse.setText("");
        postResponse.setType(PostResponseType.TYPE_BADGE.getPostResponseType());//Badge type etc
        postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
        postResponse.setUnread(false);
        postResponse.setUpdatedTime(new Date());
        postResponse.setAssignedPostResponseId(null);
        postResponse.setAssignedBadgeId(lilBadges.getObjectId());
        postResponse.setGroupId(postData.getTo().getId());
        mPostResponseModel.saveObject(postResponse);
        createInternalNotificationForPostResponse(postResponse, ACTION_TYPE_NETWORK_UPLOAD);
        return postResponse;
    }

    @SuppressLint("CheckResult")
    public void setPostResponseForLike(final PostData postData) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {

                PostResponse postResponse = new PostResponse();
                PostByUser fromUser = new PostByUser();
                fromUser.setName(mAppUserModel.getApplicationUser().getName());
                fromUser.setId(mAppUserModel.getObjectId());
                fromUser.setRole(mAppUserModel.getApplicationUser().getRole().getName());
                postResponse.setFrom(fromUser);
                postResponse.setTo(postData.getTo());
                postResponse.setObjectId(null);
                postResponse.setAlias(GeneralUtils.generateAlias("LNPostResponse", "" + mAppUserModel.getObjectId(), "" + System.currentTimeMillis()));
                postResponse.setPostID(postData.getObjectId());
                postResponse.setResources(new ArrayList<String>());
                postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
                postResponse.setText("");
                postResponse.setType(PostResponseType.TYPE_RECOMMEND.getPostResponseType());
                postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
                postResponse.setUnread(false);
                postResponse.setUpdatedTime(new Date());
                postResponse.setAssignedPostResponseId(null);
                postResponse.setAssignedBadgeId(null);
                postResponse.setGroupId(postData.getTo().getId());

                mPostResponseModel.saveObject(postResponse);
                createInternalNotificationForPostResponse(postResponse, ACTION_TYPE_NETWORK_UPLOAD);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    @SuppressLint("CheckResult")
    public void setPostResponseForComment(final PostData postData, final String postResponseText) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {

                PostResponse postResponse = new PostResponse();
                PostByUser fromUser = new PostByUser();
                fromUser.setName(mAppUserModel.getApplicationUser().getName());
                fromUser.setId(mAppUserModel.getObjectId());
                fromUser.setRole(mAppUserModel.getApplicationUser().getRole().getName());
                postResponse.setFrom(fromUser);
                postResponse.setTo(postData.getTo());
                postResponse.setObjectId(null);
                postResponse.setPostID(postData.getObjectId());
                postResponse.setResources(null);
                postResponse.setText(postResponseText);
                postResponse.setType(PostResponseType.TYPE_COMMENT.getPostResponseType());
                postResponse.setCreatedTime(DateUtils.getISO8601DateStringFromDate(new Date()));
                postResponse.setUpdatedTime(new Date());
                postResponse.setGroupId(postData.getTo().getId());
                postResponse.setAlias(GeneralUtils.generateAlias("PostResponse", fromUser.getId(), "" + System.currentTimeMillis()));
                postResponse.setSyncStatus(SyncStatus.NOT_SYNC.toString());
                postResponse.setoGDataList(CreatePostActivity.extractUrls(postResponseText));

                postResponse = mPostResponseModel.saveObject(postResponse);
                createInternalNotificationForPostResponse(postResponse, ACTION_TYPE_NETWORK_UPLOAD);
                mRxBus.send(new NewPostResponseAdded());
                mRxBus.send(new EventLatestCommentAdded(postData.getAlias()));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    private String setUserRole() {
        String strRole = "";
        if (PermissionPrefsCommon.getPostBadgeAssignPermission(mAppContext)) {
            strRole = AppUser.USERTYPE.TEACHER.toString();
        } else {
            strRole = AppUser.USERTYPE.STUDENT.toString();
        }
        return strRole;
    }

    public void addPostToFavorite(PostData postData) {
        postData.setDocId("");
        mPostModel.saveFavoritePostObject(postData);
    }

    public void removePostFromFavorite(PostData postData) {
        PostData data = mPostModel.getFavoritePostByAlias(postData.getAlias());
        mDeleteObjectModel.deleteJsonLearningNetwork(data.getDocId());
        mRxBus.send(new PostRemovedFromFavorite(data.getAlias()));
    }

    public Observable<PostResponse> getLatestCommentOnPost(final String postId) {
        return
                Observable.create(new ObservableOnSubscribe<PostResponse>() {
                    @Override
                    public void subscribe(ObservableEmitter<PostResponse> subscriber) {
                        PostResponse postResponse = mPostResponseModel.getLatestPostResponseByPostIdAndType(postId, PostResponseType.TYPE_COMMENT.getPostResponseType());
                        if (postResponse != null) {
                            subscriber.onNext(postResponse);
                        }
                        subscriber.onComplete();
                    }
                });


    }

    public Observable<PostData> getPostDataByAlias(final String postAlias) {
        return
                Observable.create(new ObservableOnSubscribe<PostData>() {
                    @Override
                    public void subscribe(ObservableEmitter<PostData> subscriber) {
                        PostData postData = mPostModel.getPostDataByAlias(postAlias);
                        if (postData != null) {
                            subscriber.onNext(postData);
                        }
                        subscriber.onComplete();
                    }
                });


    }

    public PostData getPostDataByObjectId(String objectId) {
        return mPostModel.getObjectById(objectId);
    }

    /**
     * load posts for selected group
     */
    public Observable<ArrayList<Group>> getGroupListForUser() {
        return
                Observable.create(new ObservableOnSubscribe<ArrayList<Group>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<Group>> subscriber) {

                        ArrayList<Group> groups = mGroupModel.getAllGroups();
                        ArrayList<Group> userGroups = new ArrayList<Group>();
                        for (int i = 0; i < groups.size(); i++) {
                            Group group = groups.get(i);
                            for (GroupMember groupMember : group.getMembers()) {
                                if (groupMember.getObjectId().equals(mAppUserModel.getObjectId())) {
                                    userGroups.add(group);

                                }

                            }

                        }

                        subscriber.onNext(userGroups);
                        subscriber.onComplete();
                    }
                });

    }

    public static List<GroupAbstract> getDistinctGroups(List<GroupAbstract> groupAbstractList1, List<GroupAbstract> groupAbstractList2) {

        HashMap<String, GroupAbstract> groupAbstractListNew = new HashMap<>();
        for (GroupAbstract groupAbstract :
                groupAbstractList1) {
            if (groupAbstract != null)
                groupAbstractListNew.put(groupAbstract.getObjectId(), groupAbstract);
        }
        for (GroupAbstract groupAbstract :
                groupAbstractList2) {
            if (groupAbstract != null)
                groupAbstractListNew.put(groupAbstract.getObjectId(), groupAbstract);
        }
        return new java.util.ArrayList<>(groupAbstractListNew.values());
    }

    /**
     * get one by one group
     */
    public Observable<Group> getGroupForUser() {
        return
                Observable.create(new ObservableOnSubscribe<Group>() {
                    @Override
                    public void subscribe(ObservableEmitter<Group> subscriber) {
                        UserProfile userProfile = mAppUserModel.getApplicationUser();
                        HashSet<String> groupAbstractListNew = new HashSet<String>();
                        for (GroupAbstract groupAbstract :
                                userProfile.getMemberGroups()) {
                            if (groupAbstract != null && !TextUtils.isEmpty(groupAbstract.getObjectId())) {
                                String groupId = groupAbstract.getObjectId();
                                if (!groupAbstractListNew.contains(groupId)) {

                                    groupAbstractListNew.add(groupId);
                                    Group group = mGroupModel.getGroupFromUidSync(groupId);
                                    if (group != null && !TextUtils.isEmpty(group.getObjectId())) {
                                        PostData postData = mPostModel.getLatestPostForGroup(groupId);
                                        if (postData != null && !TextUtils.isEmpty(postData.getAlias())) {
                                            group.setLastMessageTime(DateUtils.convertrIsoDate(postData.getCreatedTime()));
                                            group.setLastPostText(postData.getPostText());
                                            group.setLastPostPostedBy(postData.getFrom().getName());
                                            group.setPostData(postData);
                                        }

                                        subscriber.onNext(group);
                                    }
                                }


                            }
                        }
                        for (GroupAbstract groupAbstract :
                                userProfile.getModeratedGroups()) {
                            if (groupAbstract != null && !TextUtils.isEmpty(groupAbstract.getObjectId())) {
                                String groupId = groupAbstract.getObjectId();
                                if (!groupAbstractListNew.contains(groupId)) {

                                    groupAbstractListNew.add(groupId);
                                    Group group = mGroupModel.getGroupFromUidSync(groupId);
                                    if (group != null && !TextUtils.isEmpty(group.getObjectId())) {
                                        PostData postData = mPostModel.getLatestPostForGroup(groupId);
                                        if (postData != null && !TextUtils.isEmpty(postData.getAlias())) {
                                            group.setLastMessageTime(DateUtils.convertrIsoDate(postData.getCreatedTime()));
                                            group.setLastPostText(postData.getPostText());
                                            group.setLastPostPostedBy(postData.getFrom().getName());
                                            group.setPostData(postData);
                                        }

                                        subscriber.onNext(group);
                                    }
                                }


                            }
                        }

                        subscriber.onComplete();
                    }
                });


    }

    private List<GroupAbstract> removeDuplicateGroup(List<GroupAbstract> groupAbstractList) {

        List<GroupAbstract> groupAbstractListNew = new in.securelearning.lil.android.base.utils.ArrayList<>();
        for (GroupAbstract groupAbstract : groupAbstractList) {
            boolean isFound = false;
            for (GroupAbstract groupAbstractNew : groupAbstractListNew) {
                if (groupAbstractNew.getObjectId().equals(groupAbstract.getObjectId()) || groupAbstractNew.equals(groupAbstract)) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                groupAbstractListNew.add(groupAbstract);

            }
        }
        return groupAbstractListNew;
    }

    /**
     * delete post from doc type new if already exist
     */
    public void deleteNewPostIfAlreadyExist(final String objectId) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                PostData post = mPostModel.getNewObjectById(objectId);

                if (post.getObjectId().equals(objectId)) {
                    mDeleteObjectModel.deleteJsonLearningNetwork(post.getDocId());
                }

            }
        });

    }

    /**
     * delete post response from doc type new if already exist
     */
    public void deleteNewPostResponseIfAlreadyExist(final String objectId) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                PostResponse postResponse = mPostResponseModel.getNewObjectById(objectId);

                if (postResponse.getObjectId().equals(objectId)) {
                    mDeleteObjectModel.deleteJsonLearningNetwork(postResponse.getDocId());
                }

            }
        });
    }

    public Observable<Integer> getUnreadPostCountForGroup(final String groupId) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(mPostModel.getNewPostCountByGroupId(groupId));
                e.onComplete();
            }
        });
    }

    public Observable<PostResponse> getLatestBadgeOnPost(final String postId) {
        return
                Observable.create(new ObservableOnSubscribe<PostResponse>() {
                    @Override
                    public void subscribe(ObservableEmitter<PostResponse> subscriber) {
                        PostResponse postResponse = mPostResponseModel.getLatestPostResponseByPostIdAndType(postId, PostResponseType.TYPE_BADGE.getPostResponseType());
                        if (postResponse != null) {
                            subscriber.onNext(postResponse);
                        }
                        subscriber.onComplete();
                    }
                });


    }

    public Observable<Integer> getPostResponseCountByPostIdAndResponseType(final String postId, final String postResponseType) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(mPostResponseModel.getPostResponseCountByPostIdAndType(postId, postResponseType));
                e.onComplete();
            }
        });
    }

    public Observable<Integer> getNewCountOfPostByPostType(final String postType) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(mPostModel.getNewPostCountByPostType(postType));
                e.onComplete();
            }
        });
    }

    public Observable<Boolean> isPostLikedByUser(final String postId, final String responseType) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                int isLiked = mPostResponseModel.isPostResponseByUser(postId, responseType, mAppUserModel.getObjectId());
                if (isLiked > 0) {
                    e.onNext(true);
                } else {
                    e.onNext(false);

                }
                e.onComplete();
            }
        });
    }

    public Observable<Boolean> isPostFavoriteByUser(final String postId, final String alias) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                int isFavorite = 0;
                if (!TextUtils.isEmpty(postId)) {
                    isFavorite = mPostModel.isPostFavoriteByUserPostIdQuery(postId);

                } else {
                    isFavorite = mPostModel.isPostFavoriteByUserAliasQuery(alias);

                }
                if (isFavorite > 0) {
                    e.onNext(true);
                } else {
                    e.onNext(false);

                }
                e.onComplete();
            }
        });
    }

    public void deleteAllNewPostByGroupId(final String groupId) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                mPostModel.deleteAllNewPostByGroupId(groupId);

            }
        });
    }

    public void deleteAllNewPostResponseByPostId(final String postId) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                mPostResponseModel.deleteAllNewPostResponseByPostId(postId);

            }
        });
    }

    public void clearLearningNetworkGroupNotification(Context context, int groupHashCode) {
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(groupHashCode);
    }

    /*To play video*/
    public void playVideo(Resource resource) {

        String type = resource.getType();

        String url = "";
        if (!TextUtils.isEmpty(resource.getUrl())) {
            url = resource.getUrl();
        } else if (!TextUtils.isEmpty(resource.getUrlMain())) {
            url = resource.getUrlMain();
        } else if (!TextUtils.isEmpty(resource.getSourceURL())) {
            url = resource.getSourceURL();
        }
        if (TextUtils.isEmpty(type)) {
            if (url.contains(mAppContext.getString(R.string.typeVimeoVideo))) {
                type = mAppContext.getString(R.string.typeVimeoVideo);
            } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                type = mAppContext.getString(R.string.typeYouTubeVideo);
            } else if (url.contains(mAppContext.getString(R.string.typeVideo))) {
                type = mAppContext.getString(R.string.typeVideo);
            } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                type = mAppContext.getString(R.string.typeYouTubeVideo);
            } else {
                if (url.contains(mAppContext.getString(R.string.typeVimeoVideo))) {
                    type = mAppContext.getString(R.string.typeVimeoVideo);
                } else if (url.matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")) {
                    type = mAppContext.getString(R.string.typeYouTubeVideo);
                } else if (url.contains(mAppContext.getString(R.string.typeVideo))) {
                    type = mAppContext.getString(R.string.typeVideo);
                } else if (url.contains("youtu.be") || url.contains("youtube.com")) {
                    type = mAppContext.getString(R.string.typeYouTubeVideo);
                } else {
                    type = mAppContext.getString(R.string.typeVideo);
                }

            }
        }

        if (type.equalsIgnoreCase(mAppContext.getString(R.string.typeVideo))) {
            Resource item = new Resource();
            item.setType(mAppContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mAppContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mAppContext, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        } else if (type.equalsIgnoreCase(mAppContext.getString(R.string.typeYouTubeVideo))) {
            if (!url.contains("https:") && !url.startsWith("www")) {
                FavouriteResource favouriteResource = new FavouriteResource();
                favouriteResource.setName(url);
                favouriteResource.setUrlThumbnail("");
                mAppContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mAppContext, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
            } else {
                String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                Pattern compiledPattern = Pattern.compile(pattern);
                Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
                if (matcher.find()) {
                    String videoId = matcher.group();
                    FavouriteResource favouriteResource = new FavouriteResource();
                    favouriteResource.setName(videoId);
                    favouriteResource.setUrlThumbnail("");
                    mAppContext.startActivity(PlayYouTubeFullScreenActivity.getStartIntent(mAppContext, ConstantUtil.BLANK, ConstantUtil.BLANK, favouriteResource));
                }
            }


        } else if (type.equalsIgnoreCase(mAppContext.getString(R.string.typeVimeoVideo))) {
            mAppContext.startActivity(PlayVimeoFullScreenActivity.getStartIntent(mAppContext, ConstantUtil.BLANK, ConstantUtil.BLANK, ConstantUtil.BLANK, url));
        } else {
            Resource item = new Resource();
            item.setType(mAppContext.getString(R.string.typeVideo));
            item.setUrlMain(url);
            mAppContext.startActivity(PlayVideoFullScreenActivity.getStartActivityIntent(mAppContext, ConstantUtil.BLANK, ConstantUtil.BLANK, PlayVideoFullScreenActivity.NETWORK_TYPE_ONLINE, (Resource) item));
        }
    }


}
