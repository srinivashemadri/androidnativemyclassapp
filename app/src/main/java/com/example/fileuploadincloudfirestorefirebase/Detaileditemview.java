package com.example.fileuploadincloudfirestorefirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Detaileditemview extends AppCompatActivity {

    String url;
    String description;
    ImageView contentimage;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaileditemview);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        description = intent.getStringExtra("description");
        Log.i("description", description);
        Log.i("url", url);
        contentimage = (ImageView) findViewById(R.id.contentimage);
        textView = (TextView) findViewById(R.id.contentdescription);
        textView.setText(description);
        Picasso.get().load(url).into(contentimage);
    }
}
