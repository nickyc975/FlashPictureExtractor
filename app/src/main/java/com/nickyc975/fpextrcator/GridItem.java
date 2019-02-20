package com.nickyc975.fpextrcator;

import android.net.Uri;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class GridItem {
    private Uri image;
    private String timestamp;

    GridItem(File image, long timestamp) {
        this.image = Uri.fromFile(image);
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date(timestamp));
    }

    Uri getImageUri() {
        return image;
    }

    String getTimestamp() {
        return timestamp;
    }
}
