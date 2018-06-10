package com.brian.zaplanujwyjazd;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.util.ArrayList;
import java.util.List;

public class FindPlaceActivity extends AppCompatActivity implements LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private Spinner categorySpinner;
    private Switch switchButton;
    private EditText categoryEditText;
    private EditText radiousEditText;
    private String category;
    private ArrayList<Place> foundPlaces;
    private ArrayList<Place> selectedPlaces;
    private Place selectedPlace;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private double latitude;
    private double longitude;
    private int radious = 50000;
    private Place currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_place);

        //handle ui controls
        switchButton = findViewById(R.id.layout_switcher);
        categorySpinner = findViewById(R.id.select_category_spinner);
        radiousEditText = findViewById(R.id.radious_edit_text);
        categoryEditText = findViewById(R.id.select_category_editText);

        category = (String) categorySpinner.getItemAtPosition(categorySpinner.getSelectedItemPosition());

        radiousEditText.setText("5000");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(1000);


        selectedPlaces = getSelectedPlaces();
        setListValues();
        setPlaceSelectingListener();
        setViewSwitcher();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.button_select_place):
                if (selectedPlace != null) {
                    Bundle bundle = new Bundle();
                    if (!selectedPlaces.contains(selectedPlace)) {
                        selectedPlaces.add(selectedPlace);
                        bundle.putParcelableArrayList("selectedPlaces", selectedPlaces);

                        Intent intent = new Intent(this, PlacesListActivity.class);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, getString(R.string.message_this_place_has_already_been_selected),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.message_firs_select_place),
                            Toast.LENGTH_SHORT).show();
                }

                break;

            case (R.id.button_find_place):
                radious = Integer.valueOf(radiousEditText.getText().toString());
                if(switchButton.isChecked()){
                    category = categoryEditText.getText().toString();
                }
                if(!switchButton.isChecked()){
                    category = (String) categorySpinner.getItemAtPosition(categorySpinner.
                            getSelectedItemPosition());
                }
                category = category.replaceAll(" ","+");
                String url = getUrl(latitude, longitude, category);

                PlaceFinder placeFinder = new PlaceFinder(this);
                placeFinder.execute(url);

                break;
        }
    }

    public void setViewSwitcher(){
        final ViewSwitcher viewSwitcher = findViewById(R.id.view_switcher);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    viewSwitcher.showPrevious();
                }
                else {
                    viewSwitcher.showNext();
                }
            }
        });
    }

    public void setListValues() {
        ListView foundPlacesList = findViewById(R.id.found_places_list);
        MyListAdapter adapter;
        if (category.equals("Moje miejsca") || category.equals("Moje+miejsca")
                || category.equals("My places") || category.equals("My+places")) {
            currentLocation = new Place();
            currentLocation.setName(getResources().getString(R.string.current_location));
            currentLocation.setDescription(getResources().getString(R.string.current_location_desc));
            foundPlaces = (ArrayList<Place>) getPlacesFromDatabase();
            foundPlaces.add(0, currentLocation);
        }
            adapter = new MyListAdapter(foundPlaces,
                    R.layout.found_places_list_element, this);

        foundPlacesList.setAdapter(adapter);
    }

    public void setPlaceSelectingListener() {
        ListView foundPlacesList = findViewById(R.id.found_places_list);

        foundPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedPlace = foundPlaces.get(position);
            }
        });
    }

    public List<Place> getPlacesFromDatabase() {
        foundPlaces = new ArrayList<>();
        try {
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            Cursor cursor = db.query("PLACES",
                    new String[]{"_id", "NAME", "LATITUDE", "LONGITUDE", "DESCRIPTION"},
                    null, null,
                    null, null, "NAME ASC");

            Place place;
            if (cursor.moveToFirst()) {
                place = new Place(cursor.getInt(0), cursor.getString(1),
                        cursor.getDouble(2), cursor.getDouble(3),
                        cursor.getString(4));
                foundPlaces.add(place);

                while (cursor.moveToNext()) {
                    place = new Place(cursor.getInt(0), cursor.getString(1),
                            cursor.getDouble(2), cursor.getDouble(3),
                            cursor.getString(4));
                    foundPlaces.add(place);
                }
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, getString(R.string.database_is_not_available),
                    Toast.LENGTH_SHORT).show();
        }
        return foundPlaces;
    }

    public ArrayList<Place> getSelectedPlaces() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            selectedPlaces = bundle.getParcelableArrayList("selectedPlaces");
        } else {
            selectedPlaces = new ArrayList<>();
        }
        return selectedPlaces;
    }

    private String getUrl(double latitude, double longitude, String keyword) {
        CheckBox openNowCheckBox = findViewById(R.id.open_now_checkBox);
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + radious);
        googlePlaceUrl.append("&keyword=" + keyword);
        googlePlaceUrl.append("&language=pl");
        googlePlaceUrl.append("&sensor=true");
        if(openNowCheckBox.isChecked()){
            googlePlaceUrl.append("&opennow=true");
        }
        googlePlaceUrl.append("&key=" + "AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");

        Log.d("FindPlaceActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
    }

    public void setFoundPlaces(ArrayList<Place> foundPlaces) {
        this.foundPlaces = foundPlaces;
    }
}

