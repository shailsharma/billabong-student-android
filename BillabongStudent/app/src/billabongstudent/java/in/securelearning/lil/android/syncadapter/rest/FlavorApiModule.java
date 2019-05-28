package in.securelearning.lil.android.syncadapter.rest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import in.securelearning.lil.android.app.BuildConfig;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;
import in.securelearning.lil.android.base.di.scope.ActivityScope;
import in.securelearning.lil.android.base.utils.AppPrefs;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Prabodh Dhabaria on 25-07-2016.
 */
@Module
public class FlavorApiModule {
    private Context mContext;
    private static String BASE_URL;
   // private static String BASE_URL_FCM = "https://fcm.googleapis.com/fcm/";
    //private static final String YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE = BuildConfig.LEGACY_SERVER_KEY_FROM_FCM;


    public static String getBaseUrl() {
        return BASE_URL;
    }

    public FlavorApiModule(Context context) {
        this.mContext = context;
        BASE_URL = context.getString(R.string.api_url);

    }


    private Retrofit getClient(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
//                                .addQueryParameter("access_token", InjectorSyncAdapter.INSTANCE.getComponent().appUserModel().getApplicationUser().getid())
                                .build();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", AppPrefs.getIdToken(mContext))
                                .url(url);

                        Request request = requestBuilder.build();

                        final Request copy = request;
                        final Buffer buffer = new Buffer();
                        if (copy != null && copy.body() != null)
                            copy.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client;
    }

    private Retrofit getDirectClient(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
//                                .addQueryParameter("access_token", InjectorSyncAdapter.INSTANCE.getComponent().appUserModel().getApplicationUser().getid())
                                .build();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", AppPrefs.getIdToken(mContext))
                                .url(url);

                        Request request = requestBuilder.build();

                        final Request copy = request;
                        final Buffer buffer = new Buffer();
                        if (copy != null && copy.body() != null)
                            copy.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .build();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .build();

        return client;
    }

    private Retrofit getSearchClient(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
//                                .addQueryParameter("access_token", InjectorSyncAdapter.INSTANCE.getComponent().appUserModel().getApplicationUser().getid())
                                .build();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", AppPrefs.getIdToken(mContext))
                                .url(url);

                        Request request = requestBuilder.build();

                        final Request copy = request;
                        final Buffer buffer = new Buffer();
                        if (copy != null && copy.body() != null)
                            copy.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client;
    }

    private Retrofit getClientWithoutAuthorizationHeader(String baseUrl) {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
//                                .addQueryParameter("access_token", InjectorSyncAdapter.INSTANCE.getComponent().appUserModel().getApplicationUser().getid())
                                .build();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url);

                        Request request = requestBuilder.build();

                        final Request copy = request;
                        final Buffer buffer = new Buffer();
                        if (copy != null && copy.body() != null)
                            copy.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client;
    }

    private Retrofit getClientWithExclusionStrategy(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
//                                .addQueryParameter("access_token", InjectorSyncAdapter.INSTANCE.getComponent().appUserModel().getApplicationUser().getid())
                                .build();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", AppPrefs.getIdToken(mContext))
                                .url(url);

                        Request request = requestBuilder.build();

                        final Request copy = request;
                        final Buffer buffer = new Buffer();
                        if (copy != null && copy.body() != null)
                            copy.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .build();
        Gson gson = null;
        try {
            gson = new GsonBuilder()
                    .setExclusionStrategies(new ObjectIdExclusionStrategy(BaseDataObject.class.getCanonicalName() + ".mObjectId"))
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client;
    }

    @Provides
    @ActivityScope
    public FlavorBaseApiInterface getBaseClient() {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
//                                .addQueryParameter("access_token", InjectorSyncAdapter.INSTANCE.getComponent().appUserModel().getApplicationUser().getid())
                                .build();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url);

                        Request request = requestBuilder.build();

                        final Request copy = request;
                        final Buffer buffer = new Buffer();
                        if (copy != null && copy.body() != null)
                            copy.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client.create(FlavorBaseApiInterface.class);
    }

    @Provides
    @ActivityScope
    public FlavorDownloadApiInterface getDownloadClient() {

        return getClient(BASE_URL).create(FlavorDownloadApiInterface.class);
    }

    @Provides
    @ActivityScope
    public FlavorFCMApiInterface sendLocationData() {
        return getFCMDirectClient("").create(FlavorFCMApiInterface.class);
    }

    private Retrofit getFCMDirectClient(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();
                        HttpUrl url = originalHttpUrl.newBuilder().build();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "")
                                .url(url);
                        Request request = requestBuilder.build();
                        final Request copy = request;
                        final Buffer buffer = new Buffer();
                        if (copy != null && copy.body() != null)
                            copy.body().writeTo(buffer);
                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(35, TimeUnit.SECONDS)
                .readTimeout(35, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return client;
    }


}
