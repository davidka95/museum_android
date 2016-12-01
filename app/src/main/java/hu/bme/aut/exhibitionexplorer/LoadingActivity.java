package hu.bme.aut.exhibitionexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import hu.bme.aut.exhibitionexplorer.data.Exhibition;


public class LoadingActivity extends AppCompatActivity {
    private String exhibitionUuID;

    private Exhibition exhibition = null;
    private HashMap<String, Boolean> userStatistics;
    private boolean isUserStatistics = false;
    private boolean isExhibition = false;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null){
            loadLoginActivity();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        exhibitionUuID = sp.getString(Exhibition.KEY_CHOOSED_EXHIBITION, null);
        getExhibition();
        getUserStatistics();
    }

    private void getExhibition() {
        if (exhibitionUuID != null) {

            DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("exhibitions").child(exhibitionUuID);
            dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    exhibition = dataSnapshot.getValue(Exhibition.class);
                    isExhibition = true;
                    startMainActivity();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            isExhibition = true;
            startMainActivity();
        }
    }

    private void getUserStatistics() {
        userStatistics = new HashMap<>();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    userStatistics.put(data.getKey(), data.getValue(Boolean.class));
                }
                isUserStatistics = true;
                startMainActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isUserStatistics = true;
                startMainActivity();
            }
        });
    }

    public void startMainActivity(){
        if (isExhibition && isUserStatistics){
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.putExtra(Exhibition.KEY_EXHIBITION_PARCELABLE, exhibition);
            mainActivity.putExtra(getString(R.string.key_user_statistics), userStatistics);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainActivity);
        }
    }

    private void loadLoginActivity() {
        Intent startLoginActivity = new Intent(this, LoginActivity.class);
        startLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startLoginActivity);
    }
}
