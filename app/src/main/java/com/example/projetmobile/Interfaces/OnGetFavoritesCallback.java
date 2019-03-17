package com.example.projetmobile.Interfaces;

import com.example.projetmobile.Models.Movie;

import java.util.List;

public interface OnGetFavoritesCallback {
    void onSuccess(List<Movie> movie);

    void onError();
}
