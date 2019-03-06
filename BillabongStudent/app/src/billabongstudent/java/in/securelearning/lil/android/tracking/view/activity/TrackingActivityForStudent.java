package in.securelearning.lil.android.tracking.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityTrackingStudentBinding;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.base.rxbus.RxBus;
import in.securelearning.lil.android.home.utils.PreferenceSettingUtilClass;
import in.securelearning.lil.android.home.views.widget.AndroidPermissions;
import in.securelearning.lil.android.syncadapter.events.TrackingPostEvent;
import in.securelearning.lil.android.syncadapter.service.SyncServiceHelper;
import in.securelearning.lil.android.tracking.InjectorTracking;
import in.securelearning.lil.android.tracking.model.TrackingMapModel;
import in.securelearning.lil.android.tracking.view.DirectionsJSONParser;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TrackingActivityForStudent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    MarkerOptions marker = null;
    Marker currentMarker = null;
    @Inject
    RxBus rxBus;
    private Disposable disposable;
    @Inject
    TrackingMapModel mTrackingModel;
    ActivityTrackingStudentBinding mBinding;
    private LocationManager locationManager;
    private String provider;
    private String groupId = null;
    boolean isArriving = false;
    boolean hasArrived = false;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorTracking.INSTANCE.getComponent().inject(this);
        new DirectionsJSONParser().checkIfLocationServiceIsOnOrOff(TrackingActivityForStudent.this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tracking_student);
        setTitle("Tracking For Student");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        provider = locationManager.getBestProvider(criteria, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 19));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AndroidPermissions.checkLocationPermission(TrackingActivityForStudent.this);
            return;
        }
        mMap.setMyLocationEnabled(true);
        getCurrentLocationForFirsttime();
