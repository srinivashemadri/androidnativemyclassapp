package com.example.filestorerfirebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class studentactivity extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference dbref;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentactivity);
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();
        listView = (ListView) findViewById(R.id.listview);
        final List<HashMap<String,String>> listitems = new ArrayList<>();

        final SimpleAdapter adapter = new SimpleAdapter(this,listitems,R.layout.listitem,
                new String[]{"name","description","type","url"}, new int[]{R.id.name,R.id.description,R.id.type,R.id.url});
        listView.setAdapter(adapter);
        //childevent listner starts
        dbref.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot abc: dataSnapshot.child("files").getChildren())
                {
                    Log.i("Name=",abc.child("Uploadername").getValue().toString());
                    Log.i("Description=",abc.child("description").getValue().toString());
                    Log.i("metadata=",abc.child("metadata").getValue().toString());
                    Log.i("url=",abc.child("url").getValue().toString());
                    HashMap<String,String> result = new HashMap<>();
                    result.put("name",abc.child("Uploadername").getValue().toString());
                    result.put("description",abc.child("description").getValue().toString());
                    result.put("type",abc.child("metadata").getValue().toString());
                    result.put("url",abc.child("url").getValue().toString());
                    listitems.add(result);
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("name=", listitems.get(position).get("name"));
                Log.i("description=", listitems.get(position).get("description"));
                Log.i("type=", listitems.get(position).get("type"));
                Intent intent = new Intent(getApplicationContext(),Viewcontent.class);
                intent.putExtra("name",listitems.get(position).get("name"));
                intent.putExtra("description", listitems.get(position).get("description"));
                intent.putExtra("type", listitems.get(position).get("type"));
                intent.putExtra("url", listitems.get(position).get("url"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refreshmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.refresh:
                Intent i = new Intent(this, studentactivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }
}
