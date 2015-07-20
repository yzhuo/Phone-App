package com.yate.phoneapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    int defaultZoom = 17;
    Location myLocation, mLastLocation;
    double lon = 0;
    double lat = 0;
    String message;
    GoogleApiClient mClient;
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_maps2);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final View closeButton = findViewById(R.id.closeButton);
        final View clearButtonView = findViewById(R.id.clear);
        final View locationButtonView = findViewById(R.id.location);

        //check if google map is available
        mapFragment.getMapAsync(this);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //******************text activity
        final EditText editText = (EditText) findViewById(R.id.search);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    closeButton.performClick();
                    moveToSearchLocation();
                    handled = true;
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
                locationButtonView.setVisibility(View.VISIBLE);
                clearButtonView.setVisibility(View.GONE);
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
                    locationButtonView.setVisibility(View.GONE);
                    clearButtonView.setVisibility(View.VISIBLE);
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
        Button location = (Button) findViewById(R.id.location);
        location.performClick();
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
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //View locationButton = ((View) findViewById(1).getParent()).findViewById(2);
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
    //****************get my current location****************
    private void getMyLocation(){
        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        myLocation = locationManager.getLastKnownLocation(provider);

        if(myLocation != null){
            lat = myLocation.getLatitude();
            lon = myLocation.getLongitude();
        }
        else
        {
            Toast.makeText(MapsActivity.this, "No Known Location", Toast.LENGTH_SHORT).show();
        }


        LatLng currentLocation = new LatLng(lat, lon);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation,defaultZoom);
        //mMap.animateCamera(cameraUpdate);

    }
    //**************end****************

    //**************move to search location*************
    private void moveToSearchLocation(){
        EditText editText = (EditText) findViewById(R.id.search);
        message = editText.getText().toString();
        Toast.makeText(MapsActivity.this, "Start Translating", Toast.LENGTH_SHORT).show();

        if(Geocoder.isPresent()) {
            try {
                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName("1405 heather ln, Monroe, NC, 28110", 1);
                if (addresses.size() > 0) {
                    lat = addresses.get(0).getLatitude();
                    lon = addresses.get(0).getLongitude();
                    LatLng currentLocation = new LatLng(lat, lon);
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, defaultZoom);
                    mMap.animateCamera(cameraUpdate);
                } else {
                    Toast.makeText(MapsActivity.this, "Invalid Address", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(MapsActivity.this, "Geocoder not available", Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //*********************end


}
