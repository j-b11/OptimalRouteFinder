package com.brian.zaplanujwyjazd;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteFinder extends AsyncTask<OptymalizationType, String, Void> {

    private String gDistanceMatrixData;
    private String gDirectionsData;
    private static int[][] valuesMatrix;
    private List<String[]> directions;
    private static int shortestDistance;
    private static int longestDistance;
    private static List<Integer> shortestPath;
    private static List<Place> orderedPlaces;

    private long startTime;
    private long endTime;

    @SuppressLint("StaticFieldLeak")
    private MapActivity mapActivity;


    RouteFinder(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }

    @Override
    protected void onPreExecute() {
        mapActivity.showProgressDialog(mapActivity);
        System.out.println("Start pomiaru czasu. ");
        startTime = System.nanoTime();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(OptymalizationType... params) {
        OptymalizationType optymalizationType = params[0];
        UrlDownloader urlDownloader = new UrlDownloader();
        DataParser dataParser = new DataParser();

        //GOOGLE DISTANCE MATRIX WORK:
        String distMatrixUrl = getDistanceMatrixUrl(mapActivity.getPlaces());

        try {
            gDistanceMatrixData = urlDownloader.readUrl(distMatrixUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (optymalizationType){
            case DISTANCE:
                valuesMatrix = dataParser.parseDistance(gDistanceMatrixData);
                break;

            case TIME:
                valuesMatrix = dataParser.parseTime(gDistanceMatrixData);
                break;

            default:
                valuesMatrix = dataParser.parseDistance(gDistanceMatrixData);
        }
        solveTSP(valuesMatrix);

        orderedPlaces = new ArrayList<>();
        for(int i: shortestPath){
            orderedPlaces.add(mapActivity.getSelectedPlaces().get(i));
        }

        //GOOGLE DIRECTIONS WORK:
        String directionsUrl = getDirectionsUrl(orderedPlaces);

        try {
            gDirectionsData = urlDownloader.readUrl(directionsUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        directions = dataParser.parseDirections(gDirectionsData);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mapActivity.dissmisProgressDialog();
        mapActivity.displayDirections(directions);
        mapActivity.displayAdditionalInfo();
        endTime = System.nanoTime();
        double totalTime = (double)(endTime - startTime)/1000000000;
        System.out.printf("Koniec pomiaru czasu. Wyznaczenie trasy dla " +
                mapActivity.getSelectedPlaces().size() +
                " miejsc trwało: %.2f s. \n", totalTime);
        mapActivity.setOptymalizationModeListener();
        super.onPostExecute(null);
    }

    private String getDistanceMatrixUrl(List<Place> places){
        StringBuilder distMatrixUrl = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&");
        distMatrixUrl.append("origins=");
        for(Place place: places){
            distMatrixUrl.append(place.getLatitude() + "," + place.getLongitude() + "|");
        }

        distMatrixUrl.append("&destinations=");
        for(Place place: places){
            distMatrixUrl.append(place.getLatitude() + "," + place.getLongitude() + "|");
        }

        distMatrixUrl.append("&mode=" + mapActivity.getMode() + "&key="+ mapActivity.getString(R.string.GoogleMapsDirectionsApiKey));
        Log.d("getDistMatrixUrl", distMatrixUrl.toString());
        return distMatrixUrl.toString();
    }

    private String getDirectionsUrl(List<Place> places){
        StringBuilder directionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionsUrl.append("origin=" + places.get(0).getLatitude() + "," + places.get(0).getLongitude());
        directionsUrl.append("&destination=" + places.get(0).getLatitude() + "," + places.get(0).getLongitude());
        directionsUrl.append("&waypoints=");

        for(int i=1; i<places.size(); i++){
            directionsUrl.append(places.get(i).getLatitude() + "," + places.get(i).getLongitude() + "|");
        }

        directionsUrl.append("&mode=" + mapActivity.getMode() + "&key="+ mapActivity.getString(R.string.GoogleMapsDirectionsApiKey));
        Log.d("getDirectionsUrl", directionsUrl.toString());
        return directionsUrl.toString();
    }

    static void solveTSP(int[][] valuesMatrix) {
        shortestDistance = Integer.MAX_VALUE;
        longestDistance = Integer.MIN_VALUE;
        shortestPath = null;

        int totalPlaces = valuesMatrix.length;
        ArrayList<Integer> places = new ArrayList<>();
        for(int i=0; i<totalPlaces; i++){
            places.add(i);
        }
        int startPlace = places.get(0);
        int currentDistance = 0;
        bruteForceSearch(valuesMatrix, places, startPlace, currentDistance);
    }

    private static void bruteForceSearch(int[][] valuesMatrix, List<Integer> places,
                                         int startPlace, int currentDistance) {
        if(startPlace < places.size()-1){
            for(int i=startPlace; i < places.size(); i++){
                int tempCity = places.get(i);
                places.set(i, places.get(startPlace));
                places.set(startPlace, tempCity);
                currentDistance = computeDistance(places,valuesMatrix);
                bruteForceSearch(valuesMatrix, places, startPlace+1, currentDistance);
                tempCity = places.get(i);
                places.set(i, places.get(startPlace));
                places.set(startPlace, tempCity);
            }
        }
        else{
            if(shortestDistance > currentDistance){
                shortestDistance = currentDistance;
                shortestPath = new ArrayList<>(places);
            }
            if(longestDistance < currentDistance){
                longestDistance = currentDistance;
            }
        }
    }

    private static int computeDistance(List<Integer> places, int[][] distanceMatrix) {
        int distance = 0;
        for(int i=0; i < places.size()-1; i++){
            distance = distance + distanceMatrix[places.get(i)][places.get(i+1)];
        }
        distance = distance + distanceMatrix[places.get(places.size()-1)][places.get(0)];
        return distance;
    }

    static int distanceBetween(Place place1, Place place2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(place2.getLatitude()-place1.getLatitude());
        double dLng = Math.toRadians(place2.getLongitude()-place1.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(place1.getLatitude())) * Math.cos(Math.toRadians(place2.getLatitude()))
                        * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;
        int meterConversion = 1609;
        return (int)(distance * meterConversion);
    }

    //getters:
    static int[][] getValuesMatrix() {
        return valuesMatrix;
    }

    static int getShortestDistance() {
        return shortestDistance;
    }

    static int getLongestDistance() {
        return longestDistance;
    }

    static List<Place> getOrderedPlaces() {
        return orderedPlaces;
    }
}