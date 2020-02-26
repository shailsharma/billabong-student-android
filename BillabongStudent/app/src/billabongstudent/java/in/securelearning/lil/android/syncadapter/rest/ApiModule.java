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
import in.securelearning.lil.android.thirdparty.utils.ThirdPartyPrefs;
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
public class ApiModule {
    private Context moContext;
    private String BASE_URL;
    private static final long API_TIMEOUT = 60; //in seconds


    public ApiModule(Context context) {
        this.moContext = context;
        BASE_URL = context.getString(R.string.api_url);
    }


    public Retrofit getClient(String baseUrl) {

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
                                .header("Authorization", AppPrefs.getIdToken(moContext))
                                .header("AppVersion", BuildConfig.VERSION_NAME)
                                .url(url);

                        Request request = requestBuilder.build();

                        final Buffer buffer = new Buffer();
                        if (request != null && request.body() != null)
                            request.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
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
                                .header("Authorization", AppPrefs.getIdToken(moContext))
                                .header("AppVersion", BuildConfig.VERSION_NAME)
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
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
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
                        String token = AppPrefs.getIdToken(moContext);
                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", token)
                                .header("AppVersion", BuildConfig.VERSION_NAME)
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
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
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
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
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
                                .header("Authorization", AppPrefs.getIdToken(moContext))
                                .header("AppVersion", BuildConfig.VERSION_NAME)
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
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
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

    private Retrofit getMindSparkClient(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .build();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("x-api-key", moContext.getString(R.string.mind_spark_x_api_key))
                                .url(url);

                        Request request = requestBuilder.build();

                        final Buffer buffer = new Buffer();
                        if (request != null && request.body() != null)
                            request.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private Retrofit getWikiHowClient(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .build();

                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url);

                        Request request = requestBuilder.build();

                        final Buffer buffer = new Buffer();
                        if (request != null && request.body() != null)
                            request.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private Retrofit getLogiqidsClient(String baseUrl) {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .build();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Session-Token", ThirdPartyPrefs.getLogiqidsSessionToken(moContext))
                                .url(url);

                        Request request = requestBuilder.build();

                        final Buffer buffer = new Buffer();
                        if (request != null && request.body() != null)
                            request.body().writeTo(buffer);

                        Log.e("log request", String.format("\nrequest:\n%s\nheaders:\n%s\nurl:\n%s", buffer.readUtf8(), request.headers(), request.url().toString()));
                        Log.e("log url", url.toString());
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @ActivityScope
    public BaseApiInterface getBaseClient() {
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
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client.create(BaseApiInterface.class);
    }

    @Provides
    @ActivityScope
    public BaseAuthApiInterface getBaseAuthClient() {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .build();

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
                .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client.create(BaseAuthApiInterface.class);
    }

    @Provides
    @ActivityScope
    public UploadApiInterface getUploadClient() {
        return getClient(BASE_URL).create(UploadApiInterface.class);
    }

    @Provides
    @ActivityScope
    public DirectUploadApiInterface getDirectUploadClient() {

        return getDirectClient(BASE_URL).create(DirectUploadApiInterface.class);
    }

    @Provides
    @ActivityScope
    public NewUploadApiInterface getNewUploadClient() {

        return getClientWithExclusionStrategy(BASE_URL).create(NewUploadApiInterface.class);
    }

    @Provides
    @ActivityScope
    public DownloadApiInterface getDownloadClient() {

        return getClient(BASE_URL).create(DownloadApiInterface.class);
    }

    @Provides
    @ActivityScope
    public SearchApiInterface getSearchClient() {

        return getSearchClient(BASE_URL).create(SearchApiInterface.class);
    }

    @Provides
    @ActivityScope
    public DownloadFilesApiInterface getFileDownloadClient() {

        return getClientWithoutAuthorizationHeader(BASE_URL).create(DownloadFilesApiInterface.class);
    }

    @Provides
    @ActivityScope
    public UploadFilesApiInterface getFileUploadClient() {

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
                                .header("Authorization", AppPrefs.getIdToken(moContext))
                                .header("AppVersion", BuildConfig.VERSION_NAME)
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
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(10, TimeUnit.SECONDS)
                .build();
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Gson gson = new GsonBuilder().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client.create(UploadFilesApiInterface.class);
    }

    @Provides
    @ActivityScope
    public SyncSuccessApiInterface getSyncSuccessClient() {
        return getClient(BASE_URL).create(SyncSuccessApiInterface.class);
    }

    @Provides
    @ActivityScope
    public FCMApiInterface sendLocationData() {
        String BASE_URL_FCM = "https://fcm.googleapis.com/fcm/";
        return getFCMDirectClient(BASE_URL_FCM).create(FCMApiInterface.class);
    }

    @Provides
    @ActivityScope
    public MindSparkApiInterface getMindSparkLoginClient() {
        return getMindSparkClient(moContext.getString(R.string.mind_spark_base_url)).create(MindSparkApiInterface.class);

    }

    @Provides
    @ActivityScope
    public WikiHowApiInterface getWikiHowApiInterface() {
        return getWikiHowClient(moContext.getString(R.string.base_url_wikiHow)).create(WikiHowApiInterface.class);

    }

    @Provides
    @ActivityScope
    public LogiqidsApiInterface getLogiqidsApiInterface() {
        return getLogiqidsClient(moContext.getString(R.string.base_url_logiqids)).create(LogiqidsApiInterface.class);

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
                                .header("AppVersion", BuildConfig.VERSION_NAME)
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
