package in.securelearning.lil.android.base.db.query;

import android.content.Context;
import android.text.TextUtils;

import in.securelearning.lil.android.base.db.DatabaseHandler;
import in.securelearning.lil.android.base.utils.AppPrefs;

/**
 * Created by Prabodh Dhabaria on 14-11-2017.
 */

public class FlavorDatabaseQueryHelper extends DatabaseHandler {
    public static FlavorDatabaseQueryHelper sInstance;

    protected FlavorDatabaseQueryHelper(Context context) {
        super(context);
    }

    protected FlavorDatabaseQueryHelper(String dbname, Context context) {
        super(dbname, context);
    }

    public static FlavorDatabaseQueryHelper getInstance(Context context) {
        if(sInstance == null && AppPrefs.isLoggedIn(context)) {
            String userId = AppPrefs.getUserId(context);
            if(!TextUtils.isEmpty(userId)) {
                String prefix = "lil" + userId.hashCode();
                sInstance = new FlavorDatabaseQueryHelper(prefix, context);
            }
        }

        if(sInstance == null) {
            sInstance = new FlavorDatabaseQueryHelper(context);
        }

        return sInstance;
    }
}
