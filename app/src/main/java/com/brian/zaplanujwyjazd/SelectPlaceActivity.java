package com.brian.zaplanujwyjazd;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class SelectPlaceActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private String category;
    private List<Place> foundPlaces;
    private ArrayList<Place> selectedPlaces;
    private Place selectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_place);

        selectedPlaces = getSelectedPlaces();
        setSpinnerAdatper();
        setSpinnerListener();
        setSelectPlaceListener();
    }

    public ArrayList<Place> getSelectedPlaces(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null){
            selectedPlaces = bundle.getParcelableArrayList("selectedPlaces");
        }
        else{
            selectedPlaces = new ArrayList<>();
        }
        return selectedPlaces;
    }

    public void setSelectPlaceListener(){
        ListView foundPlacesList = (ListView) findViewById(R.id.found_places_list);

        foundPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            selectedPlace = foundPlaces.get(position);
            }
        });

    }

    public void setListValues(){
        ListView foundPlacesList = (ListView) findViewById(R.id.found_places_list);
        MyListAdapter adapter = new MyListAdapter(foundPlaces, R.layout.found_places_list_element, this);
        foundPlacesList.setAdapter(adapter);
    }

    public void setSpinnerAdatper(){
        Collection<String> categories =
                new TreeSet<>(Collator.getInstance());

        try {
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            Cursor cursor = db.query("PLACES",
                    new String[]{"_id", "CATEGORY1", "CATEGORY2", "CATEGORY3"},
                    null, null, null, null, null);

            if(cursor.moveToFirst()){
                categories.add(cursor.getString(1));
                categories.add(cursor.getString(2));
                categories.add(cursor.getString(3));
            }

            while (cursor.moveToNext()){
                categories.add(cursor.getString(1));
                categories.add(cursor.getString(2));
                categories.add(cursor.getString(3));
            }
            cursor.close();
            db.close();
        }
        catch (SQLiteException e){
            Toast.makeText(this, getString(R.string.database_is_not_available), Toast.LENGTH_SHORT).show();
        }
//      removing empty strings when some places have less than 3 categories
        categories.removeAll(Collections.singletonList(""));

        String[] categoriesArray = new String[categories.size()];
        categoriesArray = categories.toArray(categoriesArray);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoriesArray);
        categorySpinner = (Spinner) findViewById(R.id.select_category_spinner);
        categorySpinner.setAdapter(spinnerAdapter);
    }

    public void setSpinnerListener(){
        categorySpinner = (Spinner) findViewById(R.id.select_category_spinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                category = (String) categorySpinner.getItemAtPosition(position);
                getPlacesFromDatabase();
                setListValues();
                selectedPlace = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.button_select_place):
                if(selectedPlace != null){
                    Bundle bundle = new Bundle();
                    Spinner weightSpinner = (Spinner) findViewById(R.id.select_weight_spinner);
                    int weight = Integer.valueOf(String.valueOf(weightSpinner.getSelectedItem()));
                    if(!selectedPlaces.contains(selectedPlace)){
//                        selectedPlace.setWeight(weight);
                        selectedPlaces.add(selectedPlace);
                        bundle.putParcelableArrayList("selectedPlaces", selectedPlaces);

                        Intent intent = new Intent(this, PlacesListActivity.class);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(this, getString(R.string.message_this_place_has_already_been_selected),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, getString(R.string.message_firs_select_place),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void getPlacesFromDatabase() {
        foundPlaces = new ArrayList<>();
        try {
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            Cursor cursor = db.query("PLACES",
                    new String[]{"_id", "NAME", "LATITUDE", "LONGITUDE", "DESCRIPTION" },
                    "CATEGORY1 = ? OR CATEGORY2 = ? OR CATEGORY3 = ?",
                    new String[]{category, category, category},
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
        }
        catch (SQLiteException e) {
            Toast.makeText(this, getString(R.string.database_is_not_available),
                    Toast.LENGTH_SHORT).show();
        }
    }

}