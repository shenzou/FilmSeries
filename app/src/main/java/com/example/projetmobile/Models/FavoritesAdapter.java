package com.example.projetmobile.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projetmobile.Interfaces.OnMoviesClickCallback;
import com.example.projetmobile.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MovieViewHolder> {

    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    private List<Genre> allGenres;
    private List<Movie> movies;
    private OnMoviesClickCallback callback;

    public FavoritesAdapter(List<Movie> movies, List<Genre> allGenres, OnMoviesClickCallback callback) {
        this.callback = callback;
        this.movies = movies;
        this.allGenres = allGenres;
    }

    public void addMovies(Movie movieToAppend) {
        movies.add(movieToAppend);
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poster_tile, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        RatingBar rating;
        ImageView poster;
        Movie movie;
        Button favorite_button;
        Context context;
        SharedPreferences sharedPreferences;

        public MovieViewHolder(View itemView) {
            super(itemView);
            rating = itemView.findViewById(R.id.item_movie_rating);
            poster = itemView.findViewById(R.id.item_movie_poster);
            favorite_button = itemView.findViewById(R.id.favorite_button);
            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(movie);
                }
            });
        }

        public void bind(final Movie movie) {
            this.movie = movie;
            rating.setVisibility(View.VISIBLE);
            rating.setRating(movie.getRating()/2);
            final String movieID = Integer.toString(movie.getId());

            sharedPreferences = context.getSharedPreferences("MOVIES", MODE_PRIVATE);
            String stringID = Integer.toString(movie.getId());
            if(sharedPreferences.contains(stringID))
            {
                favorite_button.setTag(R.drawable.ic_fav_true);
                favorite_button.setBackgroundResource(R.drawable.ic_fav_true);
            }
            else{
                favorite_button.setTag(R.drawable.ic_fav_false);
                favorite_button.setBackgroundResource(R.drawable.ic_fav_false);
            }

            favorite_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try{
                        String stringID = movieID;
                        if(sharedPreferences.contains(stringID))
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(stringID);
                            editor.apply();
                            favorite_button.setTag(R.drawable.ic_fav_false);
                            favorite_button.setBackgroundResource(R.drawable.ic_fav_false);

                        }
                        else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(stringID, movie.getId());
                            editor.apply();
                            favorite_button.setTag(R.drawable.ic_fav_true);
                            favorite_button.setBackgroundResource(R.drawable.ic_fav_true);

                        }
                    }
                    catch(Exception e){
                        favorite_button.setTag(R.drawable.ic_fav_true);
                        favorite_button.setBackgroundResource(R.drawable.ic_fav_true);
                    }

                }
            });

            String posterPath = movie.getPosterPath();
            String ImageURI = IMAGE_BASE_URL + posterPath;
            Glide.with(itemView)
                    .load(ImageURI)
                    .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                    .into(poster);
        }

    }
}
