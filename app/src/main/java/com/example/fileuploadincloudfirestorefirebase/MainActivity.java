package com.example.fileuploadincloudfirestorefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    Button choosefile;
    Button uploadfile;
    TextView description;
    ImageView photo;
    Uri filepath;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choosefile = (Button) findViewById(R.id.choosefile);
        uploadfile = (Button) findViewById(R.id.upload);
        description = (TextView) findViewById(R.id.description);
        photo = (ImageView) findViewById(R.id.imageView);
        description.setVisibility(View.INVISIBLE);
        uploadfile.setVisibility(View.INVISIBLE);
        photo.setVisibility(View.INVISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


    }

    public void choosefile(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
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
                photo.setImageBitmap(bitmap);
                photo.setVisibility(View.VISIBLE);
                uploadfile.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                Toast.makeText(this, "file selected", Toast.LENGTH_SHORT).show();
                Log.i("selected","file");
            } catch (IOException e) {

            }
        }
    }
    public class Fileupload{
        public String description;
        public String url;
        Fileupload(String description, String url){
            this.description = description;
            this.url = url;
        }
    }
    public void uploadfile(View view){

        Date date = new Date();
        String name = Double.toString(Math.random()) +"-"  + date.getDate() +"-"+ date.getHours() +"-" + date.getMinutes()+"-"+ date.getSeconds();

        final StorageReference filepathreference = storageReference.child("fileuploaddemoinfirebasedatabase").child(name);
        UploadTask task = filepathreference.putFile(filepath);

        task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filepathreference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloaduri= task.getResult();
                    Log.i("download uri=", downloaduri.toString());
                    Fileupload fileupload = new Fileupload(description.getText().toString(),downloaduri.toString());
                    databaseReference.child("users").push().setValue(fileupload);

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myposts){
            Intent intent = new Intent(this, Viewmypostsactivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
