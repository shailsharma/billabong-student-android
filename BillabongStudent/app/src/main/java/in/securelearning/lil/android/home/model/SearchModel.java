package in.securelearning.lil.android.home.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.dataobjects.BlogDetails;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.Course;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.Group;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Quiz;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.AssignmentModel;
import in.securelearning.lil.android.base.model.AssignmentResponseModel;
import in.securelearning.lil.android.base.model.BaseModel;
import in.securelearning.lil.android.base.model.BlogModel;
import in.securelearning.lil.android.base.model.ConceptMapModel;
import in.securelearning.lil.android.base.model.DigitalBookModel;
import in.securelearning.lil.android.base.model.GroupModel;
import in.securelearning.lil.android.base.model.InteractiveImageModel;
import in.securelearning.lil.android.base.model.PopUpsModel;
import in.securelearning.lil.android.base.model.PostDataModel;
import in.securelearning.lil.android.base.model.QuizModel;
import in.securelearning.lil.android.base.model.ResourceModel;
import in.securelearning.lil.android.base.model.VideoCourseModel;
import in.securelearning.lil.android.base.utils.ToastUtils;
import in.securelearning.lil.android.home.InjectorHome;
import in.securelearning.lil.android.home.dataobjects.CategorySelection;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourcesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResults;
import in.securelearning.lil.android.syncadapter.ftp.FtpFunctions;
import in.securelearning.lil.android.syncadapter.job.JobCreator;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncService;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.syncadapter.utils.SnackBarUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Prabodh Dhabaria on 16-11-2016.
 */
public class SearchModel extends BaseModel {
    public static final String CATEGORY_K12 = "k12";
    public static final String CATEGORY_HIGHER_STUDIES = "higher-studies";
    public static final String CATEGORY_AVOCATIONAL = "avocational";
    public static final String CATEGORY_CORPORATE = "corporate";

    @Inject
    FtpFunctions mFtpFunctions;
    @Inject
    Context mContext;
    @Inject
    AssignmentModel mAssignmentModel;
    @Inject
    DigitalBookModel mDigitalBookModel;
    @Inject
    InteractiveImageModel mInteractiveImageModel;
    @Inject
    ConceptMapModel mConceptMapModel;
    @Inject
    PopUpsModel mPopUpsModel;
    @Inject
    VideoCourseModel mVideoCourseModel;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    ResourceModel mResourceModel;
    @Inject
    AssignmentResponseModel mAssignmentResponseModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    QuizModel mQuizModel;
    @Inject
    BlogModel mBlogModel;

    @Inject
    GroupModel mGroupModel;
    @Inject
    PostDataModel mPostDataModel;

    public SearchModel() {
        InjectorHome.INSTANCE.getComponent().inject(this);
    }

    private boolean doesObjectMatchSearchCriteria(BaseDataObject object, String query, CategorySelection categorySelection) {
        query = query.toLowerCase();
        boolean isCategoryMatch = false;
        boolean shouldCategoryMatch = false;
        if (object instanceof Course && categorySelection.getCategory() != null && !TextUtils.isEmpty(categorySelection.getCategory().getId())) {
            if (((Course) object).getMetaInformation().getSubject().getId().equalsIgnoreCase(categorySelection.getCategory().getId())) {
                isCategoryMatch = true;
            }
// if (categorySelection.getCategory().getSearchTag().equalsIgnoreCase(CATEGORY_K12)) {
//                if (((Course) object).getMetaInformation().getGrade().getName().equalsIgnoreCase(categorySelection.getSubCategory().getSearchTag())
//                        && ((Course) object).getMetaInformation().getSubject().getName().equalsIgnoreCase(categorySelection.getSubSubCategory().getSearchTag())) {
//                    isCategoryMatch = true;
//                }
//            }
//            if (categorySelection.getCategory().getSearchTag().equalsIgnoreCase(CATEGORY_CORPORATE)) {
//                if (((Course) object).getMetaInformation().getDomain().getName().equalsIgnoreCase(categorySelection.getSubCategory().getSearchTag())
//                        && ((Course) object).getMetaInformation().getSpeciality().getName().equalsIgnoreCase(categorySelection.getSubSubCategory().getSearchTag())) {
//                    isCategoryMatch = true;
//                }
//            }
//            if (categorySelection.getCategory().getSearchTag().equalsIgnoreCase(CATEGORY_HIGHER_STUDIES)) {
//                if (((Course) object).getMetaInformation().getStream().getName().equalsIgnoreCase(categorySelection.getSubCategory().getSearchTag())
//                        && ((Course) object).getMetaInformation().getSubject().getName().equalsIgnoreCase(categorySelection.getSubSubCategory().getSearchTag())) {
//                    isCategoryMatch = true;
//                }
//            }
//            if (categorySelection.getCategory().getSearchTag().equalsIgnoreCase(CATEGORY_AVOCATIONAL)) {
//                if (((Course) object).getMetaInformation().getDomain().getName().equalsIgnoreCase(categorySelection.getSubCategory().getSearchTag())
//                        && ((Course) object).getMetaInformation().getSpeciality().getName().equalsIgnoreCase(categorySelection.getSubSubCategory().getSearchTag())) {
//                    isCategoryMatch = true;
//                }
//            }
            shouldCategoryMatch = true;
        }
        if (!(shouldCategoryMatch && !isCategoryMatch)) {
            if (!query.isEmpty()) {

                if (((Course) object).getSearchableText().contains(query)) {
                    return true;
                }
            } else return true;
        }
        return false;
    }

