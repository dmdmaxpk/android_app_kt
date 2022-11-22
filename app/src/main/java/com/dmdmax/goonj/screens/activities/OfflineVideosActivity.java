package com.dmdmax.goonj.screens.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dmdmax.goonj.R;
import com.dmdmax.goonj.models.OfflineVideos;
import com.dmdmax.goonj.storage.DBHelper;

import java.io.File;
import java.util.List;

public class OfflineVideosActivity extends AppCompatActivity {

    private ListView lv;
    private DBHelper mDBHelper;
    private List<OfflineVideos> offlineVideosList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_videos);
        lv = findViewById(R.id.listView);

        mDBHelper = new DBHelper(this);
        offlineVideosList = mDBHelper.getOfflineVideos();
        lv.setAdapter(new Adapter(offlineVideosList, this));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri intentUri = Uri.fromFile(new File(offlineVideosList.get(position).getLocalPath()));

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(intentUri, "video/mp4");
                startActivity(intent);

            }
        });
    }
}

class Adapter extends BaseAdapter {

    private List<OfflineVideos> offlineVideosList;
    private Context context;

    public Adapter(List<OfflineVideos> offlineVideosList, Context context) {
        this.offlineVideosList = offlineVideosList;
        this.context = context;
    }


    @Override
    public int getCount() {
        return this.offlineVideosList.size();
    }

    @Override
    public Object getItem(int position) {
        return offlineVideosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.offline_video_single_item, null, false);
        ((TextView)view.findViewById(R.id.title)).setText(offlineVideosList.get(position).getTitle());
        return view;
    }
}