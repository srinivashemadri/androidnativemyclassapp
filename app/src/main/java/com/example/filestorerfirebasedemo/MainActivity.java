package com.example.filestorerfirebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbref;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbref = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }
    public void login(View view) {
        TextView emailtextview = (TextView) findViewById(R.id.email);
        TextView pwdtextview = (TextView) findViewById(R.id.password);
        String email = emailtextview.getText().toString();
        String password = pwdtextview.getText().toString();
        Log.i("email", email);
        Log.i("password", password);
        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), fileupload.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Email/Password is invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else if(email.length()>0 && password.length()==0)
        {
            Toast.makeText(getApplicationContext(),"Password shouldn't be empty",Toast.LENGTH_SHORT).show();
        }
        else if(email.length()==0 && password.length()>0)
            Toast.makeText(getApplicationContext(),"email shouldn't be empty",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"Email and password shouldn't be empty",Toast.LENGTH_SHORT).show();


    }
    public void signup(View view)
    {
        Intent intent = new Intent(getApplicationContext(), signupactivity.class);
        startActivity(intent);
    }
    public void student(View view)
    {
        Intent intent = new Intent(getApplicationContext(),studentactivity.class);
        startActivity(intent);
    }

}

