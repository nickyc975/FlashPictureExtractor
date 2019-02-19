package com.nickyc975.fpextrcator;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loadImages();
    }


    private void loadImages() {
        String OUTPUT_DIR = "FlashPictures/.cache/";
        String QQ_FP_DIR_1 = "tencent/MobileQQ/diskcache/";
        String QQ_FP_DIR_2 = "Tencent/MobileQQ/diskcache/";

        File QQFPDir;
        File outputDir = Environment.getExternalStoragePublicDirectory(OUTPUT_DIR);
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
        File[] cacheFiles = QQFPDir.listFiles();
        for (File file : cacheFiles) {
            if (file.canRead() && isImage(file)) {
                target_path = OUTPUT_DIR + file.lastModified() + ".jpg";
                target = Environment.getExternalStoragePublicDirectory(target_path);
                copyFile(file, target);
                if (!target.setLastModified(file.lastModified())) {
                    Log.w("fpextrcator", "Unable to change last modified time of " + target.getPath() + " .");
                }
            }
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
