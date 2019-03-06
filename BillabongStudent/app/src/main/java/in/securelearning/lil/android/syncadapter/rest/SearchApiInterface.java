package in.securelearning.lil.android.syncadapter.rest;


import in.securelearning.lil.android.base.dataobjects.AboutCourse;
import in.securelearning.lil.android.base.dataobjects.Assignment;
import in.securelearning.lil.android.base.dataobjects.ConceptMap;
import in.securelearning.lil.android.base.dataobjects.DigitalBook;
import in.securelearning.lil.android.base.dataobjects.InteractiveImage;
import in.securelearning.lil.android.base.dataobjects.InteractiveVideo;
import in.securelearning.lil.android.base.dataobjects.LearningMap;
import in.securelearning.lil.android.base.dataobjects.LilSearch;
import in.securelearning.lil.android.base.dataobjects.PopUps;
import in.securelearning.lil.android.base.dataobjects.Question;
import in.securelearning.lil.android.base.dataobjects.Resource;
import in.securelearning.lil.android.base.dataobjects.VideoCourse;
import in.securelearning.lil.android.base.utils.ArrayList;
import in.securelearning.lil.android.home.model.HomeModel;
import in.securelearning.lil.android.syncadapter.dataobject.LearningMapAggregatesParams;
import in.securelearning.lil.android.syncadapter.dataobject.MasteryRequestObject;
import in.securelearning.lil.android.syncadapter.dataobject.QuizResponse;
import in.securelearning.lil.android.syncadapter.dataobject.RecommendedCourseParams;
import in.securelearning.lil.android.syncadapter.dataobject.RecommendedResourceParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchCoursesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchRecommendedCoursesParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourceParams;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResourcesResults;
import in.securelearning.lil.android.syncadapter.dataobject.SearchResults;
import in.securelearning.lil.android.syncadapter.dataobject.SkillMasteryQuestionGetData;
import in.securelearning.lil.android.syncadapter.dataobject.SkillMasteryQuestionPostData;
import in.securelearning.lil.android.syncadapter.dataobject.StudentGradeMapping;
import in.securelearning.lil.android.syncadapter.dataobject.TeacherGradeMapping;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Json Download Interface
 */

public interface SearchApiInterface {

    @POST("LilSearches/search")
    Call<java.util.ArrayList<Resource>> getSearchResults(@Body LilSearch lilSearch);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<DigitalBook>> getDigitalBookSearchResults(@Body LilSearch lilSearch);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<Assignment>> getAssignmentsSearchResults(@Body LilSearch lilSearch);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<PopUps>> getPopUpSearchResults(@Body LilSearch lilSearch);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<ConceptMap>> getConceptMapSearchResults(@Body LilSearch lilSearch);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<InteractiveImage>> getInteractiveImageSearchResults(@Body LilSearch lilSearch);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<VideoCourse>> getVideoCourseSearchResults(@Body LilSearch search);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<InteractiveVideo>> getInteractiveVideoSearchResults(@Body LilSearch search);

    @POST("LilSearches/search")
    Call<java.util.ArrayList<AboutCourse>> getCourseSearchResults(@Body LilSearch search);

//    @GET("search/lilSearch/{searchParams}/{skip}/{limit}")
//    Call<SearchResults> getCourseSearchResultsEs(@Path("searchParams") String searchParams, @Path("skip") int skip, @Path("limit") int limit);

    @GET("search/digital-book/{objectId}/preview")
    Call<ResponseBody> getDigitalBook2(@Path("objectId") String objectId);

    @GET("search/custom-section/fetchCSWithAnnotation/{objectId}")
    Call<ResponseBody> getCustomSection(@Path("objectId") String objectId);

    @GET("search/interactive-video/fetchInteractiveVideo/{objectId}")
    Call<ResponseBody> getInteractiveVideo(@Path("objectId") String objectId);

    @GET("search/popup/fetchPopup/{objectId}")
    Call<ResponseBody> getPopUp(@Path("objectId") String objectId);