//        startFachingLocation();
    }


    private void getRouteData() {
        Observable.create(new ObservableOnSubscribe<SparseArray<TrackingRoute>>() {
            @Override
            public void subscribe(ObservableEmitter<SparseArray<TrackingRoute>> e) throws Exception {
                SparseArray<TrackingRoute> route = mTrackingModel.getRouteDataFromDataBase(groupId);
                if (route != null) {
                    e.onNext(route);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SparseArray<TrackingRoute>>() {
                    @Override
                    public void accept(SparseArray<TrackingRoute> locationData) throws Exception {
                        drawPathOnMap(locationData.get(0));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void startFachingLocation() {
        SyncServiceHelper.startSyncService(this);
        disposable = rxBus.toFlowable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(final Object o) throws Exception {
                if (o instanceof TrackingPostEvent) {

                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            if (groupId == null) {
                                groupId = ((TrackingPostEvent) o).getObjectId();
                                getRouteData();
                            } else {
                                drawCabOnMap(((TrackingPostEvent) o).getText());
                            }
                        }
                    });
                }
            }
        });
    }

    private Runnable getCurrentLocationForFirsttime() {
        Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(ObservableEmitter<Location> e) throws Exception {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                final Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                provider = locationManager.getBestProvider(criteria, true);
                Location loc = null;
                LatLng sydney;
                if (provider != null && !provider.equals("")) {
                    while (loc == null) {
                        if (ActivityCompat.checkSelfPermission(TrackingActivityForStudent.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivityForStudent.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            AndroidPermissions.checkLocationPermission(TrackingActivityForStudent.this);
                            return;
                        }
                        loc = locationManager.getLastKnownLocation(provider);
                        if (loc != null) {
                            e.onNext(loc);
                            e.onComplete();
                        }else{
                            Thread.currentThread().sleep(3000);
                        }
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location loc) throws Exception {
                        LatLng sydney = new LatLng(loc.getLatitude(), loc.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 19));
                        startFachingLocation();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        return null;
    }

    public void drawCabOnMap(String data) {
        if (data != null) {
            Location location = mTrackingModel.getLocationFromPostText(data);

            DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
            if (ActivityCompat.checkSelfPermission(TrackingActivityForStudent.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivityForStudent.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AndroidPermissions.checkLocationPermission(TrackingActivityForStudent.this);
                return;
            }
            //Here we get value from Setting and use it for send notification according setting value

            int busIsArraving = PreferenceSettingUtilClass.getBus_arriving(TrackingActivityForStudent.this);
            int busHasArrived = PreferenceSettingUtilClass.getBus_has_arrived(TrackingActivityForStudent.this);

//            SettingUtilClass setting = mSettingModel.getSetting();
//            int busIsArraving = setting.getBus_arriving();
//            int busHasArrived = setting.getBus_has_arrived();
            double bArriveingValue = directionsJSONParser.convertMeterToMile(busIsArraving);
            double bHasArrivedValue = directionsJSONParser.convertMeterToMile(busHasArrived);
            Location studentLocation = locationManager.getLastKnownLocation(provider);
            try {
                if (directionsJSONParser.distance(studentLocation.getLatitude(), studentLocation.getLongitude(), location.getLatitude(), location.getLongitude()) < bHasArrivedValue) {
                    if (!hasArrived) {
                        directionsJSONParser.addNotification(TrackingActivityForStudent.this, TrackingActivityForStudent.class, "Bus has arrived");
                        hasArrived = true;
                    }
                } else if (directionsJSONParser.distance(studentLocation.getLatitude(), studentLocation.getLongitude(), location.getLatitude(), location.getLongitude()) < bArriveingValue) {
                    if (isArriving) {
                        directionsJSONParser.addNotification(TrackingActivityForStudent.this, TrackingActivityForStudent.class, "Bus is arriving");
                        isArriving = true;
                    }
                }else{
                    isArriving = false;
                    hasArrived = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LatLng dest = new LatLng(location.getLatitude(), location.getLongitude());
            if (currentMarker != null) {
                currentMarker.remove();
                currentMarker = null;
            }
            if (currentMarker == null) {
                if (mMap != null) {
                    if (location.hasBearing()) {
                        int zoom = getZoomLeavel(location);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(dest)             // Sets the center of the map to current location
                                .zoom(zoom)                   // Sets the zoom
                                .bearing(location.getBearing()) // Sets the orientation of the camera to east
                                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_taxi_cab, getTheme());
                    BitmapDescriptor markerIcon = new DirectionsJSONParser().getMarkerIconFromDrawable(circleDrawable);
                    marker = new MarkerOptions()
                            .position(dest)
                            .title("My Marker")
                            .icon(markerIcon);
                    currentMarker = mMap.addMarker(marker);
                }
            }
        }
    }

    private int getZoomLeavel(Location location){
        int zoom = 18;
        int speed = (int) location.getSpeed();
        switch (speed){
            case 60: case 61: case 62: case 63: case 64: case 65: case 66: case 67: case 68: case 69: case 70:
                zoom = 15;
                break;
            case 50: case 51:case 52: case 53: case 54: case 55: case 56: case 57: case 58: case 59:
                zoom = 16;
                break;
            case 40: case 41:case 42: case 43: case 44: case 45: case 46: case 47: case 48: case 49:
                zoom = 17;
                break;
            case 30: case 31:case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39:
                zoom = 18;
                break;
            default:
                zoom = 18;
        }
        return zoom;
    }

    private void showMarkerOnMap(Location location) {
        MarkerOptions marker = null;
        Marker currentMarker = null;
        if (location != null && mMap != null) {
            LatLng dest = new LatLng(location.getLatitude(), location.getLongitude());
            Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_taxi_cab, getTheme());
            BitmapDescriptor markerIcon = new DirectionsJSONParser().getMarkerIconFromDrawable(circleDrawable);
            if (currentMarker == null) {
                if (mMap != null) {
                    marker = new MarkerOptions()
                            .position(dest)
                            .title("My Marker")
                            .icon(markerIcon);
                    currentMarker = mMap.addMarker(marker);
                }

            }
        }
    }

    private void drawPathOnMap(TrackingRoute route) {
        PolylineOptions rectOptions = new PolylineOptions();
        //this is the color of route
        rectOptions.color(Color.argb(255, 85, 166, 27));
        LatLng startLatLng = null;
        LatLng endLatLng = null;

        if (route.getLocation().size() > 1) {
            for (int i = 0; i < route.getLocation().size(); i++) {
                Double lati = Double.valueOf(route.getLocation().get(i).getLocationLatitude());
                Double longi = Double.valueOf(route.getLocation().get(i).getLocationLongitude());
                LatLng dest = new LatLng(lati, longi);
                if (mMap != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(dest);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.showInfoWindow();
                            return false;
                        }
                    });

                    LatLng latlng = new LatLng(lati,
                            longi);
                    if (i == 0) {
                        startLatLng = latlng;
                        markerOptions.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else if (i == route.getLocation().size() - 1) {
                        endLatLng = latlng;
                        markerOptions.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    }
                    mMap.addMarker(markerOptions);
                    rectOptions.add(latlng);
                    String url;
                    DownloadTask downloadTask = new DownloadTask();
                    if (startLatLng != null && endLatLng != null) {
                        url = getDirectionsUrl(startLatLng, endLatLng);
                        downloadTask.execute(url);
                    }
                }
            }
            mMap.addPolyline(rectOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                ParserTask parserTask = new ParserTask();
                parserTask.execute(result);
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result != null) {
                ArrayList points;
                PolylineOptions lineOptions;
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = result.get(i);
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(8);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);
                }
            }
        }
    }

}
