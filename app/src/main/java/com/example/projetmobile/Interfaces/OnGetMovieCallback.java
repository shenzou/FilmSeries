package com.example.projetmobile.Interfaces;

import com.example.projetmobile.Models.Movie;

public interface OnGetMovieCallback {

    void onSuccess(Movie movie);

    void onError();
}
