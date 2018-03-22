package com.sourcey.instantmessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sourcey.instantmessage.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.R.attr.data;

public class EditProfile extends AppCompatActivity {

    @Bind(R.id.imageView2) ImageView profilePicBox;
    @Bind(R.id.setImage) Button setImage;
    @Bind(R.id.displayNameTB) EditText displayNameTB;
    @Bind(R.id.submitButt) Button submitButton;
    @Bind(R.id.userBioTb) EditText userBioTb;
    FirebaseDatabase database;
    DatabaseReference mRef;
    private final String TAG = "tag";
    private FirebaseAuth mAuth;
    private Uri profilePic;
    private int PICK_IMAGE_REQUEST =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String displayName = displayNameTB.getText().toString();
                final String userBio = userBioTb.getText().toString();
                FirebaseUser user = mAuth.getCurrentUser();
                final String userID = user.getUid();

                Log.d(TAG,userID);
                mergeToDB(userBio,userID,displayName);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .setPhotoUri(profilePic)
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG,"line 83");
                                    Toast.makeText(getBaseContext(),"profile updated", Toast.LENGTH_LONG).show();
                                    backToProfile();
                                }
                            }
                        });
            }
        });
    }

    public void getImage(){;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            profilePic = data.getData();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePic);
                // Log.d(TAG, String.valueOf(bitmap));
                profilePicBox.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void backToProfile(){
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
    }

    public void mergeToDB(final String bio, final String userID, final String displayName)
    {
        mRef.child("users").child(userID)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> values = new HashMap<>();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    values.put(snapshot.getKey(),snapshot.getValue());
                                }
                                values.put("bio",bio);
                                values.put("displayName",displayName);
                                values.put("ProfilePic",profilePic.toString());
                                Log.d(TAG,userID);
                                mRef.child("users").child(userID).updateChildren(values);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );



    }

}


