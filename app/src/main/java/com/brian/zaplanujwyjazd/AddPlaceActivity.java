package com.brian.zaplanujwyjazd;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddPlaceActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText descriptionEditText;
    private String name;
    private Double latitude, longitude;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

    }

    public boolean isEnteredDataValid(){
        nameEditText = findViewById(R.id.name_edit_text);
        latitudeEditText = findViewById(R.id.latitude_edit_text);
        longitudeEditText = findViewById(R.id.longitude_edit_text);

            return !(String.valueOf(nameEditText.getText()).isEmpty()
                    || String.valueOf(latitudeEditText.getText()).isEmpty()
                    || String.valueOf(longitudeEditText.getText()).isEmpty()
                    || (Double.valueOf(latitudeEditText.getText().toString()) > 90)
                    || (Double.valueOf(latitudeEditText.getText().toString()) < -90)
                    || (Double.valueOf(longitudeEditText.getText().toString()) > 180)
                    || (Double.valueOf(longitudeEditText.getText().toString()) < -180));
    }

    public void getValues(){
        nameEditText = findViewById(R.id.name_edit_text);
        latitudeEditText = findViewById(R.id.latitude_edit_text);
        longitudeEditText = findViewById(R.id.longitude_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);

        name = String.valueOf(nameEditText.getText());
        latitude = Double.valueOf(latitudeEditText.getText().toString());
        longitude = Double.valueOf(longitudeEditText.getText().toString());
        if(String.valueOf(descriptionEditText.getText()).isEmpty()){
            description = "";
        }
        else{
            description = String.valueOf(descriptionEditText.getText());
        }
    }

    public void addPlaceToDatabase(View view){
        if(isEnteredDataValid()){
            getValues();
            try{
                SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                DatabaseHelper.insertPlace(db, name, latitude, longitude, description);
                db.close();

                Toast.makeText(this, getString(R.string.added) + " " + name + " " +
                                getString(R.string.to_database), Toast.LENGTH_LONG).show();
            }
            catch (NumberFormatException e){
                Toast.makeText(this, getString(R.string.database_is_not_available), Toast.LENGTH_SHORT).show();
            }

            //restarting activity co clear all editTextViews and spinners
            finish();
            startActivity(getIntent());
        }

        else{
            Toast.makeText(this, getString(R.string.enter_valid_data), Toast.LENGTH_SHORT).show();
        }
    }
}
