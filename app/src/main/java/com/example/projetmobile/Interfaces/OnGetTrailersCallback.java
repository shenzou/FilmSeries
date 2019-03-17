package com.example.projetmobile.Interfaces;

import com.example.projetmobile.Models.Trailer;

import java.util.List;

public interface OnGetTrailersCallback {
    void onSuccess(List<Trailer> trailers);

    void onError();
}
