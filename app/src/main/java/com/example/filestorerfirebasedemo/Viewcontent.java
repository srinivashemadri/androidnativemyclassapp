package com.example.filestorerfirebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class Viewcontent extends AppCompatActivity {

    String url;
    String filetype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcontent);
        Intent intent = getIntent();
        TextView name = (TextView) findViewById(R.id.name);
        TextView description =(TextView) findViewById(R.id.description);
        TextView type = (TextView) findViewById(R.id.type);
        name.setText(intent.getStringExtra("name"));
        description.setText(intent.getStringExtra("description"));
        type.setText(intent.getStringExtra("type"));
        url = intent.getStringExtra("url");
        Log.i("url in new activity=",url);
        filetype=intent.getStringExtra("type");
        if(filetype.equals("image/jpeg") || filetype.equals("image/png") || filetype.equals("image/jpg"))
        {
            Toast.makeText(this,"Image Loading please wait",Toast.LENGTH_SHORT).show();
            ImageView imageView = (ImageView) findViewById(R.id.imageView3);
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(url).into(imageView);
        }
        else if(filetype.equals("video/mp4")|| filetype.equals("video/3gp"))
        {
            Toast.makeText(this,"Video is Loading please wait",Toast.LENGTH_SHORT).show();
            VideoView videoView =(VideoView) findViewById(R.id.videoView2);
            videoView.setVideoURI(Uri.parse(url));
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.start();
        }
        else
        {
            Button btn = (Button) findViewById(R.id.clickme);
            btn.setVisibility(View.VISIBLE);
            String x="";
            if(filetype.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                x="word";
            else if(filetype.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                x="ppt";
            btn.setText("Click me to download "+ x +" file");
        }
    }
    public void download(View view)
    {
        Toast.makeText(this,"Downloading! Check notification bar",Toast.LENGTH_SHORT).show();
        DownloadManager downloadmanager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        if(filetype.equals("application/pdf"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads", "PDF Document" + ".pdf");
        else if(filetype.equals("WORD Document"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads","Word Document"+".docx");
        else if(filetype.equals("Power Point Presentation Document"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads","Power Point Presentation"+".pptx");
        else if(filetype.equals("image/jpeg"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads", "Image"+".jpeg");
        else if(filetype.equals("image/jpg"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads","Image"+".jpg");
        else if(filetype.equals("image/png"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads","Image"+".png");
        else if(filetype.equals("video/mp4"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads","Video"+".mp4");
        else if(filetype.equals("video/3gp"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads","Video"+".3gp");
        else if(filetype.equals("video/mkv"))
            request.setDestinationInExternalFilesDir(getApplicationContext(), "/downloads","Video"+".mkv");
        downloadmanager.enqueue(request);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.downloadmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.download:
                download(null);
        }
        return super.onOptionsItemSelected(item);
    }
}
