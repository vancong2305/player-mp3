package com.example.playermp3.ui;

import static com.example.playermp3.MainActivity.recyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playermp3.MainActivity;
import com.example.playermp3.R;
import com.example.playermp3.model.Song;
import com.example.playermp3.services.song.Download;
import com.example.playermp3.services.song.Play;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> allSongs = null; // Danh sách tất cả bài hát (dữ liệu gốc)
    private List<Song> songList = null; // Danh sách các bài hát được hiển thị
    public static boolean isReady = false;
    public void updateAllPlayButtons() {
        System.out.println(getItemCount() + " Item count là");
        for (int i = 0; i < getItemCount(); i++) {
            SongViewHolder holder = (SongViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.btnPlay.setText("Phát");
            }
        }
    }
    public SongAdapter(List<Song> allSongs) {
        this.allSongs = allSongs;
        this.songList = new ArrayList<>(allSongs); // Khởi tạo danh sách hiển thị
        if (allSongs != null) { // Kiểm tra nếu allSongs đã có dữ liệu
            checkData();
        }
        isReady = true;
    }

    private void checkData() {
        // Lấy thư mục âm nhạc đã tải xuống
        File musicDir = MainActivity.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (musicDir != null) {
            File[] files = musicDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".mp3");
                }
            });

            // Duyệt danh sách và xử lý các tệp mp3
            if (files != null) {
                for (File file : files) {
                    System.out.println("Đường dẫn file " + file.getName().toString());
                    // Truy cập file ở đây: file.getPath(), file.getName(), v.v.
                    System.out.println("Đường dẫn file: " + file.getName().toString());
                }
            } else {
                // Thông báo lỗi không tìm thấy file
            }
        } else {
            // Thông báo lỗi không tìm thấy thư mục
        }
    }


    public void filter(String searchText) {
        searchText = searchText.toLowerCase();
        songList.clear();

        if (searchText.isEmpty()) {
            songList.addAll(allSongs); // Nếu không có text search, hiển thị lại tất cả
        } else {
            for (Song song : allSongs) {
                if (song.getName().toLowerCase().contains(searchText)) {
                    songList.add(song);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = songList.get(position);
        holder.tvName.setText(song.getName());
        holder.tvUrl.setText(song.getCode());
        Picasso.get()
                .load(song.getThumbnail())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgThumbnail);

        holder.btnPlay.setOnClickListener(view -> {
            if(Play.isPlay && position == Play.position) {
                Play.stop(); // Pause
                holder.btnPlay.setText("Phát");
                updateAllPlayButtons();
            } else {
                Play.stop(); // Dừng nhạc cũ trước khi phát
                Play.play(songList.get(position).getUrl(), songList.get(position).getName());  // Phát bài hát mới
                holder.btnPlay.setText("Dừng");
            }
            Play.position = position;
        });

        holder.btnDownload.setOnClickListener(view -> {
            int songIndex = holder.getAdapterPosition();
            String url = songList.get(songIndex).getUrl();
            String fileName = songList.get(songIndex).getCode() + ".mp3";
            Download download = new Download();
            download.execute(url, fileName);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvUrl;
        ImageView imgThumbnail; // Thêm nếu Song class có trường ảnh
        Button btnPlay;
        Button btnDownload;

        public SongViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvUrl = view.findViewById(R.id.tv_url);
            imgThumbnail = view.findViewById(R.id.img_thumbnail);
            btnPlay = view.findViewById(R.id.btn_play);
            btnDownload = view.findViewById(R.id.btn_download);
        }
    }
}
