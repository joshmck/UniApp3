package com.sourcey.instantmessage;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sourcey.instantmessage.R;

import static java.security.AccessController.getContext;

public class UserProfile extends AppCompatActivity {
    @Bind(R.id.editProfile) Button editProfileButt;
    @Bind(R.id.ProfilePicture) ImageView profilePictureBox;
    @Bind(R.id.UserName) TextView userNameBox;
    @Bind(R.id.userBio) TextView userBioBox;
    @Bind(R.id.bioTextView) TextView bioTextView;
    private String TAG = "tag";
    FirebaseDatabase dataBase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        dataBase = FirebaseDatabase.getInstance();
        ButterKnife.bind(this);
        getDisplayName();
        profilePicture();
        getUserEmail();
        getUserBio();
        editProfileButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
    }

    public void getDisplayName(){
        FirebaseUser user = mAuth.getCurrentUser();
        String userName = user.getDisplayName();
        if(userName != null){
            userNameBox.setText(userName);
        }else{
            userNameBox.setText("This user has not set a display name yet, they should really edit their profile!");
        }
    }

    public void getUserBio()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        DatabaseReference myRef = dataBase.getReference();
        myRef = dataBase.getReference().child("users").child(userID).child("bio");

        myRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                       if(dataSnapshot.getValue() != null){
                           String bio = dataSnapshot.getValue().toString();
                           bioTextView.setText(bio);
                       }else{
                           String bio = "This user has not yet set their bio, they can do so by editing their profile.";
                           bioTextView.setText(bio);

                       }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    public void profilePicture(){
        FirebaseUser user = mAuth.getCurrentUser();
        Uri picture = user.getPhotoUrl();
        if(picture != null)
        {
            Log.d(TAG,"line 75");
            Glide
                    .with(getApplicationContext())
                    .load(picture)
                    .centerCrop()
                    .into(profilePictureBox);
        }
        else
        {
            Log.d(TAG,"no pic");
        }

    }

    public void getUserEmail(){
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        Log.d(TAG,email);
        userBioBox.setText(email);
    }

    public void editProfile(){
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }
}