    @GET("search/interactive-image/fetchInteractiveImage/{objectId}")
    Call<ResponseBody> getInteractiveImage(@Path("objectId") String objectId);

    @GET("search/concept-map/fetch/{objectId}")
    Call<ResponseBody> getConceptMap(@Path("objectId") String objectId);

    @POST("search/search-page/lilSearch")
    Call<SearchCoursesResults> getCourseSearchResultsEs(@Body SearchCoursesParams params);

    @POST("search/recommended-page/")
    Call<SearchCoursesResults> getRecommendedCourses(@Body RecommendedCourseParams params);

    @POST("search/recommended-page/getResultsBySubjectIdAndInstituteId")
    Call<SearchResults> getCourseSearchResultsBySubjectEs(@Body SearchRecommendedCoursesParams params);

    @POST("search/recommended-page/getTopFiltersAndSubjects")
    Call<SearchResults> getFilterParamsEs(@Body SearchRecommendedCoursesParams params);

    @GET("search/find/Question/{\"where\": {\"skills.id\": \"{skillId}\", \"isPlainText\":true}, \"limit\": {limit}}")
    Call<java.util.ArrayList<Question>> getQuestionsFromSkillIdEs(@Path("skillId") String skillId, @Path("limit") int limit);

    @POST("search/question/fetchBySkillAndComplexityLevel")
    Call<SkillMasteryQuestionGetData> getQuestionFromSkillComplexityLevel(@Body SkillMasteryQuestionPostData skillMasteryQuestionResponse);

    @GET("search/question/fetchBySkill/{objectId}/{skip}/{limit}")
    Call<java.util.ArrayList<Question>> getQuestionFromSkill(@Path("objectId") String objectId, @Path("skip") int skip, @Path("limit") int limit);

    @POST("search/question/fetchBySkillTopicAndComplexityLevel")
    Call<SkillMasteryQuestionGetData> fetchBySkillAndComplexityLevel(@Body MasteryRequestObject masteryRequestObject);

    @POST("search/question/fetchBySkillListAndComplexityLevel")
    Call<java.util.ArrayList<SkillMasteryQuestionGetData>> fetchBySkillListAndComplexityLevel(@Body MasteryRequestObject masteryRequestObject);

    @GET("LearningMaps/fetchByUser")
    Call<ArrayList<LearningMap>> getLearningMapList();

    @POST("search/search-page/lilSearch")
    Call<SearchResourcesResults> getResourceSearchResultsEs(@Body SearchResourceParams params);

    @POST("search/recommended-page/getYoutubeResourcesBySubjectGradeTopic")
    Call<SearchResourcesResults> getVideoData(@Body RecommendedResourceParams params);

    @GET("learning-map/responseBySubjectIdAndGradeId/{subjectId}/{gradeId}")
    Call<java.util.ArrayList<HomeModel.StudentScore>> getStudentDataForMap(@Path("subjectId") String subjectId, @Path("gradeId") String gradeId);

    @GET("search/curator-mapping/fetchCuratorGradeSection")
    Call<TeacherGradeMapping> getTeacherMapDataForMap();

    @GET("search/institute-mapping/fetch/{instituteId}/{gradeId}/{sectionId}")
    Call<StudentGradeMapping> getSubjectFromInstituteGradeSection(@Path("instituteId") String instituteId, @Path("gradeId") String gradeId, @Path("sectionId") String sectionId);

    @POST("search/learning-map/getLMBySubjectGradeSection")
    Call<java.util.ArrayList<HomeModel.StudentScore>> getStudentAggregateDataForMap(@Body LearningMapAggregatesParams params);

    @POST("search/search-page/coursesBySubjectGradeTopic")
    Call<SearchCoursesResults> getCourseBySubjectGrade(@Body RecommendedCourseParams params);

    @POST("search/recommended-page/getYoutubeResourcesBySubjectGradeTopic")
    Call<SearchResourcesResults> getResourceBySubjectGrade(@Body RecommendedResourceParams params);

    @POST("QuestionResponses/bulkUpload")
    Call<ResponseBody> uploadQuizResponse(@Body QuizResponse quizResponse);



}
