package com.nickyc975.fpextrcator;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String OUTPUT_DIR = "FlashPictures/";
        setContentView(R.layout.activity_main);

        copyFiles(loadImages(), OUTPUT_DIR);
    }


    private List<File> loadImages() {
        File QQFPDir;
        List<File> FlashPictures = new ArrayList<>();
        File QQFPDir_1 = Environment.getExternalStoragePublicDirectory("tencent/MobileQQ/diskcache/");
        File QQFPDir_2 = Environment.getExternalStoragePublicDirectory("Tencent/MobileQQ/diskcache/");

        if (QQFPDir_1.exists()) {
            QQFPDir = QQFPDir_1;
        } else if (QQFPDir_2.exists()) {
            QQFPDir = QQFPDir_2;
        } else {
            Toast.makeText(this, "文件夹不存在！", Toast.LENGTH_LONG).show();
            return FlashPictures;
        }

        File[] cacheFiles = QQFPDir.listFiles();
        for (File file : cacheFiles) {
            if (file.canRead() && isImage(file)) {
                FlashPictures.add(file);
            }
        }

        return FlashPictures;
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


    private void copyFiles(List<File> files, String dir) {
        File target;
        String target_path;
        File outputDir = Environment.getExternalStoragePublicDirectory(dir);
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                Toast.makeText(this, "创建文件夹失败！", Toast.LENGTH_LONG).show();
                return;
            }
        }

        for (File source : files) {
            target_path = dir + source.lastModified() + ".jpg";
            target = Environment.getExternalStoragePublicDirectory(target_path);
            copyFile(source, target);
            target.setLastModified(source.lastModified());
        }
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
        } catch (IOException ignored) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
