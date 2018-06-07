package com.brian.zaplanujwyjazd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class DataParser {

    List<String[]> parseDirections(String jsonData){

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        String[] polylines;
        List<String[]> route = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(jsonData);
            jRoutes = jObject.getJSONArray("routes");

            // Traversing all routes
            for(int i=0; i<jRoutes.length(); i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                // Traversing all legs
                for(int j=0; j<jLegs.length(); j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    polylines = new String[jSteps.length()];

                    // Traversing all steps
                    for(int k=0; k<jSteps.length(); k++){
                        String polyline;
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        polylines[k] = polyline;
                    }
                    route.add(polylines);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return route;
    }

    int[][] parseDistance(String jsonData){
        int[][] distanceMatrix = new int[0][0];
        JSONArray jRows;
        JSONArray jElements;

        try {
            JSONObject jObject = new JSONObject(jsonData);
            jRows = jObject.getJSONArray("rows");
            distanceMatrix = new int[jRows.length()][jRows.length()];

            for (int i = 0; i < jRows.length(); i++) {
                jElements = ((JSONObject)jRows.get(i)).getJSONArray("elements");

                for (int j = 0; j < jElements.length(); j++) {
                    int distance = (int) ((JSONObject)((JSONObject)jElements.get(j))
                            .get("distance")).get("value");
                    distanceMatrix[i][j] = distance;
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return distanceMatrix;
    }

    int[][] parseTime(String jsonData){
        int[][] timeMatrix = new int[0][0];
        JSONArray jRows;
        JSONArray jElements;

        try {
            JSONObject jObject = new JSONObject(jsonData);
            jRows = jObject.getJSONArray("rows");
            timeMatrix = new int[jRows.length()][jRows.length()];

            for (int i = 0; i < jRows.length(); i++) {
                jElements = ((JSONObject)jRows.get(i)).getJSONArray("elements");

                for (int j = 0; j < jElements.length(); j++) {
                    int time = (int) ((JSONObject)((JSONObject)jElements.get(j)).get("duration")).get("value");
                    timeMatrix[i][j] = time;
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return timeMatrix;
    }

    ArrayList<Place> parsePlaces(String jsonData){
        ArrayList<Place> foundPlaces = new ArrayList<>();
        JSONArray placesArray;
        JSONObject placeJObject;
        Place place;

        try {
            JSONObject jObject = new JSONObject(jsonData);
            placesArray = jObject.getJSONArray("results");

            for(int i = 0; i<placesArray.length(); i++){
                place= new Place();
                placeJObject = placesArray.getJSONObject(i);
                place.setName(placeJObject.optString("name"));
                place.setLatitude(placeJObject.optJSONObject("geometry").optJSONObject("location")
                        .optDouble("lat"));
                place.setLongitude(placeJObject.optJSONObject("geometry").optJSONObject("location")
                        .optDouble("lng"));
                place.setDescription(placeJObject.optString("vicinity"));
                place.setRating((float)placeJObject.optDouble("rating"));

                foundPlaces.add(place);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return foundPlaces;
    }

}