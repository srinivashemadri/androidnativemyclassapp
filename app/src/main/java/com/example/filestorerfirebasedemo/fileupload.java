package com.example.filestorerfirebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class fileupload extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST =234 ;
    Uri filepath;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbref;
    FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileupload);
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbref = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(this,mAuth.getUid(),Toast.LENGTH_LONG).show();
        uid = mAuth.getUid();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.facultymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.myposts:
                Log.i("Menu item selected=","myposts");
                 intent = new Intent(getApplicationContext(),myposts.class);
                startActivity(intent);
                break;
            case R.id.logout:
                Log.i("Menu item selected","logout");
                mAuth.signOut();
                intent= new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class content{
        public String url;
        public String metadata;
        public String description;
        public String Uploadername;
        public content(String ur,String met,String description,String name)
        {
            this.url=ur;
            this.metadata= met;
            this.description = description;
            this.Uploadername = name;
        }
    }
    public void choose(View view)
    {
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVisibility(View.INVISIBLE);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);
        ProgressBar progressBar =(ProgressBar) findViewById(R.id.progressBar);
        imageView.setVisibility(View.INVISIBLE);
        TextView textView = (TextView) findViewById(R.id.uploadingpercentage);
        textView.setVisibility(View.INVISIBLE);
        Log.i("Button","Choose");
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select an image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData()!=null)
        {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                //ImageView imageView = (ImageView) findViewById(R.id.imageView);
                //imageView.setImageBitmap(bitmap);
                TextView textView = findViewById(R.id.uploadingpercentage);
                textView.setVisibility(View.VISIBLE);
                textView.setText("File has been selected!, Please click upload to share!!");

                Log.i("selected","file");
            } catch (IOException e) {

            }
        }
    }

    public void upload(View view)
    {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        TextView desciption = findViewById(R.id.description);
        final String description = desciption.getText().toString();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        Date date = new Date();
        String name = Double.toString(Math.random()) +"-"  + date.getDate() +"-"+ date.getHours() +"-" + date.getMinutes()+"-"+ date.getSeconds();
        final StorageReference ref = mStorageRef.child("files").child(name);
        UploadTask uploadTask = ref.putFile(filepath);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    final Uri downloadUri = task.getResult();
                    Log.i("download uri=", String.valueOf(downloadUri));
                    final String url = String.valueOf(downloadUri);


                    ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            String filetype = storageMetadata.getContentType();
                            String metadatacontentype= filetype;
                            if(filetype.equals("application/pdf"))
                                metadatacontentype="PDF Document";
                            else if(filetype.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                                metadatacontentype="WORD Document";
                            else if(filetype.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                                metadatacontentype="Power Point Presentation Document";

                            content cnt = new content(url,metadatacontentype,description, mAuth.getCurrentUser().getDisplayName());
                            dbref.child("users").child(uid).child("files").push().setValue(cnt);
                            if(metadatacontentype.equals("video/mp4") || metadatacontentype.equals("video/3gp"))
                            {
                                Toast.makeText(getApplicationContext(),"Video has been successfully shared",Toast.LENGTH_LONG).show();
                                VideoView videoView = (VideoView) findViewById(R.id.videoView);
                                videoView.setVisibility(View.VISIBLE);
                                videoView.setVideoURI(downloadUri);
                                videoView.start();
                            }
                            else if(metadatacontentype.equals("image/jpeg") ||metadatacontentype.equals("image/jpg") || metadatacontentype.equals("image/png"))
                            {
                                Toast.makeText(getApplicationContext(),"Image has been successfully shared",Toast.LENGTH_LONG).show();
                                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                imageView.setVisibility(View.VISIBLE);
                                Picasso.get().load(downloadUri).into(imageView);
                            }
                            else if(metadatacontentype.equals("application/pdf"))
                            {
                                Toast.makeText(getApplicationContext(),"PDF has been successfully shared",Toast.LENGTH_LONG).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Exception=", String.valueOf(e));
                        }
                    });

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                //Toast.makeText(getApplicationContext(),Long.toString(,Toast.LENGTH_LONG).show();

                progressBar.setProgress((int) (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                TextView textView = (TextView) findViewById(R.id.uploadingpercentage);
                textView.setVisibility(View.VISIBLE);
                textView.setText("Uploaded:"+ 100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount() + "%");

                //Long percentage = progress*100;
                //Log.i("progress", Long.toString(percentage));
            }
        });

    }


}
