package com.example.playermp3.services.song;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playermp3.MainActivity;
import com.example.playermp3.R;
import com.example.playermp3.model.Song;
import com.example.playermp3.ui.SongAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Listed extends AsyncTask<String, Void, String> {
    private Handler handler = new Handler(Looper.getMainLooper());
    private static Listed instance;
    public static Listed getInstance() {
        if (instance == null) {
            instance = new Listed();
        }
        return instance;
    }
    public List<Song> songList = new ArrayList<>();
    public Listed() {}
    @Override
    protected String doInBackground(String... strings) {
        String urlString = strings[0];
        String response = "";

        // Lấy api
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
            } else {
                Log.e("MainActivity", "Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tách api ra thành json và đưa vào list đối tượng song
        try {
            JSONObject jsonData = new JSONObject(response); // response là chuỗi JSON của bạn
            JSONArray songArray = jsonData.getJSONObject("data").getJSONArray("song");

            // Lặp qua array "song" (mỗi bài hát)
            for (int i = 0; i < songArray.length(); i++) {
                JSONObject songObject = songArray.getJSONObject(i);

                // Lấy giá trị "code" và "name"
                String code = songObject.getString("code");
                String name = songObject.getString("name");
                String thumbnail = songObject.getString("thumbnail");
                Song song = new Song();
                // Sử dụng các giá trị code và name:
                song.setName(name);
                song.setCode(code);
                song.setThumbnail(thumbnail);
                songList.add(song);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Thêm tiếp thuộc tính bị thiếu của đối tượng song trong list
        for (Song song : songList) {
            addUrlMedia(song, song.getCode());
            // In tên bài hát
            System.out.println("Tên bài hát: " + song.getName());
            System.out.println("Mã bài hát: " + song.getCode());
            System.out.println("Đường dẫn: " + song.getUrl());
            System.out.println("Thumbnail: " + song.getThumbnail());
        }

        return response;
    }
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.context));
                MainActivity.songAdapter = new SongAdapter(songList);
                MainActivity.recyclerView.setAdapter(MainActivity.songAdapter);
            }
        });
    }

    public void addUrlMedia(Song song, String code) {
        String urlString = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + code;
        String response = "";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
            } else {
                Log.e("MainActivity", "Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonData = new JSONObject(response); // response là chuỗi JSON của bạn
            JSONObject dataObject = jsonData.getJSONObject("data");
            JSONObject sourceObject = dataObject.getJSONObject("source");
            String url = sourceObject.getString("128");
            song.setUrl(url);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
