package in.securelearning.lil.android.home.model;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Chaitendra on 3/24/2017.
 */

public class ScreenInfo {


    public static String getScreenDPI(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        float xDpi = displayMetrics.xdpi;
        String strDpi = "";
        if (xDpi <= 120 || (xDpi > 120 && xDpi < 160)) {
            strDpi = "ldpi";
        } else if (xDpi == 160 || (xDpi > 160 && xDpi < 240)) {
            strDpi = "mdpi";
        } else if (xDpi == 240 || (xDpi > 240 && xDpi < 320)) {
            strDpi = "hdpi";
        } else if (xDpi == 320 || (xDpi > 320 && xDpi < 480)) {
            strDpi = "xhdpi";
        } else if (xDpi == 480 || (xDpi > 480 && xDpi < 640)) {
            strDpi = "xxhdpi";
        } else if (xDpi >= 640) {
            strDpi = "xxxhdpi";
        }

        return strDpi;
    }

}
