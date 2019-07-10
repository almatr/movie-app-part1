package com.example.android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.myapplication.Model.MovieClass;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;


public class MovieDetail extends AppCompatActivity {

    //Declare ImageView to hold image and TextViews to hold texts
    private ImageView movieImage;
    private TextView titleText;
    private TextView rating;
    private TextView releaseDate;
    private MovieClass movieDetail;
    private ExpandableTextView expandtxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        setTitle("");

        movieImage = findViewById(R.id.detail_img);
        titleText = findViewById(R.id.title);
        rating = findViewById(R.id.rating);
        releaseDate = findViewById(R.id.release_date);
        expandtxt = findViewById(R.id.expand_text_view);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Get the object from the intent using the MovieObject key
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("MovieObject")) {
                movieDetail = (MovieClass) getIntent().getParcelableExtra("MovieObject");

                //get Image from movie object and load using picasso
                String imageUrl = movieDetail.getImage();
                Picasso.with(this).load(imageUrl).into(movieImage);

                // get the Textviews populated
                titleText.setText(movieDetail.getTitle());
                rating.setText(movieDetail.getRating());
                releaseDate.setText(movieDetail.getReleaseDate());
                expandtxt.setText(movieDetail.getOverview());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


