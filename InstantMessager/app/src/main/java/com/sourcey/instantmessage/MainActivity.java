package com.sourcey.instantmessage;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static final String TAG = "xd";
    @Bind(R.id.logoutButton) Button logoutButton;
    @Bind(R.id.viewProfile) Button viewProfile;
    @Bind(R.id.friendList) Button friendList;
    @Bind(R.id.convButton) Button conversationButton;
    String fullName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG,"33");
        FirebaseUser currUser = mAuth.getCurrentUser();
        if(currUser != null){
            welcomeUser();
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        checkUser(currUser);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfileStub();
            }
        });
        friendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserList.class);
                startActivity(intent);
            }
        });
        conversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Conversations.class);
                startActivity(intent);
            }
        });
    }

    public void checkUser(FirebaseUser user){
        Log.d(TAG,"42");
        if(user != null){
            Log.d(TAG,"45");
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void welcomeUser(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference nameRef;
        String userId = currentUser.getUid();
        if(userId != null)
        {
            nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("FullName");
            Log.d(TAG,userId+"86");

            nameRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if(dataSnapshot.getValue() != null){
                                fullName = dataSnapshot.getValue().toString();

                                Log.d(TAG,fullName);
                                TextView welcomeMessage = (TextView)findViewById(R.id.welcomeMessage);

                                welcomeMessage.setText("Welcome "+fullName+"!");
                            }else{
                                Log.d(TAG,"something is wrong line 102");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }




    }

    public void signout(){
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        Toast.makeText(getBaseContext(),"Successfully signed out", Toast.LENGTH_LONG);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewProfileStub(){
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
    }
}
