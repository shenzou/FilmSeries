package com.example.projetmobile.Interfaces;

import com.example.projetmobile.Models.Movie;

import java.util.List;

public interface OnGetMoviesCallback {

    void onSuccess(int page, List<Movie> movies);

    void onError();
}