package com.example.android.myapplication;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.android.myapplication.Model.MovieClass;
import com.example.android.myapplication.utilities.JsonUtil;
import com.example.android.myapplication.utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //for saving menu position key
    private static int mMenuPosition = 0;
    private static final String POSITION_KEY = "menuKey";

    private static final String MOVIES_ARRAY = "moviesArray";
    //for saving recyclerView scroll position
    private static final String SCROLLED_POSITION = "scrolled_position";
    private int mScrolledPosition = 0;
   // private Parcelable savedRecyclerLayoutState;

    // Declaring RecyclerView, MovieAdapter and ArrayList of MovieClass objects
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager gridManager;
    private ArrayList<MovieClass> mMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mRecyclerView.setHasFixedSize(false);

        final int columnsNum = getResources().getInteger(R.integer.gallery_columns);

        gridManager = new GridLayoutManager(
                MainActivity.this,columnsNum);
        mRecyclerView.setLayoutManager(gridManager);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            mMenuPosition = savedInstanceState.getInt(POSITION_KEY);
            mScrolledPosition = savedInstanceState.getInt(SCROLLED_POSITION);
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_ARRAY);
            mMovieAdapter.setMovieData(mMovies);
        }else{
            loadMovieData(mMenuPosition);
        }
    }

    private void loadMovieData(int movieItemKey){
        String movieUrlStr = getResources().getString(R.string.popular_url);
        mMenuPosition = movieItemKey;
        switch(movieItemKey) {
            case 0:
                movieUrlStr = getResources().getString(R.string.popular_url);
                break;
            case 1:
                movieUrlStr = getResources().getString(R.string.topRated_url);
                break;
            default:
                break;
        }
        if (isNetworkAvailable() && isOnline()){
            URL movieUrl = NetworkUtils.buildUrl(movieUrlStr);
            new movieQueryTask().execute(movieUrl);
        }else{
            showErrorMessage();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadMovieData(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.spinner, menu);
        MenuItem item = menu.findItem(R.id.spinner_movies);
        Spinner spinner = (Spinner) item.getActionView();
        //Creating an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_array, android.R.layout.simple_spinner_dropdown_item);
        //Setting adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(mMenuPosition, false);
        spinner.setOnItemSelectedListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_KEY, mMenuPosition);
        outState.putInt(SCROLLED_POSITION, ((GridLayoutManager)
                mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        outState.putParcelableArrayList(MOVIES_ARRAY, mMovies);
    }

    //verify network availability
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    //Check if online
    public Boolean isOnline() {
        try {
            Process processing =
                    java.lang.Runtime.getRuntime().exec("ping -c 1 www.themoviedb.org");
            int returnVal = processing.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //simple Snackbar to inform the user of the network connectivity
    public void showErrorMessage() {
        int snackBarDuration = 4000;
        Snackbar snackbar = Snackbar
                .make(mRecyclerView, "You are not connected," +
                        " please check your connection", snackBarDuration);
        snackbar.show();
    }

    //perform background operations
    public class movieQueryTask extends AsyncTask<URL, Void, String>{
        @Override
        protected String doInBackground(URL... urls) {
            if(urls == null){
                return null;
            }
            URL searchUrl = urls[0];
            String result = null;
            try {
                result = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            }catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null && !result.equals("")){
                if(mMovies != null){
                    mMovies.clear();
                }
                //JsonUtil returns JSON result which is parsed into ArrayList of MovieClass objects
                mMovies = JsonUtil.parseMoviefromJson(result);
                mMovieAdapter.setMovieData(mMovies); //setting movies in mMovieAdapter;
                if (mScrolledPosition != 0) {
                    mRecyclerView.scrollToPosition(mScrolledPosition);
                }
            }
        }
    }
}
