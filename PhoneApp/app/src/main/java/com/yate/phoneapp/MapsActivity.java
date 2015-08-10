package com.yate.phoneapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

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
    public String searchAddress;

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
                    searchAddress = editText.getText().toString();
                    setSearchAddress(searchAddress);
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



    }

    @Override
    public void onMapReady(GoogleMap map) {


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
        //mMap.setMyLocationEnabled(true);

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

    //used when my current location button push
    private void getMyLocation(){

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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom);
        mMap.animateCamera(cameraUpdate);
    }

    //handle geocode jason request
    private void setSearchAddress(String address){
        if(address == null ||address.equals("")){
            Toast.makeText(getBaseContext(),"No loction is entered",Toast.LENGTH_LONG).show();
        } else {
            String url="https://maps.googleapis.com/maps/api/geocode/json?";
            try{
                url = url+"address="+URLEncoder.encode(address,"utf-8")+"&sensor=false";
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
    }

    //downloading the data
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            //creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            //connecting to the url
            urlConnection.connect();
            //reading data from url
            iStream=urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line=br.readLine())!=null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch(Exception e){
            Log.d("downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //creating Asynctask to start downloading
    private class DownloadTask extends AsyncTask<String, Integer, String>{
        String data = null;
        @Override
        protected String doInBackground(String... url){
            try{
                data = downloadUrl(url[0]);
            } catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    //parse geocoding data
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
        JSONObject jObject;

        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData){
            List<HashMap<String,String>> places =null;
            GeocodeJSONParser parser = new GeocodeJSONParser();
            try{
                jObject = new JSONObject(jsonData[0]);
                places = parser.parse(jObject);
            } catch(Exception e){
                Log.d("Exception", e.toString());
            }
            return places;
        }

        //executed after completion of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list) {
            mMap.clear();
            //mark all the return result from geocode
            for(int i=0;i<list.size();i++){
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String,String> hmPlace = list.get(i);
                double lat = Double.parseDouble(hmPlace.get("lat"));
                double lng = Double.parseDouble(hmPlace.get("lng"));
                Toast.makeText(getBaseContext(),"lat"+lat+",lng"+lng,Toast.LENGTH_LONG).show();
                String name = hmPlace.get("formatted_address");
                LatLng latLng = new LatLng(lat,lng);
                markerOptions.position(latLng);
                markerOptions.title(name);
                mMap.addMarker(markerOptions);
                if(i==0){
                   mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        }
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
