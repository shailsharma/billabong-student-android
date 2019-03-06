package in.securelearning.lil.android.home.views.activity;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.syncadapter.dataobject.AboutCourseExt;

/**
 * Created by Secure on 23-10-2017.
 */

public class TopicDataObject {
    String topicId;
    String topicName;
    String gradeId;
    ArrayList<AboutCourseExt> topicList;
    ArrayList<FavouriteResource> topicResourceList;
    ArrayList<String> subjectIds;
    String subjectName;
    public TopicDataObject() {
    }

    public TopicDataObject(String topicId, String topicName, String gradeId, ArrayList<String> subjectIds, String subjectName) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.gradeId = gradeId;
        this.subjectIds = subjectIds;
        this.subjectName = subjectName;
    }

    public TopicDataObject(String topicId, String topicName) {
        this.topicId = topicId;
        this.topicName = topicName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<String> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(ArrayList<String> subjectIds) {
        this.subjectIds = subjectIds;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public ArrayList<AboutCourseExt> getTopicList() {
        return topicList;
    }

    public void setTopicList(ArrayList<AboutCourseExt> topicList) {
        this.topicList = topicList;
    }

    public ArrayList<FavouriteResource> getTopicResourceList() {
        return topicResourceList;
    }

    public void setTopicResourceList(ArrayList<FavouriteResource> topicResourceList) {
        this.topicResourceList = topicResourceList;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }
}
