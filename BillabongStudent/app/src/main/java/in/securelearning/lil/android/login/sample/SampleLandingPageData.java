package in.securelearning.lil.android.login.sample;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.Boards;
import in.securelearning.lil.android.base.dataobjects.GradeSection;
import in.securelearning.lil.android.base.dataobjects.Grades;
import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.Languages;
import in.securelearning.lil.android.base.dataobjects.PeriodicEventsNew;
import in.securelearning.lil.android.base.dataobjects.Subjects;
import in.securelearning.lil.android.base.dataobjects.WeeklySchedule;
import in.securelearning.lil.android.base.db.DatabaseHandler;
import in.securelearning.lil.android.base.db.query.DatabaseQueryHelper;
import in.securelearning.lil.android.base.model.BadgesModel;
import in.securelearning.lil.android.base.utils.DocumentUtils;
import in.securelearning.lil.android.base.utils.FileUtils;
import in.securelearning.lil.android.login.InjectorLogin;

/**
 * Created by Pushkar Raj on 6/27/2016.
 */
public class SampleLandingPageData {
    private final String TAG = SampleLandingPageData.class.getCanonicalName();
    @Inject
    Context mContext;
    @Inject
    DatabaseQueryHelper mDatabaseQueryHelper;

    public SampleLandingPageData() {
        InjectorLogin.INSTANCE.getComponent().inject(this);
    }

    /**
     * Method for insert Dummy Jsons to Databse
     */


    public void insertSampleData() {
        insertFromSampleJson();
    }

    /**
     * Method for insert Dummy Jsons to Databse
     */
    private void insertFromSampleJson() {

        boolean inserted = false;
        String periodic = "";
        String fileName = "";
        String jsonString = "";

        if (new BadgesModel().fetchLilBadgesListSync().size() <= 0) {

//            insertJsonForMetadDatScreen();

            //Create and Insert student Periodic events
            //fileName = "periodic_events_sample_new";

//            for (int i = 1; i <= 4; i++) {
//                jsonString = getJsonString(fileName + "" + i, mContext);
//                PeriodicEventsNew periodicEventsJson = constructUsingGson(PeriodicEventsNew.class, jsonString);
//                insertSampleInDb(periodicEventsJson, mContext, DatabaseHandler.DOC_TYPE_PERIOD_EVENTS);
//            }


            //Create and Insert Teacher Periodic events
            periodic = "lil_badge_sample";
            for (int i = 1; i <= 7; i++) {
                jsonString = getJsonString(periodic + i, mContext);
                LILBadges lilBadeJson = constructUsingGson(LILBadges.class, jsonString);
                insertSampleInDbLearningNetwork(lilBadeJson, mContext, DatabaseHandler.DOC_TYPE_LIL_BADGE);
            }

//            periodic = "lil_curriculum_sample1_new";
//            jsonString = getJsonString(periodic, mContext);
//            CurriculumNew lilCurriculumJson = constructUsingGson(CurriculumNew.class, jsonString);
//            insertSampleInDb(lilCurriculumJson, mContext, DatabaseHandler.DOC_TYPE_CURRICULUM);

//
//            //Start-Insert Weekly Schedule into databse
//            fileName = "weekly_schedule_sample";
//            jsonString = getJsonString(fileName, mContext);
//            WeeklySchedule weeklySchedule = constructUsingGson(WeeklySchedule.class, jsonString);
//            insertSampleInDbLearningNetwork(weeklySchedule, mContext, DatabaseHandler.DOC_TYPE_WEEKLY_SCHEDULE);

        }

    }


    public <T> T constructUsingGson(Class<T> type, String jsonString) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonString, type);
    }


    private void insertSampleInDbLearningNetwork(Object object, Context context, String docType) {
        Date now = new Date();
        String nowString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(now);

        String alias = "testuser" + nowString;

        // create an object that contains data for a document
        Map<String, Object> docContent = new DocumentUtils().getDocumentFromObject(docType, object);


        try {
            String MId = "";
            if (docType.toString().equalsIgnoreCase(DatabaseHandler.DOC_TYPE_GROUP)) {
                Log.e(TAG, "Group Doc Inserted:" + mDatabaseQueryHelper.createInLearningNetwork(docContent));
            } else {

                MId = mDatabaseQueryHelper.createInLearningNetwork(docContent);
            }

            //    Map<String, Object>  objectMap=mDatabaseQueryHelper.retrieve(MId);

            // 1. Create


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getJsonString(String fileName, Context context) {
        String jsonString = "";

        String fileNameWithExt = fileName + ".json";

        InputStream resourceReader;
//        if (FileUtils.checkIfJsonFileIsExists(mContext, fileName))
//            resourceReader = FileUtils.getInputStreamOfJsonFile(mContext, fileNameWithExt);
//        else {
        resourceReader = context.getResources().openRawResource(getRawId(fileName));
//        }

        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceReader, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            Log.e("JsonFromRaw", "Unhandled exception while using JSONResourceReader", e);
        } finally {
            try {
                resourceReader.close();
            } catch (Exception e) {
                Log.e("JsonFromRaw", "Unhandled exception while using JSONResourceReader", e);
            }
        }

        jsonString = writer.toString();
        return jsonString;

    }

    public int getRawId(String mName) {

        int resId = 0;
        try {
            @SuppressWarnings("rawtypes") Class raw = in.securelearning.lil.android.app.R.raw.class;
            Field field = raw.getField(mName);
            resId = field.getInt(null);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return resId;

    }
}
