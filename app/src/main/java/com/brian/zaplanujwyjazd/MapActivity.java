package com.brian.zaplanujwyjazd;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double latitude,longitude;
    private List<Place> selectedPlaces;
    private String mode;
    private int pressedButtonId;
    private OptymalizationType optymalizationType;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //set default values:
        optymalizationType = OptymalizationType.DISTANCE;
        pressedButtonId = R.id.driving_button;
        mode = "driving";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //adding icons into buttons:
        Typeface font = Typeface.createFromAsset( getAssets(), "ionicons.ttf" );
        Button drivingButton = findViewById( R.id.driving_button );
        drivingButton.setTypeface(font);
        Button bicyclingButton = findViewById( R.id.bicycling_button );
        bicyclingButton.setTypeface(font);
        Button walkingButton = findViewById( R.id.walking_button );
        walkingButton.setTypeface(font);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    List<Place> getPlaces(){
        Intent intent = getIntent();
        Bundle places = intent.getBundleExtra("places");
        List<Place> selectedPlaces = places.getParcelableArrayList("selectedPlaces");

        //current location must be firs place in selected places list:
        Place temp = null;
        for (Place place : selectedPlaces){
            if(place.getName().equals(getResources().getString(R.string.current_location))){
                temp = place;
            }
        }
        if (temp != null){
            selectedPlaces.remove(temp);
            selectedPlaces.add(0, temp);
        }

        return selectedPlaces;
    }


    private void findRoute(OptymalizationType optymalizationType){
        RouteFinder routeFinder = new RouteFinder(this);
        routeFinder.execute(optymalizationType);
    }

    void setOptymalizationModeListener(){
        Spinner spinner = findViewById(R.id.optymalization_mode_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        optymalizationType = OptymalizationType.DISTANCE;
                        findRoute(optymalizationType);
                        break;
                    case 1:
                        optymalizationType = OptymalizationType.TIME;
                        findRoute(optymalizationType);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    void displayDirections(List<String[]> directions){
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.zoomBy(0));
        displayMarkers(RouteFinder.getOrderedPlaces());
        for(String legs[]: directions){
            for(String directionsList: legs){
                PolylineOptions options = new PolylineOptions();
                options.color(Color.RED);
                options.width(8);
                options.addAll(PolyUtil.decode(directionsList));
                mMap.addPolyline(options);
            }
        }

        LatLng latLng = new LatLng(latitude , longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    private void displayMarkers(List<Place> orderedPlaces){
        LatLng latLng;
        MarkerOptions markerOptions = new MarkerOptions();

        Place currentLocation = null;
        int iterator = 0;

        if(orderedPlaces.get(0).getName().equals(getResources().getString(R.string.current_location))){
            currentLocation = orderedPlaces.get(0);
            iterator = 1;
        }

        if (currentLocation != null){
            //current location:
            latLng = new LatLng(orderedPlaces.get(0).getLatitude(), orderedPlaces.get(0).getLongitude());
            markerOptions.position(latLng);
            markerOptions.title(orderedPlaces.get(0).getName());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);

            //other locations:
            for( int i = iterator; i < orderedPlaces.size(); i++){
                latLng = new LatLng(orderedPlaces.get(i).getLatitude(), orderedPlaces.get(i).getLongitude());
                markerOptions.position(latLng);
                markerOptions.title(i + ". " + orderedPlaces.get(i).getName());
                markerOptions.snippet(orderedPlaces.get(i).getDescription());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOptions);
            }
        }

        //if there is no current location in the tour
        for( int i = iterator; i < orderedPlaces.size(); i++){
            latLng = new LatLng(orderedPlaces.get(i).getLatitude(), orderedPlaces.get(i).getLongitude());
            markerOptions.position(latLng);
            markerOptions.title(orderedPlaces.get(i).getName());
            markerOptions.snippet(orderedPlaces.get(i).getDescription());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);
        }

    }

    void displayAdditionalInfo(){
       //display distance/time matrix:
        StringBuilder distMatrix;
        switch (optymalizationType){
            case DISTANCE:
                distMatrix = new StringBuilder(getString(R.string.distance_matrix_for)
                        + RouteFinder.getOrderedPlaces().size() + getString(R.string.places) + "[m] : \n");
                break;

            case TIME:
                distMatrix = new StringBuilder(getString(R.string.time_matrix_for)
                        + RouteFinder.getOrderedPlaces().size() + getString(R.string.places) + "[s] : \n");
                break;

            default:
                distMatrix = new StringBuilder(getString(R.string.distance_matrix_for)
                        + RouteFinder.getOrderedPlaces().size() + getString(R.string.places) + "\n");
        }


        for (int[] row: RouteFinder.getValuesMatrix()){
            distMatrix.append(Arrays.toString(row)).append("\n");
        }

        TextView detailsTextView = findViewById(R.id.matrix);
        detailsTextView.setText("");
        detailsTextView.setText(distMatrix.toString());

        //display tsp solution:
        String solution = detailsTextView.getText() +  getString(R.string.optimal_route);
        for(Place place: RouteFinder.getOrderedPlaces()){
            solution += place.getName() + " -> ";
        }
        solution += RouteFinder.getOrderedPlaces().get(0).getName() + "\n";

        switch (optymalizationType){
            case DISTANCE:
                solution += getString(R.string.shortest_path) + RouteFinder.getShortestDistance()/1000 + "[km],  "
                        + getString(R.string.longest_path) + RouteFinder.getLongestDistance()/1000 + "[km]";
                break;

            case TIME:
                solution += getString(R.string.fastest_path) + RouteFinder.getShortestDistance()/60 + "[min],  "
                        + getString(R.string.slowest_path) + RouteFinder.getLongestDistance()/60 + "[min]";
                break;
        }
        detailsTextView.setText(solution);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.driving_button):
                if(pressedButtonId != R.id.driving_button){
                    setButtonColor(R.id.driving_button);
                    pressedButtonId = R.id.driving_button;
                    mode = "driving";
                    findRoute(optymalizationType);
                }
                break;

            case (R.id.bicycling_button):
                if(pressedButtonId != R.id.bicycling_button){
                    setButtonColor(R.id.bicycling_button);
                    pressedButtonId = R.id.bicycling_button;
                    mode = "bicycling";
                    findRoute(optymalizationType);
                }
                break;

            case (R.id.walking_button):
                if(pressedButtonId != R.id.walking_button){
                    setButtonColor(R.id.walking_button);
                    pressedButtonId = R.id.walking_button;
                    mode = "walking";
                    findRoute(optymalizationType);
                }
                break;
        }
    }

    private void setButtonColor(int buttonId){
        Button drivingBtn = findViewById(R.id.driving_button);
        Button bicyclingBtn = findViewById(R.id.bicycling_button);
        Button walkingBtn = findViewById(R.id.walking_button);

        switch (buttonId){
            case(R.id.driving_button):
                drivingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonClicked));
                bicyclingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                walkingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                break;
            case(R.id.bicycling_button):
                drivingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                bicyclingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonClicked));
                walkingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                break;
            case (R.id.walking_button):
                drivingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                bicyclingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                walkingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonClicked));
                break;
            default:
                drivingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                bicyclingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                walkingBtn.setBackgroundColor(getResources().getColor(R.color.colorMapButtonDefault));
                break;
        }
    }

    void showProgressDialog(Context context){
        dialog = new ProgressDialog(context);
        dialog.setTitle(getString(R.string.please_wait));
        dialog.setMessage(getString(R.string.finding_best_route));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void dissmisProgressDialog(){
        dialog.dismiss();
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    Boolean isStartingLocationSet = false;
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //stop location updates
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            Log.d("onLocationChanged", "Removing Location Updates");
//        }

        if(!isStartingLocationSet){
            selectedPlaces = getPlaces();
            isStartingLocationSet = true;
            //in async task:
            //get googleDistanceMatrixUrl, download and parse json, build valuesMatrix:
            findRoute(optymalizationType);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    locationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    //getters:
    List<Place> getSelectedPlaces() {
        return selectedPlaces;
    }

    String getMode() {
        return mode;
    }
}