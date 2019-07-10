package com.example.android.myapplication.utilities;

import com.example.android.myapplication.Model.MovieClass;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JsonUtil {

    // Define strings to use in movie object creation
    private static final String RESULTS = "results";
    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String ID = "id";

    // Define string to use for setting image
    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

    //ArrayList of MovieClass to store movie objects
    public static ArrayList<MovieClass> imageArray = new ArrayList<>();

    // Function taking JSON string, setting movie object and adding object into ArrayList
    // of MovieClass
    public static ArrayList<MovieClass> parseMoviefromJson(String jsonResult){
        try {
            JSONObject responseObj = new JSONObject(jsonResult);
            JSONArray array = responseObj.getJSONArray(RESULTS);
            for (int i = 0; i < array.length(); i++){
                MovieClass Movies = new MovieClass();
                JSONObject imageobject = array.getJSONObject(i);
                Movies.setImage(BASE_URL + imageobject.optString(POSTER_PATH));
                Movies.setTitle(imageobject.optString(TITLE));
                Movies.setOverview(imageobject.optString(OVERVIEW));
                Movies.setRating(imageobject.optString(VOTE_AVERAGE));
                Movies.setReleaseDate(imageobject.optString(RELEASE_DATE));
                Movies.setId(imageobject.optInt(ID));
                imageArray.add(Movies);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return imageArray;
    }
}

