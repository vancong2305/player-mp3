package com.example.playermp3.services.song;

import static com.example.playermp3.MainActivity.context;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.playermp3.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Download extends AsyncTask<String, Void, Void> {
    public Download(){}

    @Override
    protected Void doInBackground(String... strings) {
        try {
            // Tải file mp3
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // Lưu file mp3
                ResponseBody body = response.body();
                InputStream inputStream = body.byteStream();
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), strings[1]);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] data = new byte[1024];
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                Handler handler = new Handler(Looper.getMainLooper()); // Lấy Handler của luồng chính
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.context, "Tải file " + strings[1] +" thành công!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Handler handler = new Handler(Looper.getMainLooper()); // Lấy Handler của luồng chính
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Thông báo lỗi tải file
                        Toast.makeText(MainActivity.context, "Tải file " + strings[1] +" thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
