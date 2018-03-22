package com.sourcey.instantmessage;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class ViewProfile extends AppCompatActivity
{
    FirebaseAuth mAuth;
    final String TAG = "TAG";
    String userID;
    @Bind(R.id.userNameTB) TextView userNameTB;
    @Bind(R.id.ProfilePicture) ImageView profileImage;
    @Bind(R.id.userBio) TextView userBio;
    @Bind(R.id.emailInfo) TextView emailInfo;
    @Bind(R.id.sendMessage) Button sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        getUserInfo();

        sendMessage.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(ViewProfile.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public void getUserInfo()
    {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();

        String email = intent.getStringExtra("USER_EMAIL");
        mRef.child("users").orderByChild("Email").equalTo(email).addListenerForSingleValueEvent
                (
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        collectInfo((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                }
        );
    }

    public void collectInfo(Map<String, Object> users) {
       Map<String, Object> userInfo = new HashMap<>();
        for (Map.Entry<String, Object> entry : users.entrySet())
        {
            Map singleUser = (Map) entry.getValue();
            userInfo.put("FullName",singleUser.get("FullName").toString());
            userInfo.put("bio",singleUser.get("bio").toString());
            userInfo.put("displayName",singleUser.get("displayName").toString());
            userInfo.put("email",singleUser.get("Email").toString());
            userInfo.put("picture",singleUser.get("ProfilePic").toString());
            userInfo.put("userID",singleUser.get("userID").toString());
            Log.d(TAG, userInfo.get("email").toString());

            userNameTB.setText(userInfo.get("FullName").toString());
            String userUri = userInfo.get("picture").toString();
            Uri userPic = Uri.parse(userUri);
            String bio = userInfo.get("bio").toString();
            userBio.setText(bio);
            String email = userInfo.get("email").toString();
            emailInfo.setText(email);
            userID = userInfo.get("userID").toString();
            Glide
                    .with(getApplicationContext())
                    .load(userPic)
                    .centerCrop()
                    .into(profileImage);

        }
    }
}
