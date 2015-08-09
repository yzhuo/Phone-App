package com.yate.phoneapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback{
    //Google API client
    private GoogleApiClient mGoogleApiClient;
    //******************
    private LocationRequest mLocationRequest;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    int defaultZoom = 17;
    Location myLocation;
    double lon = 0;
    double lat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //End************

        //Location Request Object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10*1000)  //10seconds, in ms
                .setFastestInterval(1 * 1000);  // 1 second, in ms
        //End *************************

        final View closeButton = findViewById(R.id.closeButton);
        final View clearButtonView = findViewById(R.id.clear);
        final View locationButtonView = findViewById(R.id.location);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //check if google map is available
        mapFragment.getMapAsync(this);


        //******************text activity
        final EditText editText = (EditText) findViewById(R.id.search);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    closeButton.performClick();
                    Button send = (Button) findViewById(R.id.send);
                }
                return handled;
            }
        });
        //****************text activity end

        //******************close button action***********
        closeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hideKeyboard(v);
                //locationButtonView.setVisibility(View.VISIBLE);
                //clearButtonView.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                editText.clearFocus();
            }
        });
        //*************end

        //***********show close button on click edit text
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                   // locationButtonView.setVisibility(View.GONE);
                    //clearButtonView.setVisibility(View.VISIBLE);
                    closeButton.setVisibility(View.VISIBLE);
                }
            }
        });
        //******show keyboard end***

        //**************reset activity
        Button reset = (Button)findViewById(R.id.clear);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText search = (EditText) findViewById(R.id.search);
                search.setText("");
            }
        });
        //************reset activity end

        //***********location button******************
        Button location = (Button)findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //mapFragment.getView().findViewById(0x2).performClick();
                getMyLocation();
                //Toast.makeText(MapsActivity.this, "Click", Toast.LENGTH_SHORT).show();
            }
        });
        //***************end****************
        //Criteria criteria = new Criteria();
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //String provider = locationManager.getBestProvider(criteria, true);
        //myLocation = locationManager.getLastKnownLocation(provider);



    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        //Intent intent = getIntent();
        //String message=intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //***user search location****
        /*
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses=null;
        try {
            addresses =  geocoder.getFromLocationName(message,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses.size()>0) {
            double lat = addresses.get(0).getLatitude() * 1E6;
            double lon = addresses.get(0).getLongitude() * 1E6;
            LatLng currentLocation = new LatLng(lat, lon);
            map.addMarker(new MarkerOptions().position(currentLocation).title(message));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, defaultZoom));
        }
        */
        //*****end****
        //***phone location*****
        //myLocation.getLocation(getApplicationContext(),locationResult);
        /*
        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        myLocation = locationManager.getLastKnownLocation(provider);


        if(myLocation != null){
            lat = myLocation.getLatitude();
            lon = myLocation.getLongitude();
        }


        LatLng currentLocation = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, defaultZoom));
        */


        //*****end*****


        /*
            AlertDialog.Builder adb = new AlertDialog.Builder(MapsActivity.this);
            adb.setTitle("Google Map");
            adb.setMessage("Unable to find the location");
            adb.setPositiveButton("Close", null);
            adb.show();
        */


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //*****************set up padding for the map icon***********
        RelativeLayout topMenu = (RelativeLayout) findViewById(R.id.map_tool_bar_top);
        RelativeLayout botMenu = (RelativeLayout) findViewById(R.id.map_tool_bar_bottom);
        mMap.setPadding(0, 140, 0, 140);
        //*******************padding end*****************
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    //*************hide keyboard ******************
    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //**************end hide keyboard************

    public void openFilter(View view){
        Intent intent = new Intent(this, filter.class);
        startActivity(intent);
    }

    private void getMyLocation(){
        /*
        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        myLocation = locationManager.getLastKnownLocation(provider);


        if(myLocation != null){
            lat = myLocation.getLatitude();
            lon = myLocation.getLongitude();
        }


        LatLng currentLocation = new LatLng(lat, lon);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation,defaultZoom);
        mMap.animateCamera(cameraUpdate);
        */

        double currentLatitude = myLocation.getLatitude();
        double currentLongitude = myLocation.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom);
        mMap.animateCamera(cameraUpdate);
    }

    private void handleNewLocation(Location location){
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended.. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()){
            try {
                //start an activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }catch (IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        } else {
            Log.i(TAG,"Location services connection failed with code " +connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }
}