    public ArrayList<BaseDataObject> searchForCourses(String query, CategorySelection categorySelection) {
        query = query.toLowerCase();
        ArrayList<BaseDataObject> list = new ArrayList<>();
        if (query.isEmpty() && categorySelection.getCategory() == null) {
        } else {
            for (DigitalBook object :
                    mDigitalBookModel.getDigitalBookList()) {
                if (doesObjectMatchSearchCriteria(object, query, categorySelection)) {
                    list.add(object);
                }
            }
            for (PopUps object :
                    mPopUpsModel.getPopUpsList()) {
                if (doesObjectMatchSearchCriteria(object, query, categorySelection)) {
                    list.add(object);
                }
            }
            for (InteractiveImage object :
                    mInteractiveImageModel.getInteractiveImageList()) {
                if (doesObjectMatchSearchCriteria(object, query, categorySelection)) {
                    list.add(object);
                }
            }
            for (ConceptMap object :
                    mConceptMapModel.getConceptMapList()) {
                if (doesObjectMatchSearchCriteria(object, query, categorySelection)) {
                    list.add(object);
                }

            }
            for (VideoCourse object :
                    mVideoCourseModel.getCompleteList()) {
                if (doesObjectMatchSearchCriteria(object, query, categorySelection)) {
                    list.add(object);
                }

            }
        }

        return list;
    }

    public ArrayList<BaseDataObject> searchForAssignments(String query) {
        query = query.toLowerCase();
        ArrayList<BaseDataObject> list = new ArrayList<>();
        for (Assignment assignment :
                mAssignmentModel.getAssignmentList()) {

            if (assignment.getTitle().toLowerCase().contains(query) ||
                    assignment.getMetaInformation().getTopic().getName().toLowerCase().contains(query) ||
                    assignment.getMetaInformation().getSubject().getName().toLowerCase().contains(query)) {
                if (assignment.getAssignedBy().getId().equals(mAppUserModel.getObjectId()))
                    list.add(assignment);
            }

        }
        return list;
    }

    public ArrayList<BaseDataObject> searchForBlogs(String query) {
        query = query.toLowerCase();
        ArrayList<BaseDataObject> list = new ArrayList<>();
        if (!query.isEmpty()) {
            for (BlogDetails blogDetails :
                    mBlogModel.getBlogsDetailsesListSync()) {

                if (blogDetails.getBlogInstance().getTitle().toLowerCase().contains(query) || blogDetails.getBlogInstance().getDescription().toLowerCase().contains(query)) {
                    list.add(blogDetails);
                }

            }
        }
        return list;
    }

    public ArrayList<BaseDataObject> searchOnlineForAssignments(String query) {
        ArrayList<BaseDataObject> list = new ArrayList<>();
        return list;
    }

