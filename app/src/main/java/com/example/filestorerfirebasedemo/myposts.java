package com.example.filestorerfirebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class myposts extends AppCompatActivity {
    FirebaseUser user ;
    String uid;
    FirebaseDatabase db;
    DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myposts);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        db= FirebaseDatabase.getInstance();
        dbref = db.getReference();
        ListView listView = (ListView) findViewById(R.id.listview);
        final List<HashMap<String,String>> listitems = new ArrayList<>();

        final SimpleAdapter adapter = new SimpleAdapter(this,listitems,R.layout.listitem,
                new String[]{"name","description","type","url"}, new int[]{R.id.name,R.id.description,R.id.type,R.id.url});
        listView.setAdapter(adapter);
        //childevent listner starts
        dbref.child("users").child(uid).child("files").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Log.i("metadata=",dataSnapshot.child("metadata").getValue().toString());
                    Log.i("description=",dataSnapshot.child("description").getValue().toString());

                    HashMap<String,String> result = new HashMap<>();
                    result.put("name",user.getDisplayName());
                    result.put("description",dataSnapshot.child("description").getValue().toString());
                    result.put("type",dataSnapshot.child("metadata").getValue().toString());
                    result.put("url",dataSnapshot.child("url").getValue().toString());
                    listitems.add(result);
                   adapter.notifyDataSetChanged();


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
}
