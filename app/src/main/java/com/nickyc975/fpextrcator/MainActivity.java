package com.nickyc975.fpextrcator;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {
    private int EXPORT_NUM = 10;
    private String OUTPUT_DIR = "FlashPictures/";
    private String CACHE_DIR = OUTPUT_DIR + ".cache/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "正在导出...", Toast.LENGTH_SHORT).show();
                exportImages();
                Toast.makeText(v.getContext(), "导出完成！", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "正在刷新...", Toast.LENGTH_SHORT).show();
                loadImages();
                Toast.makeText(v.getContext(), "刷新完成！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportImages() {
        int exportNum;
        EditText item = findViewById(R.id.export_num);

        try {
            exportNum = Integer.parseInt(item.getText().toString());
        } catch (Exception e) {
            exportNum = EXPORT_NUM;
        }

        File cacheDir = Environment.getExternalStoragePublicDirectory(CACHE_DIR);
        if (!cacheDir.exists()) {
            Toast.makeText(this, "请先点击右上角的刷新按钮！", Toast.LENGTH_SHORT).show();
            return;
        }

        LinkedList<File> images = new LinkedList<>(Arrays.asList(cacheDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        })));
        if (images.size() <= 0) {
            Toast.makeText(this, "请先点击右上角的刷新按钮！", Toast.LENGTH_SHORT).show();
            return;
        }

        Collections.sort(images, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (int) ((o2.lastModified() - o1.lastModified()) / 1000);
            }
        });

        File target;
        String target_path;
        for (int i = 0; i < Math.min(exportNum, images.size()); i++) {
            target_path = OUTPUT_DIR + images.get(i).getName();
            target = Environment.getExternalStoragePublicDirectory(target_path);
            copyFile(images.get(i), target);
        }
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
        for (File file : QQFPDir.listFiles()) {
            if (file.canRead() && isImage(file)) {
                target_path = CACHE_DIR + file.lastModified() + ".jpg";
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