    public ArrayList<BaseDataObject> searchOnlineForCourses(String query, CategorySelection categorySelection) {


        String category = "";
        String level1 = "";
        String level2 = "";

        if (categorySelection.getSubSubCategory() != null)
            level2 = categorySelection.getSubSubCategory().getSearchTag();
        if (categorySelection.getSubCategory() != null)
            level1 = categorySelection.getSubCategory().getSearchTag();
        if (categorySelection.getCategory() != null)
            category = categorySelection.getCategory().getId();

        ArrayList<BaseDataObject> list = new ArrayList<>();

        Call<ArrayList<AboutCourse>> call = mNetworkModel.searchForCourseOnline(query, categorySelection.getSubjectIds(), level1, level2);
        try {
            Response<ArrayList<AboutCourse>> response = call.execute();

            if (response.isSuccessful()) {
                list.addAll(response.body());
            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        list.addAll(response.body());
                    }
                } else {
                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }

    public SearchCoursesResults searchOnlineForCoursesEs(Context context, View view, String query, CategorySelection categorySelection, int skip, int limit) {

        //ArrayList<SearchResults> list = new ArrayList<>();
        SearchCoursesResults searchResults = new SearchCoursesResults();
        Call<SearchCoursesResults> call = null;
        if (categorySelection.getInnerLearningLevelIds() != null && categorySelection.getInnerLearningLevelIds().size() > 0) {
            call = mNetworkModel.searchForCourseOnlineEs(query, categorySelection.getSubjectIds(), categorySelection.getLearningLevelIds(), categorySelection.getLanguageIds(), categorySelection.getCourseType(), categorySelection.getInnerLearningLevelIds(), skip, limit);
        } else {
            call = mNetworkModel.searchForCourseOnlineEs(query, categorySelection.getSubjectIds(), categorySelection.getLearningLevelIds(), categorySelection.getLanguageIds(), categorySelection.getCourseType(), skip, limit);
        }

        try {
            Response<SearchCoursesResults> response = call.execute();

            if (response.isSuccessful()) {
                //list.addAll(response.body().getList());
                searchResults = response.body();
            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        //list.addAll(response.body().getList());
                        searchResults = response.body();
                    }
                } else {
                    SnackBarUtils.showColoredSnackBar(context, view, "Could not connect to the server", ContextCompat.getColor(context, in.securelearning.lil.android.base.R.color.colorRed));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return searchResults;
    }

    public ArrayList<BaseDataObject> searchOnlineForCoursesBySubjectEs(CategorySelection categorySelection) {

        ArrayList<BaseDataObject> list = new ArrayList<>();

        Call<SearchResults> call = mNetworkModel.searchForCourseOnlineBySubjectEs(categorySelection.getSubjectIds().get(0), mAppUserModel.getApplicationUser().getAssociation().getId());
        try {
            Response<SearchResults> response = call.execute();

            if (response.isSuccessful()) {
                list.addAll(response.body().getList());
            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        list.addAll(response.body().getList());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }
// public ArrayList<BaseDataObject> searchOnlineForCourses(String query, CategorySelection categorySelection) {
//
//
//        String category = "";
//        String level1 = "";
//        String level2 = "";
//
//        if (categorySelection.getSubSubCategory() != null)
//            level2 = categorySelection.getSubSubCategory().getSearchTag();
//        if (categorySelection.getSubCategory() != null)
//            level1 = categorySelection.getSubCategory().getSearchTag();
//        if (categorySelection.getCategory() != null)
//            category = categorySelection.getCategory().getSearchTag();
//
//        ArrayList<BaseDataObject> list = new ArrayList<>();
//        try {
//            Log.e("res", "search online");
//
//            Call<ArrayList<DigitalBook>> call = mNetworkModel.searchForDigitalBookOnline(query, category, level1, level2);
//            try {
//                Response<ArrayList<DigitalBook>> response = call.execute();
//
//                if (response.isSuccessful()) {
//                    list.addAll(response.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                } else if (response.code() == 401) {
//                    if (SyncServiceHelper.refreshToken(mContext)) {
//                        response = call.clone().execute();
//                        if (response.isSuccessful()) {
//                            list.addAll(response.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                        }
//                    } else {
//                        ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
//                    }
//                }
//            } catch (Exception e) {
//
//            }
//
//            try {
//                Call<ArrayList<PopUps>> call2 = mNetworkModel.searchForPopUpOnline(query, category, level1, level2);
//                Response<ArrayList<PopUps>> response2 = call2.execute();
//                if (response2.isSuccessful()) {
//                    list.addAll(response2.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                } else if (response2.code() == 401) {
//                    if (SyncServiceHelper.refreshToken(mContext)) {
//                        response2 = call2.clone().execute();
//                        if (response2.isSuccessful()) {
//                            list.addAll(response2.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                        }
//                    } else {
//                        ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                Call<ArrayList<ConceptMap>> call3 = mNetworkModel.searchForConceptMapOnline(query, category, level1, level2);
//                Response<ArrayList<ConceptMap>> response3 = call3.execute();
//                if (response3.isSuccessful()) {
//                    list.addAll(response3.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                } else if (response3.code() == 401) {
//                    if (SyncServiceHelper.refreshToken(mContext)) {
//                        response3 = call3.clone().execute();
//                        if (response3.isSuccessful()) {
//                            list.addAll(response3.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                        }
//                    } else {
//                        ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                Call<ArrayList<InteractiveImage>> call4 = mNetworkModel.searchForInteractiveImageOnline(query, category, level1, level2);
//                Response<ArrayList<InteractiveImage>> response4 = call4.execute();
//                if (response4.isSuccessful()) {
//                    list.addAll(response4.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                } else if (response4.code() == 401) {
//                    if (SyncServiceHelper.refreshToken(mContext)) {
//                        response4 = call4.clone().execute();
//                        if (response4.isSuccessful()) {
//                            list.addAll(response4.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                        }
//                    } else {
//                        ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//
//                Call<ArrayList<VideoCourse>> call5 = mNetworkModel.searchForVideoCourseOnline(query, category, level1, level2);
//                Response<ArrayList<VideoCourse>> response5 = call5.execute();
//                if (response5.isSuccessful()) {
//                    list.addAll(response5.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                } else if (response5.code() == 401) {
//                    if (SyncServiceHelper.refreshToken(mContext)) {
//                        response5 = call5.clone().execute();
//                        if (response5.isSuccessful()) {
//                            list.addAll(response5.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                        }
//                    } else {
//                        ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//
//                Call<ArrayList<InteractiveVideo>> call6 = mNetworkModel.searchForInteractiveVideoOnline(query, category, level1, level2);
//                Response<ArrayList<InteractiveVideo>> response6 = call6.execute();
//                if (response6.isSuccessful()) {
//                    list.addAll(response6.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                } else if (response6.code() == 401) {
//                    if (SyncServiceHelper.refreshToken(mContext)) {
//                        response6 = call6.clone().execute();
//                        if (response6.isSuccessful()) {
//                            list.addAll(response6.body());
////                for (Course course :
////                        response.body()) {
////                        list.add(course);
////                }
//                        }
//                    } else {
//                        ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }

    public ArrayList<BaseDataObject> searchOnlineForMicroCourses(String query) {
        ArrayList<BaseDataObject> list = new ArrayList<>();
        return list;
    }

    private ArrayList<BaseDataObject> getSampleDataCourses() {
        ArrayList<BaseDataObject> list = new ArrayList<>();
        DigitalBook book = new DigitalBook();
        book.setTitle("title1");
        book.setObjectId("sdsdsd");
        list.add(book);
        book = new DigitalBook();
        book.setTitle("title2");
        book.setObjectId("shwrgrgwes");
        list.add(book);
        book = new DigitalBook();
        book.setTitle("title3");
        book.setObjectId("ssdgbasf");
        list.add(book);
        book = new DigitalBook();
        book.setTitle("title4");
        book.setObjectId("sdgfadgas");
        list.add(book);
        return list;
    }

    public ArrayList<BaseDataObject> searchForAssignmentResponses(String query) {
        query = query.toLowerCase();
        ArrayList<BaseDataObject> list = new ArrayList<>();
        for (AssignmentResponse assignmentResponse :
                mAssignmentResponseModel.getAssignmentResponseList()) {

            if (assignmentResponse.getAssignmentTitle().toLowerCase().contains(query) ||
                    assignmentResponse.getMetaInformation().getTopic().getName().toLowerCase().contains(query) ||
                    assignmentResponse.getMetaInformation().getSubject().getName().toLowerCase().contains(query)) {
                if (assignmentResponse.getSubmittedBy().getObjectId().equals(mAppUserModel.getObjectId()))
                    list.add(assignmentResponse);
            }

        }
        return list;
    }

    public ArrayList<BaseDataObject> searchForResources(String query) {
        query = query.toLowerCase();
        ArrayList<BaseDataObject> list = new ArrayList<>();
        for (Resource resource :
                mResourceModel.getResourceListSync()) {

            if (resource.getTitle().toLowerCase().contains(query) ||
                    resource.getCaption().toLowerCase().contains(query) ||
                    resource.getName().toLowerCase().contains(query) ||
                    resource.getUrlMain().toLowerCase().contains(query) ||
                    resource.getObjectId().toLowerCase().contains(query)) {
                list.add(resource);
            }

        }
        return list;
    }

    public ArrayList<BaseDataObject> searchForGroupsPost(String query) {
        query = query.toLowerCase();
        ArrayList<BaseDataObject> list = new ArrayList<>();
        List<Group> mGroups = new ArrayList<>();
        for (Group group : mGroupModel.getGroupListByUserUIdSync(mAppUserModel.getObjectId())) {

            if (group.getGroupName().toLowerCase().contains(query) ||
                    group.getPurpose().toLowerCase().contains(query)
                    ) {
                mGroups.add(group);
            }

        }
//        for (PostData postData : mPostDataModel.fetchAllPostDataBySync(mAppUserModel.getObjectId())) {
//            if (group.getGroupName().toLowerCase().contains(query) ||
//                    group.getPurpose().toLowerCase().contains(query)) {
//                mGroups.add(group);
//            }
//        }

        return list;
    }

    public ArrayList<BaseDataObject> searchOnlineForResources(String query) {
        ArrayList<BaseDataObject> list = new ArrayList<>();
        try {
            Log.e("res", "search online");

            Call call = mNetworkModel.searchForResourcesOnline(query);
            Response<ArrayList<Resource>> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                for (Resource resource :
                        response.body()) {
                    if (resource.getType().toLowerCase().equals("image") || resource.getType().toLowerCase().equals("video"))
                        list.add(resource);
                }
            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
//                        list.addAll(response.body());
                        for (Resource resource :
                                response.body()) {
                            if (resource.getType().toLowerCase().equals("image") || resource.getType().toLowerCase().equals("video"))
                                list.add(resource);
                        }
                    }
                } else {
                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public SearchResourcesResults searchOnlineForResourcesEs(String query, CategorySelection categorySelection, int skip, int limit) {
        SearchResourcesResults results = new SearchResourcesResults();
        try {
            Log.e("res", "search online");

            Call<SearchResourcesResults> call = mNetworkModel.searchForResourcesOnlineEs(query, categorySelection.getSubjectIds(), categorySelection.getLearningLevelIds(), categorySelection.getLanguageIds(), categorySelection.getCourseType(), skip, limit);
            Response<SearchResourcesResults> response = call.execute();
            if (response.isSuccessful()) {
//                list.addAll(response.body());
                results = response.body();
            } else if (response.code() == 401) {
                if (SyncServiceHelper.refreshToken(mContext)) {
                    response = call.clone().execute();
                    if (response.isSuccessful()) {
                        results = response.body();
                    }
                } else {
                    ToastUtils.showToastAlert(mContext, "Could Not connect to the server");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    public ArrayList<BaseDataObject> searchFtpForResources(String query) {
        ArrayList<BaseDataObject> list = new ArrayList<>();

        ArrayList<Resource> listNames = mFtpFunctions.listFiles();
        for (Resource resource :
                listNames) {
            if (resource.getName().toLowerCase().contains(query))
                list.add(resource);
        }

        return list;
    }

    public ArrayList<BaseDataObject> searchForQuiz(String query) {
        query = query.toLowerCase();
        ArrayList<BaseDataObject> list = new ArrayList<>();
        for (Quiz quiz :
                mQuizModel.getQuizListSync()) {

            if (quiz.getTitle().toLowerCase().contains(query) ||
                    quiz.getMetaInformation().getSubject().getName().toLowerCase().contains(query) ||
                    quiz.getObjectId().toLowerCase().contains(query)) {
                list.add(quiz);
            }

        }
        return list;
    }


    public boolean copyFromFtp(Resource resource, String filePath, String name) {
        return mFtpFunctions.downloadResourceToTemp(resource, filePath, name);
    }

    public void downloadDigitalBook(final String objectId) {
        SyncService.startActionDownloadDigitalBook(mContext, objectId);
    }

    public void downloadPopUp(final String objectId) {
        SyncService.startActionDownloadPopUp(mContext, objectId);
    }

    public void downloadConceptMap(final String objectId) {
        SyncService.startActionDownloadConceptMap(mContext, objectId);
    }

    public void downloadInteractiveImage(final String objectId) {
        SyncService.startActionDownloadInteractiveImage(mContext, objectId);
    }

    public void downloadVideoCourse(final String objectId) {
        SyncService.startActionDownloadVideoCourse(mContext, objectId);
    }

    public void downloadInteractiveVideo(final String objectId) {
        SyncService.startActionDownloadInteractiveVideo(mContext, objectId);
    }

    public void downloadResource(final String objectId) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> subscriber) {
                JobCreator.createDownloadResourceJob(objectId).execute();

            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                    }
                });
    }

}
