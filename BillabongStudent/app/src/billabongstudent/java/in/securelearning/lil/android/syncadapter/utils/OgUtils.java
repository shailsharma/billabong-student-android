package in.securelearning.lil.android.syncadapter.utils;

import android.util.Log;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.OGMetaDataResponse;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.InjectorSyncAdapter;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Response;

/**
 * Created by Secure on 05-06-2017.
 */

public class OgUtils {
    @Inject
    NetworkModel mNetworkModel;

    public OgUtils() {
        InjectorSyncAdapter.INSTANCE.getComponent().inject(this);
    }

    public Observable<OGMetaDataResponse> getOgDataFromServer(final ArrayList<String> oGDataList) {
        return Observable.create(new ObservableOnSubscribe<OGMetaDataResponse>() {
            @Override
            public void subscribe(ObservableEmitter<OGMetaDataResponse> e) throws Exception {

                Response<OGMetaDataResponse> response = mNetworkModel.getOGData(oGDataList).execute();
                if (response != null && response.isSuccessful()) {
                    e.onNext(response.body());
                }
                e.onComplete();
            }
        });
    }

    public ArrayList<String> extractUrls(String text) {
        String data[] = GeneralUtils.getArrayOfAllUrls(text);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(data));
        return list;
    }
}
