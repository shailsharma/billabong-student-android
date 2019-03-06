package in.securelearning.lil.android.tracking.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.app.databinding.ActivityTrackingTeacherBinding;
import in.securelearning.lil.android.base.dataobjects.GroupMember;
import in.securelearning.lil.android.base.dataobjects.TrackingRoute;
import in.securelearning.lil.android.home.views.widget.AndroidPermissions;
import in.securelearning.lil.android.tracking.InjectorTracking;
import in.securelearning.lil.android.tracking.model.TrackingMapModel;
import in.securelearning.lil.android.tracking.view.DirectionsJSONParser;
import in.securelearning.lil.android.tracking.view.fragment.DialogFragment;
import in.securelearning.lil.android.tracking.view.utils.GroupMemberExt;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TrackingActivityForTeacher extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String mprovider;
    Marker currentMarker = null;
    MarkerOptions marker = null;
    ActivityTrackingTeacherBinding mBinding;
    private boolean postId = false;
    String selectedGroupId = null;
    String routeType;
    int selectedGroupNumber = -1;
    public static ArrayList<GroupMemberExt> stList;
    public static String intimationPostId = "";
    String selectedRouteName;

    @Inject
    TrackingMapModel mTrackingModel;


    public static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectorTracking.INSTANCE.getComponent().inject(this);
        new DirectionsJSONParser().checkIfLocationServiceIsOnOrOff(TrackingActivityForTeacher.this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tracking_teacher);
        setTitle("Tracking For Teacher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mBinding.btnStart.setOnClickListener(this);
        mBinding.btnStop.setOnClickListener(this);
        mBinding.btnShowRouteStudent.setOnClickListener(this);
        mBinding.btnShowRouteStudent.setVisibility(View.INVISIBLE);
        mBinding.cardBtnStop.setVisibility(View.INVISIBLE);
        mBinding.cardBtnStart.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showPopupForSelectionOfGroup() {
        final boolean[] flag = {false};
        HashMap<String, String> mapList = getGroupDataFromDataBase();
        final String[] items = new String[mapList.size()];
        final String[] itemsId = new String[mapList.size()];
        Iterator<String> keyIterator = mapList.keySet().iterator();
        for (int i = 0; i < mapList.size(); i++) {
            String key = keyIterator.next();
            items[i] = mapList.get(key);
            itemsId[i] = key;
            System.out.println("Code=" + key + "  Country=" + mapList.get(key));
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(TrackingActivityForTeacher.this);
        builder.setTitle("Select The Group");
        builder.setSingleChoiceItems(items, selectedGroupNumber,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        flag[0] = true;
                        selectedRouteName = items[item];
                        selectedGroupId = itemsId[item];
                        selectedGroupNumber = item;
                        Toast.makeText(getApplicationContext(), items[item] + " item",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("Pick Up", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (flag[0]) {
                    mBinding.cardBtnStart.setVisibility(View.INVISIBLE);
                    mBinding.cardBtnStop.setVisibility(View.VISIBLE);
                    mBinding.btnShowRouteStudent.setVisibility(View.VISIBLE);
                    Toast.makeText(TrackingActivityForTeacher.this, "Pick Up", Toast.LENGTH_SHORT)
                            .show();
                    routeType = "Pick Up";
                    getRouteData(selectedGroupId);
                    startTripFinally();
                    stList = getStudentListFromDataBase(selectedGroupId);
                }
            }
        });
        builder.setNegativeButton("Drop", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (flag[0]) {
                    mBinding.cardBtnStart.setVisibility(View.INVISIBLE);
                    mBinding.cardBtnStop.setVisibility(View.VISIBLE);
                    mBinding.btnShowRouteStudent.setVisibility(View.VISIBLE);
                    Toast.makeText(TrackingActivityForTeacher.this, "Drop Up", Toast.LENGTH_SHORT)
                            .show();
                    routeType = "Drop Up";
                    getRouteData(selectedGroupId);
                    startTripFinally();
                    stList = getStudentListFromDataBase(selectedGroupId);
                }
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectedGroupId = null;
                selectedGroupNumber = -1;
                if (mMap != null) {
                    mMap.clear();
                }
            }
        });

        builder.setCancelable(false);
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF00FF"));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FFBF00"));
                alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED);
            }
        });
        alert.show();
    }

    private HashMap<String, String> getGroupDataFromDataBase() {
        HashMap<String, String> mapList = mTrackingModel.getGroupListFromDataBase();
        return mapList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AndroidPermissions.checkLocationPermission(TrackingActivityForTeacher.this);
            return;
        }
        mMap.setMyLocationEnabled(true);
        getCurrentLocationForFirsttime();
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
                mprovider = locationManager.getBestProvider(criteria, true);
                Location loc = null;
                LatLng sydney;
                if (mprovider != null && !mprovider.equals("")) {
                    while (loc == null) {
                        if (ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            AndroidPermissions.checkLocationPermission(TrackingActivityForTeacher.this);
                            return;
                        }
                        loc = locationManager.getLastKnownLocation(mprovider);
                        if (loc != null) {
                            e.onNext(loc);
                            e.onComplete();
                        } else {
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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        return null;
    }


    private void drawPathOnMap(TrackingRoute route) {
        PolylineOptions rectOptions = new PolylineOptions();
        //this is the color of route
        rectOptions.color(Color.argb(255, 85, 166, 27));
        LatLng startLatLng = null;
        LatLng endLatLng = null;
        if (mMap != null) {
            mMap.clear();
        }
        if (route.getLocation().size() > 1) {
            for (int i = 0; i < route.getLocation().size(); i++) {
                Double lati = Double.valueOf(route.getLocation().get(i).getLocationLatitude());
                Double longi = Double.valueOf(route.getLocation().get(i).getLocationLongitude());
                LatLng dest = new LatLng(lati, longi);
                if (mMap != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(dest);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.showInfoWindow();
                            return false;
                        }
                    });

                    LatLng latlng = new LatLng(lati, longi);
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

    private void getRouteData(final String selectedGroupId) {
        Observable.create(new ObservableOnSubscribe<SparseArray<TrackingRoute>>() {
            @Override
            public void subscribe(ObservableEmitter<SparseArray<TrackingRoute>> e) throws Exception {
                SparseArray<TrackingRoute> route = mTrackingModel.getRouteDataFromDataBase(selectedGroupId);
                if (route != null && route.size() > 0) {
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
                        if (routeType.equalsIgnoreCase("Pick Up")) {
                            drawPathOnMap(locationData.get(0));
                        } else {
                            drawPathOnMap(locationData.get(1));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
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

    @Override
    public void onClick(View v) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TrackingActivityForTeacher.this);
        android.app.AlertDialog alert;
        switch (v.getId()) {
            case R.id.btn_start:
                builder.setTitle("Are you sure, want to start the trip");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        showPopupForSelectionOfGroup();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                alert = builder.create();
                alert.show();
                break;

            case R.id.btn_stop:
                builder.setTitle("Are you sure, want to stop the trip");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        try {
                            mTrackingModel.traceRoute(locationManager.getLastKnownLocation(mprovider), "stop", selectedGroupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    if (!TextUtils.isEmpty(s)) {
                                        locationManager.removeUpdates(TrackingActivityForTeacher.this);
                                        if (ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            AndroidPermissions.checkLocationPermission(TrackingActivityForTeacher.this);
                                            return;
                                        }
                                        selectedGroupId = null;
                                        selectedGroupNumber = -1;
                                        if (mMap != null) {
                                            mMap.clear();
                                        }
                                        mBinding.btnShowRouteStudent.setVisibility(View.INVISIBLE);
                                        mBinding.cardBtnStop.setVisibility(View.INVISIBLE);
                                        mBinding.cardBtnStart.setVisibility(View.VISIBLE);
                                        mMap.setMyLocationEnabled(false);
                                        DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                                        directionsJSONParser.addNotification(TrackingActivityForTeacher.this, TrackingActivityForTeacher.class, "Your trip successfully stopped");
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                alert = builder.create();
                alert.show();
                break;
            case R.id.btn_show_route_student:
                showRouteStudentListPopup();
                break;
        }
    }

    private void startTripFinally() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        mprovider = locationManager.getBestProvider(criteria, true);
        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AndroidPermissions.checkLocationPermission(TrackingActivityForTeacher.this);
                return;
            }
            mMap.setMyLocationEnabled(true);
            final Location location = locationManager.getLastKnownLocation(mprovider);
            if (location != null) {
                mTrackingModel.traceRoute(location, "start", selectedGroupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        postId = true;
                        DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                        directionsJSONParser.addNotification(TrackingActivityForTeacher.this, TrackingActivityForTeacher.class, "Route has started");
                        if (ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            AndroidPermissions.checkLocationPermission(TrackingActivityForTeacher.this);
                            return;
                        }
                        locationManager.requestLocationUpdates(1000, 5, criteria, TrackingActivityForTeacher.this, TrackingActivityForTeacher.this.getMainLooper());
                        if (location != null)
                            onLocationChanged(location);
                        else
                            Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getBaseContext(), "No Location Found, Please start trip again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showRouteStudentListPopup() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment myDialogFragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putString("routeType", routeType);
        args.putString("selectedGroupId", selectedGroupId);
        args.putString("selectedRouteName", selectedRouteName);
        myDialogFragment.setArguments(args);
        myDialogFragment.show(fm, "dialog_fragment");
    }

    private ArrayList<GroupMemberExt> getStudentListFromDataBase(String selectedGroupId) {
        ArrayList<GroupMember> list = mTrackingModel.getStudentListFromDataBase(selectedGroupId);
        stList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stList.add(new GroupMemberExt(list.get(i)));
        }
        return stList;
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
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
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

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            if (jsonData[0] != null) {
                try {
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            if (result != null) {
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
                    lineOptions.width(12);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);
                }
            }
        }
    }

    //Location Class Override methods
    @Override
    public void onLocationChanged(final Location location) {
        if (location != null && mMap != null) {
            LatLng dest = new LatLng(location.getLatitude(), location.getLongitude());
            if (currentMarker != null) {
                currentMarker.remove();
                currentMarker = null;
            }
            if (currentMarker == null) {
                if (mMap != null) {
                    if (location.hasBearing()) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(dest)             // Sets the center of the map to current location
                                .zoom(18)                   // Sets the zoom
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

            // Here we check if curent location is stops of route54r
            double lat2 = location.getLatitude();
            double lng2 = location.getLongitude();
            Double lat1 = 24.578181; // This is destination lat,
            Double lng1 = 73.741018; // // This is destination lang
            //Here we get value from Setting and use it for send notification according setting value

//            int busHasArrived = PreferenceSettingUtilClass.getBus_has_arrived(TrackingActivityForTeacher.this);
//            double sValue = new DirectionsJSONParser().convertMeterToMile(busHasArrived);
//            double sValue = .00031d; // for demo purpose
            if (postId && selectedGroupId != null) {
//                if (new DirectionsJSONParser().distance(lat1, lng1, lat2, lng2) < sValue) { // if distance < 0.0031 miles we take locations as equal 20 meter
//                    mTrackingModel.traceRoute(location, "stop", selectedGroupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
//                        @Override
//                        public void accept(String s) throws Exception {
//                            if (!TextUtils.isEmpty(s)) {
//                                locationManager.removeUpdates(TrackingActivityForTeacher.this);
//                                if (ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivityForTeacher.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                    AndroidPermissions.checkLocationPermission(TrackingActivityForTeacher.this);
//                                    return;
//                                }
//                                selectedGroupId = null;
//                                selectedGroupNumber = -1;
//                                if (mMap != null) {
//                                    mMap.clear();
//                                }
//                                mBinding.btnShowRouteStudent.setVisibility(View.INVISIBLE);
//                                mBinding.cardBtnStop.setVisibility(View.INVISIBLE);
//                                mBinding.cardBtnStart.setVisibility(View.VISIBLE);
//                                mMap.setMyLocationEnabled(false);
//                                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
//                                directionsJSONParser.addNotification(TrackingActivityForTeacher.this, TrackingActivityForTeacher.class, "Your trip successfully delivered");
//                            }
//                        }
//                    }, new Consumer<Throwable>() {
//
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            throwable.printStackTrace();
//                        }
//                    });
//                } else {
//                    Toast.makeText(TrackingActivityForTeacher.this, "location "+location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                mTrackingModel.traceRoute(location, null, selectedGroupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
//                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
