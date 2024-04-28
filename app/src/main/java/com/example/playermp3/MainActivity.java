package com.example.playermp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.playermp3.services.song.Listed;
import com.example.playermp3.ui.SongAdapter;

public class MainActivity extends AppCompatActivity {
    public static Context context;
    public static Button btPlay;
    public static Button btDowndload;
    public static RecyclerView recyclerView;
    public static SongAdapter songAdapter;
    EditText search_input;
    public static TextView nowSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_main);
        addView();
        addEvent();
        addData();
    }
    private void addView() {
        context = this;
        btPlay = findViewById(R.id.btn_play);
        btDowndload = findViewById(R.id.btn_download);
        recyclerView = findViewById(R.id.recycle_view);
        search_input = findViewById(R.id.search_input);
        nowSong = findViewById(R.id.nowSong);
        search_input.post(new Runnable() {
            @Override
            public void run() {
                search_input.setFocusable(true);
                search_input.setFocusableInTouchMode(true);
            }
        });

    }
    private void addEvent() {
        EditText searchInput = findViewById(R.id.search_input);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần ở đây
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();  // Lấy text mới
                if (MainActivity.songAdapter.isReady) {
                    songAdapter.filter(searchText);  // Thực hiện lọc
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }
    private void addData() {
        Listed.getInstance().execute("https://mp3.zing.vn/xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1");
    }

}