package in.securelearning.lil.android.home.views.fragment;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import in.securelearning.lil.android.home.model.HomeModel;

/**
 * Created by Secure on 24-04-2017.
 */

public class LearnigMapUtils {

        HashMap<String, HomeModel.SubjectMap> subjectArrayList = new HashMap<>();
        ArrayList<HomeModel.TopicMap> topicMapList = new ArrayList<>();
        ArrayList<HomeModel.SkillMap> skillMapList = new ArrayList<>();
        int[] subject = {0, 0, 0};
        int[] topic = {0, 0, 0};
        int[] skill = {0, 0, 0};

        public ArrayList<HomeModel.TopicMap> getTopicMapList() {
            return topicMapList;
        }

        public void setTopicMapList(ArrayList<HomeModel.TopicMap> topicMapList) {
            this.topicMapList = topicMapList;
        }

        public ArrayList<HomeModel.SkillMap> getSkillMapList() {
            return skillMapList;
        }

        public void setSkillMapList(ArrayList<HomeModel.SkillMap> skillMapList) {
            this.skillMapList = skillMapList;
        }

        public LearnigMapUtils() {
        }

        public HashMap<String, HomeModel.SubjectMap> getSubjectArrayList() {
            return subjectArrayList;
        }

        public void setSubjectArrayList(HashMap<String, HomeModel.SubjectMap> subjectArrayList) {
            this.subjectArrayList = subjectArrayList;
        }

        public int[] getSubject() {
            return subject;
        }

        public void setSubject(int[] subject) {
            this.subject = subject;
        }

        public int[] getTopic() {
            return topic;
        }

        public void setTopic(int[] topic) {
            this.topic = topic;
        }

        public int[] getSkill() {
            return skill;
        }

        public void setSkill(int[] skill) {
            this.skill = skill;
        }

    public interface IValueFormatter {
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler);
    }

    public static class MyValueFormatter implements IValueFormatter, ValueFormatter {
//        private DecimalFormat mFormat;

        public MyValueFormatter() {
//            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
           return String.format("%.2f", value);
//            return mFormat.format(value) + "";
        }
    }

    public interface IAxisValueFormatter {
        public String getFormattedValue(float value, YAxis yAxis);
    }

    public static class MyAxisValueFormatter implements IAxisValueFormatter, YAxisValueFormatter {
        private DecimalFormat mFormat;

        public MyAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");

        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value) + "";
        }
    }
}
