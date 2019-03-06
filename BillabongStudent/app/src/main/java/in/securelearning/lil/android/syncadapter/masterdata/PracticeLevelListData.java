package in.securelearning.lil.android.syncadapter.masterdata;

import java.util.HashMap;

import in.securelearning.lil.android.app.R;


/**
 * Created by Prabodh Dhabaria on 17-03-2018.
 */

public class PracticeLevelListData {
    HashMap<String, String[]> mIds = new HashMap<>();
    HashMap<String, String[]> mNames = new HashMap<>();
    HashMap<String, int[]> mIcons = new HashMap<>();

    HashMap<String, String> mAllNames = new HashMap<>();
    HashMap<String, Integer> mAllIcons = new HashMap<>();
    HashMap<String, String> mAllIds = new HashMap<>();

    public HashMap<String, String[]> getIds() {
        return mIds;
    }

    public void setIds(HashMap<String, String[]> ids) {
        mIds = ids;
    }

    public HashMap<String, String[]> getNames() {
        return mNames;
    }

    public HashMap<String, String> getAllNames() {
        return mAllNames;
    }

    public void setAllNames(HashMap<String, String> names) {
        mAllNames = names;
    }

    public HashMap<String, String> getAllIds() {
        return mAllIds;
    }

    public void setAllIds(HashMap<String, String> Ids) {
        mAllIds = Ids;
    }

    public HashMap<String, Integer> getAllIcons() {
        return mAllIcons;
    }

    public void setAllIcons(HashMap<String, Integer> allIcons) {
        mAllIcons = allIcons;
    }

    public void setNames(HashMap<String, String[]> names) {
        mNames = names;
    }

    public PracticeLevelListData(HashMap<String, String[]> ids, HashMap<String, String[]> names, HashMap<String, int[]> icons) {
        mIds = ids;
        mNames = names;
        mIcons = icons;
    }

    public HashMap<String, int[]> getIcons() {
        return mIcons;
    }

    public void setIcons(HashMap<String, int[]> icons) {
        mIcons = icons;
    }

