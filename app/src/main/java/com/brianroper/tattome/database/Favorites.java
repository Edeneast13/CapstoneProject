package com.brianroper.tattome.database;

import android.net.Uri;

/**
 * Created by brianroper on 5/27/16.
 */
public class Favorites {

    private Uri tattooUrl;
    private String title;

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
}
