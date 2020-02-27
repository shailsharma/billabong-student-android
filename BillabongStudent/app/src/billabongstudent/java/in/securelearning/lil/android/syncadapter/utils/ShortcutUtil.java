package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.Toast;

import java.util.Arrays;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.login.views.activity.LoginActivity;

/**
 * Created by Chaitendra on 23-Mar-18.
 */

public class ShortcutUtil {

    public static final String ACTION_SHORTCUT_ASSIGNMENT = "in.securelearning.lil.android.action.SHORTCUT_ASSIGNMENT";
    public static final String ACTION_SHORTCUT_LEARNING_NETWORK = "in.securelearning.lil.android.action.SHORTCUT_LEARNING_NETWORK";
    public static final String ACTION_SHORTCUT_CALENDAR = "in.securelearning.lil.android.action.SHORTCUT_CALENDAR";
    public static final String ACTION_SHORTCUT_WORKSPACE = "in.securelearning.lil.android.action.SHORTCUT_WORKSPACE";
    public static final String ACTION_SHORTCUT_TRAINING = "in.securelearning.lil.android.action.SHORTCUT_TRAINING";
    public static final String ACTION_SHORTCUT_LEARNING_MAP = "in.securelearning.lil.android.action.SHORTCUT_LEARNING_MAP";

    public static void addShortcut(Context context, String id, String label, int icon, String action) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(action);
            intent.putExtra("duplicate", false);
            ShortcutInfo shortcut = new ShortcutInfo.Builder(context, id)
                    .setIcon(Icon.createWithResource(context, icon))
                    .setShortLabel(label)
                    .setIntent(intent)
                    .build();

            shortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));
        } else {
            Intent shortcutIntent = new Intent(context, LoginActivity.class);
            shortcutIntent.setAction(action);
            Intent addIntent = new Intent();
            addIntent.putExtra("duplicate", false);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, icon));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(addIntent);
        }

        Toast.makeText(context, context.getString(R.string.messageShortcutCreated), Toast.LENGTH_SHORT).show();

    }

    public static void removeShortcut(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            if (shortcutManager != null) {
                shortcutManager.removeAllDynamicShortcuts();
            }
        } else {
            String[] actions = {ACTION_SHORTCUT_ASSIGNMENT,
                    ACTION_SHORTCUT_LEARNING_NETWORK,
                    ACTION_SHORTCUT_CALENDAR,
                    ACTION_SHORTCUT_WORKSPACE,
                    ACTION_SHORTCUT_TRAINING,
                    ACTION_SHORTCUT_LEARNING_MAP};

            String[] labels = {context.getString(R.string.homework),
                    context.getString(R.string.title_network),
                    context.getString(R.string.string_calendar),
                    context.getString(R.string.label_workspace),
                    context.getString(R.string.labelTraining),
                    context.getString(R.string.string_nav_learning_map)};

            for (int i = 0; i < actions.length; i++) {
                Intent shortcutIntent = new Intent(context, LoginActivity.class);
                shortcutIntent.setAction(actions[i]);
                Intent addIntent = new Intent();
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, labels[i]);
                addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
                context.sendBroadcast(addIntent);


            }

        }

    }


}


