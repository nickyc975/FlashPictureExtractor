package com.nickyc975.fpextrcator;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private String OUTPUT_DIR = "FlashPictures/";
    private String CACHE_DIR = OUTPUT_DIR + ".cache/";

    private GridView gallery = null;
    private GridViewAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gallery = findViewById(R.id.gallery);

        findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportSelectedImages();
            }
        });

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImages();
            }
        });
    }

    private void exportSelectedImages() {

    }


    private void loadImages() {
        String QQ_FP_DIR_1 = "tencent/MobileQQ/diskcache/";
        String QQ_FP_DIR_2 = "Tencent/MobileQQ/diskcache/";

        File QQFPDir;
        File outputDir = Environment.getExternalStoragePublicDirectory(CACHE_DIR);
        File QQFPDir_1 = Environment.getExternalStoragePublicDirectory(QQ_FP_DIR_1);
        File QQFPDir_2 = Environment.getExternalStoragePublicDirectory(QQ_FP_DIR_2);

        if (QQFPDir_1.exists()) {
            QQFPDir = QQFPDir_1;
        } else if (QQFPDir_2.exists()) {
            QQFPDir = QQFPDir_2;
        } else {
            Log.e("fpextrcator", "QQ cache dir not found!");
            return;
        }

        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                Log.e("fpextrcator", "Unable to create dir " + outputDir.getPath() + " !");
                return;
            }
        }

        File target;
        String target_path;
        ArrayList<GridItem> gridItems = new ArrayList<>();
        for (File file : QQFPDir.listFiles()) {
            if (file.canRead() && isImage(file)) {
                target_path = CACHE_DIR + file.lastModified() + ".jpg";
                target = Environment.getExternalStoragePublicDirectory(target_path);
                copyFile(file, target);
                if (!target.setLastModified(file.lastModified())) {
                    Log.w("fpextrcator", "Unable to change last modified time of " + target.getPath() + " .");
                }
                gridItems.add(new GridItem(file, file.lastModified()));
            }
        }

        if (adapter == null) {
            adapter = new GridViewAdapter(this, gridItems);
            gallery.setAdapter(adapter);
        } else {
            adapter.setData(gridItems);
        }
    }


    private boolean isImage(File file) {
        try {
            byte[] buffer = new byte[2];
            FileInputStream reader = new FileInputStream(file);
            if (reader.read(buffer) == 2) {
                return buffer[0] == (byte)0xFF && buffer[1] == (byte) 0xD8;
            }
            reader.close();
        } catch (IOException ignored) {

        }
        return false;
    }


    private void copyFile(File source, File target) {
        try {
            byte[] buffer = new byte[1024];
            FileInputStream reader = new FileInputStream(source);
            FileOutputStream writer = new FileOutputStream(target);

            int length;
            while ((length = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, length);
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            Log.e("fpextrcator", e.getMessage());
        }
    }
}
