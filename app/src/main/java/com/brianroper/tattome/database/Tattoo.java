package com.brianroper.tattome.database;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by brianroper on 5/27/16.
 */
public class Tattoo {

    private Uri tattooUrl;
    private String title;
    private Bitmap image;

    public Uri getTattooUrl() {
        return tattooUrl;
    }

    public void setTattooUrl(Uri tattooUrl) {
        this.tattooUrl = tattooUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
