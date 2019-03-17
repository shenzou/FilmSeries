package com.example.projetmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.projetmobile.Interfaces.OnGetGenresCallback;
import com.example.projetmobile.Interfaces.OnGetMovieCallback;
import com.example.projetmobile.Interfaces.OnMoviesClickCallback;
import com.example.projetmobile.Models.FavoritesAdapter;
import com.example.projetmobile.Models.Genre;
import com.example.projetmobile.Models.Movie;
import com.example.projetmobile.Models.MoviesRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {

    MoviesRepository moviesRepository;
    private RecyclerView moviesList;
    private FavoritesAdapter adapter;
    private TextView noFavs;

    private List<Genre> movieGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        noFavs = findViewById(R.id.noFavs);
        initFavorites();

    }

    OnMoviesClickCallback callback = new OnMoviesClickCallback() {
        @Override
        public void onClick(Movie movie) {
            Intent intent = new Intent(FavoritesActivity.this, MovieActivity.class);
            intent.putExtra(MovieActivity.MOVIE_ID, movie.getId());
            startActivityForResult(intent, 1);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        initFavorites();
    }

    private void initFavorites()
    {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        moviesList = findViewById(R.id.favorites_list);
        moviesList.setLayoutManager(manager);
        moviesRepository = MoviesRepository.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MOVIES", MODE_PRIVATE);

        Map<String, ?> map = sharedPreferences.getAll();
        getGenres();
        final List<Movie> movies =  new ArrayList<>();


        Iterator<String> itr = map.keySet().iterator();
        noFavs.setVisibility(View.VISIBLE);
        while(itr.hasNext())
        {
            noFavs.setVisibility(View.INVISIBLE);
            String abc = itr.next();
            moviesRepository.getMovie(Integer.parseInt(abc), new OnGetMovieCallback() {
                @Override
                public void onSuccess(Movie movie) {
                    adapter = new FavoritesAdapter(movies, null, callback);
                    adapter.addMovies(movie);
                    moviesList.setAdapter(adapter);
                }

                @Override
                public void onError() {
                    finish();
                }
            });
        }

        //moviesList.setAdapter(adapter);
    }

    private void getGenres() {
        moviesRepository.getGenres(new OnGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {
                movieGenres = genres;
            }

            @Override
            public void onError() {

            }
        });
    }
}
