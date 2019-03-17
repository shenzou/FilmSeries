package com.example.projetmobile.Interfaces;

import com.example.projetmobile.Models.Genre;

import java.util.List;

public interface OnGetGenresCallback {

    void onSuccess(List<Genre> genres);

    void onError();
}