    public PracticeLevelListData() {

        mAllIds.put("History", "58c92788bddb93e4048f61e7");
        mAllIds.put("Geography", "58c927a0bddb93e4048f61ef");
        mAllIds.put("Civics", "58c968d47fd5ab6c2c500325");
        mAllIds.put("Mathematics", "58c9269abddb93e4048f6185");
        mAllIds.put("Biology", "58c92576bddb93e4048f6124");
        mAllIds.put("Chemistry", "58c9256ebddb93e4048f6120");
        mAllIds.put("Physics", "58c92567bddb93e4048f611c");
        mAllIds.put("Economics", "5908334e4f7d342100ec1218");
        mAllIds.put("Political Science", "58c926f4bddb93e4048f61ae");
        mAllIds.put("Art", "58c9359c7fd5ab6c2c4ff755");
        mAllIds.put("Science", "58c92560bddb93e4048f6118");
        mAllIds.put("Social Science", "58c927aabddb93e4048f61f3");
        mAllIds.put("English", "58c92c757fd5ab6c2c4ff564");
        mAllIds.put("Hindi", "58c92c837fd5ab6c2c4ff568");
        mAllIds.put("Sanskrit", "59855422a66ea21c0057deef");
        mAllIds.put("GK", "59855812a66ea21c0057e030");
        mAllIds.put("Games", "58c935aa7fd5ab6c2c4ff75d");
        mAllIds.put("Computer", "598179f6a66ea21c0057b4c9");
        mAllIds.put("Vehicle Driving", "58e88bda296fe11c00fa5025");
        mAllIds.put("Dental Science", "58c930087fd5ab6c2c4ff68a");

        mAllNames.put("58c92788bddb93e4048f61e7", "History");
        mAllNames.put("58c927a0bddb93e4048f61ef", "Geography");
        mAllNames.put("58c968d47fd5ab6c2c500325", "Civics");
        mAllNames.put("58c9269abddb93e4048f6185", "Mathematics");
        mAllNames.put("58c92576bddb93e4048f6124", "Biology");
        mAllNames.put("58c9256ebddb93e4048f6120", "Chemistry");
        mAllNames.put("58c92567bddb93e4048f611c", "Physics");
        mAllNames.put("5908334e4f7d342100ec1218", "Economics");
        mAllNames.put("58c926f4bddb93e4048f61ae", "Political Science");
        mAllNames.put("58c9359c7fd5ab6c2c4ff755", "Art");
        mAllNames.put("58c92560bddb93e4048f6118", "Science");
        mAllNames.put("58c927aabddb93e4048f61f3", "Social Science");
        mAllNames.put("58c92c757fd5ab6c2c4ff564", "English");
        mAllNames.put("58c92c837fd5ab6c2c4ff568", "Hindi");
        mAllNames.put("59855422a66ea21c0057deef", "Sanskrit");
        mAllNames.put("59855812a66ea21c0057e030", "GK");
        mAllNames.put("58c935aa7fd5ab6c2c4ff75d", "Games");
        mAllNames.put("598179f6a66ea21c0057b4c9", "Computer");
        mAllNames.put("58e88bda296fe11c00fa5025", "Vehicle Driving");
        mAllNames.put("58c930087fd5ab6c2c4ff68a", "Dental Science");

        mAllIcons.put("58c92788bddb93e4048f61e7", R.drawable.circle_history);
        mAllIcons.put("58c927a0bddb93e4048f61ef", R.drawable.circle_geography);
        mAllIcons.put("58c968d47fd5ab6c2c500325", R.drawable.circle_civics);
        mAllIcons.put("58c9269abddb93e4048f6185", R.drawable.circle_mathematic);
        mAllIcons.put("58c92576bddb93e4048f6124", R.drawable.circle_biology);
        mAllIcons.put("58c9256ebddb93e4048f6120", R.drawable.circle_chemistry);
        mAllIcons.put("58c92567bddb93e4048f611c", R.drawable.circle_physics);
        mAllIcons.put("5908334e4f7d342100ec1218", R.drawable.circle_economics);
        mAllIcons.put("58c926f4bddb93e4048f61ae", R.drawable.circle_poltical_science);
        mAllIcons.put("58c9359c7fd5ab6c2c4ff755", R.drawable.circle_art);
        mAllIcons.put("58c92560bddb93e4048f6118", R.drawable.circle_science);
        mAllIcons.put("58c927aabddb93e4048f61f3", R.drawable.circle_social_science);
        mAllIcons.put("58c92c757fd5ab6c2c4ff564", R.drawable.circle_english);
        mAllIcons.put("58c92c837fd5ab6c2c4ff568", R.drawable.circle_hindi);
        mAllIcons.put("59855422a66ea21c0057deef", R.drawable.circle_sanskrit);
        mAllIcons.put("59855812a66ea21c0057e030", R.drawable.circle_gk);
        mAllIcons.put("58c935aa7fd5ab6c2c4ff75d", R.drawable.circle_games);
        mAllIcons.put("598179f6a66ea21c0057b4c9", R.drawable.circle_computer);
        mAllIcons.put("58e88bda296fe11c00fa5025", R.drawable.circle_vehicle_driving);
        mAllIcons.put("58c930087fd5ab6c2c4ff68a", R.drawable.circle_dental_science);

        mIds.put("58c91b1793cc7dcc284b79e5", new String[]{
                "58c92788bddb93e4048f61e7",
                "58c927a0bddb93e4048f61ef",
                "58c968d47fd5ab6c2c500325",
                "58c9269abddb93e4048f6185",
                "58c92576bddb93e4048f6124",
                "58c9256ebddb93e4048f6120",
                "58c92567bddb93e4048f611c"
        });
        mNames.put("58c91b1793cc7dcc284b79e5", new String[]{
                "History",
                "Geography",
                "Civics",
                "Mathematics",
                "Biology",
                "Chemistry",
                "Physics"
        });
        mIcons.put("58c91b1793cc7dcc284b79e5", new int[]{
                R.drawable.transparent_history,
                R.drawable.transparent_geography,
                R.drawable.transparent_civics,
                R.drawable.transparent_mathematic,
                R.drawable.transparent_biology,
                R.drawable.transparent_chemistry,
                R.drawable.transparent_physics
        });

        mIds.put("58c91b2093cc7dcc284b79e9", new String[]{
                "58c92576bddb93e4048f6124",
                "58c9256ebddb93e4048f6120",
                "58c92567bddb93e4048f611c",
                "58c927a0bddb93e4048f61ef",
                "58c968d47fd5ab6c2c500325",
                "58c92788bddb93e4048f61e7",
                "58c9269abddb93e4048f6185"});
        mNames.put("58c91b2093cc7dcc284b79e9", new String[]{
                "Biology",
                "Chemistry",
                "Physics",
                "Geography",
                "Civics",
                "History",
                "Mathematics"});
        mIcons.put("58c91b2093cc7dcc284b79e9", new int[]{
                R.drawable.transparent_biology,
                R.drawable.transparent_chemistry,
                R.drawable.transparent_physics,
                R.drawable.transparent_geography,
                R.drawable.transparent_civics,
                R.drawable.transparent_history,
                R.drawable.transparent_mathematic
        });

        mIds.put("58c91b2993cc7dcc284b79ed", new String[]{
                "58c927a0bddb93e4048f61ef",
                "58c968d47fd5ab6c2c500325",
                "58c92788bddb93e4048f61e7",
                "58c9269abddb93e4048f6185",
                "58c92576bddb93e4048f6124",
                "58c9256ebddb93e4048f6120",
                "58c92567bddb93e4048f611c"});
        mNames.put("58c91b2993cc7dcc284b79ed", new String[]{
                "Geography",
                "Civics",
                "History",
                "Mathematics",
                "Biology",
                "Chemistry",
                "Physics"});
        mIcons.put("58c91b2993cc7dcc284b79ed", new int[]{
                R.drawable.transparent_geography,
                R.drawable.transparent_civics,
                R.drawable.transparent_history,
                R.drawable.transparent_mathematic,
                R.drawable.transparent_biology,
                R.drawable.transparent_chemistry,
                R.drawable.transparent_physics
        });

        mIds.put("58c91b3493cc7dcc284b79f1", new String[]{
                "58c9256ebddb93e4048f6120",
                "58c92576bddb93e4048f6124",
                "58c92567bddb93e4048f611c",
                "58c927a0bddb93e4048f61ef",
                "58c92788bddb93e4048f61e7",
                "58c9269abddb93e4048f6185"});
        mNames.put("58c91b3493cc7dcc284b79f1", new String[]{
                "Chemistry",
                "Biology",
                "Physics",
                "Geography",
                "History",
                "Mathematics"});
        mIcons.put("58c91b3493cc7dcc284b79f1", new int[]{
                R.drawable.transparent_chemistry,
                R.drawable.transparent_biology,
                R.drawable.transparent_physics,
                R.drawable.transparent_geography,
                R.drawable.transparent_history,
                R.drawable.transparent_mathematic});

        mIds.put("58c91b3c93cc7dcc284b79f5", new String[]{
                "58c9256ebddb93e4048f6120",
                "58c92576bddb93e4048f6124",
                "58c92567bddb93e4048f611c",
                "5908334e4f7d342100ec1218",
                "58c927a0bddb93e4048f61ef",
                "58c92788bddb93e4048f61e7",
                "58c9269abddb93e4048f6185",
                "58c926f4bddb93e4048f61ae"
        });
        mNames.put("58c91b3c93cc7dcc284b79f5", new String[]{
                "Chemistry",
                "Biology",
                "Physics",
                "Economics",
                "Geography",
                "History",
                "Mathematics",
                "Political Science"
        });
        mIcons.put("58c91b3c93cc7dcc284b79f5", new int[]{
                R.drawable.transparent_chemistry,
                R.drawable.transparent_biology,
                R.drawable.transparent_physics,
                R.drawable.transparent_economics,
                R.drawable.transparent_geography,
                R.drawable.transparent_history,
                R.drawable.transparent_mathematic,
                R.drawable.transparent_poltical_science
        });
    }
}
