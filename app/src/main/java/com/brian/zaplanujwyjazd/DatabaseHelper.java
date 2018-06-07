package com.brian.zaplanujwyjazd;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

 class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Places";
    private static final int DB_VERSION = 1;

    DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase (SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion < newVersion){
            db.execSQL("CREATE TABLE PLACES (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +"NAME TEXT, "
                    +"LATITUDE REAL, "
                    +"LONGITUDE REAL, "
                    +"DESCRIPTION TEXT);");

            insertPlace(db, "Moje mieszkanie", 51.245975, 22.547053,  "Marii Curie-Skłodowskiej 36");
            insertPlace(db, "Uczelnia", 51.2364559,22.5479532,  "Nadbystrzycka 38A, 20-618 Lublin");
            insertPlace(db, "Mieszkanie Oskara", 51.234534, 22.525498, "Jana Sawy 15, 20-400 Lublin");
        }
    }

    static void insertPlace(SQLiteDatabase db, String name, double latitude, double longtitude,
                            String description){
        ContentValues placeValues = new ContentValues();
        placeValues.put("NAME", name);
        placeValues.put("LATITUDE", latitude);
        placeValues.put("LONGITUDE", longtitude);
        placeValues.put("DESCRIPTION", description);
        db.insert("PLACES", null, placeValues);
    }
}
