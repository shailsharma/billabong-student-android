package in.securelearning.lil.android.syncadapter.dataobjects;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.utils.AppPrefs;
import in.securelearning.lil.android.login.views.activity.LoginActivity;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Secure on 12-05-2017.
 */

public class FCMToken extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        /* Get updated InstanceID token.*/
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", "Refreshed token: " + refreshedToken);

        /* If you want to send messages to this application instance or
         manage this apps subscriptions on the server side, send the
        /Instance ID token to your app server.*/
        try {
            sendRegistrationToServer(getBaseContext(), refreshedToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendRegistrationToServer(Context context, String token) throws IOException {

        if (BuildConfig.IS_FCM_APP_SERVER_SYNC_ENABLED) {

            if (!TextUtils.isEmpty(token) && AppPrefs.isLoggedIn(context)) {
                NetworkModel networkModel = InjectorSyncAdapter.INSTANCE.getComponent().networkModel();
                RefreshFCMToken refreshFCMToken = new RefreshFCMToken();
                refreshFCMToken.setToken(token);
                refreshFCMToken.setUserDeviceType(context.getString(R.string.labelAndroid));
                Call<ResponseBody> responseBodyCall = networkModel.sendRegistrationToServer(refreshFCMToken);
                Response<ResponseBody> response = responseBodyCall.execute();
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("FCM response1--", response.body().toString());
                } else if (response.code() == 401 && SyncServiceHelper.refreshToken(context)) {
                    Response response1 = responseBodyCall.clone().execute();
                    if (response1.isSuccessful() && response1.body() != null) {
                        Log.e("FCM response2--", response1.body().toString());
                    } else if (response1.code() == 401) {
                        context.startActivity(LoginActivity.getUnauthorizedIntent(context));
                    }
                }
            }
        }


    }

}
