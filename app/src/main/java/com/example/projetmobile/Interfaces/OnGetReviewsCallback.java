package com.example.projetmobile.Interfaces;

import com.example.projetmobile.Models.Review;

import java.util.List;

public interface OnGetReviewsCallback {
    void onSuccess(List<Review> reviews);

    void onError();
}
