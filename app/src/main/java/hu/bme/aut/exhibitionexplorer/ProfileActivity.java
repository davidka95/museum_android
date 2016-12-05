package hu.bme.aut.exhibitionexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.exhibitionexplorer.data.Artifact;

/**
 * Created by Zay on 2016.12.05..
 */

public class ProfileActivity extends AppCompatActivity {
    public static final String KEY_HISTORY_DELETED = "KEY_HISTORY_DELETED" ;
    Toolbar toolbar;
    private TextView tvFavoriteCount;
    private TextView tvTrivia;
    private TextView tvUserEmail;
    private TextView tvClearQuizAnswers;
    private HashMap<String, Boolean> userStatistics;
    private int answerAll;
    private int answerGood;
    private String Uid;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private boolean quizHistoryDeleted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(KEY_HISTORY_DELETED, quizHistoryDeleted);
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });

        tvFavoriteCount = (TextView) findViewById(R.id.tvFavoriteCount);
        tvTrivia = (TextView) findViewById(R.id.tvTriviaStatus);
        tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
        userStatistics = new HashMap<>();
        Bundle extras = getIntent().getExtras();
        quizHistoryDeleted = false;
        fillTextViews(extras);

        final Context context = this;
        tvClearQuizAnswers = (TextView) findViewById(R.id.tvClear_quiz_answers);
        tvClearQuizAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Quiz history")
                        .setMessage("Are you sure you want to delete your quiz answers?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child(Uid).removeValue();
                                quizHistoryDeleted = true;
                                answerAll = answerGood = 0;
                                tvTrivia.setText(String.format(getResources().getString(R.string.trivia_status_value), answerGood, answerAll));
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_delete)
                        .show();
            }
        });
    }


    private void fillTextViews(Bundle bundle) {
        long count = Artifact.count(Artifact.class);
        Uid = bundle.getString(MainActivity.KEY_USER_ID);
        Log.d("Uid: ", Uid);
        tvFavoriteCount.setText(String.valueOf(count));
        tvUserEmail.setText(bundle.getString(MainActivity.KEY_USER_EMAIL));
        userStatistics.putAll((HashMap<String, Boolean>) bundle.getSerializable(MainActivity.KEY_USER_STATISTICS));
        answerAll = userStatistics.size();

        for (Map.Entry<String, Boolean> entry : userStatistics.entrySet()) {
            if (entry.getValue() == true)
                answerGood++;
        }
        tvTrivia.setText(String.format(getResources().getString(R.string.trivia_status_value), answerGood, answerAll));
    }
}
