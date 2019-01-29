package com.brian.zaplanujwyjazd;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

public class Place implements Parcelable{
    @Getter
    private int id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private double latitude;
    @Getter
    @Setter
    private double longitude;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private float rating;


    public Place() {
    }

    public Place(int id, String name, double latitude, double longitude, String description) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public Place(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Place(Parcel in) {
        id = in.readInt();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        rating = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(description);
        parcel.writeFloat(rating);
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public boolean equals(Object otherObject){
        if (getClass() != otherObject.getClass())
            return false;
        Place other = (Place) otherObject;
            return name.equals(other.name);
    }

    @Override
    public int hashCode(){
        return id;
    }
    
}
