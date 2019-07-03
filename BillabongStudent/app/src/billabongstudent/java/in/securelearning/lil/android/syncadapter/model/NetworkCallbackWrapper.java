package in.securelearning.lil.android.syncadapter.model;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import in.securelearning.lil.android.syncadapter.dataobjects.BaseResponse;
import in.securelearning.lil.android.syncadapter.dataobjects.BaseView;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public abstract class NetworkCallbackWrapper <T extends BaseResponse> extends DisposableObserver<T> {
    //BaseView is just a reference of a View in MVP
    private WeakReference<BaseView> weakReference;

    public NetworkCallbackWrapper(BaseView view) {
        this.weakReference = new WeakReference<>(view);
    }

    protected abstract void onSuccess(T t);

    @Override
    public void onNext(T t) {
        //You can return StatusCodes of different cases from your API and handle it here. I usually include these cases on BaseResponse and iherit it from every Response
        onSuccess(t);
    }

    @Override
    public void onError(Throwable t) {
        BaseView view = weakReference.get();
        if (t instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) t).response().errorBody();
            view.onUnknownError(getErrorMessage(responseBody));
        } else if (t instanceof SocketTimeoutException) {
            view.onTimeout();
        } else if (t instanceof IOException) {
            view.onNetworkError();
        } else {
            view.onUnknownError(t.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}