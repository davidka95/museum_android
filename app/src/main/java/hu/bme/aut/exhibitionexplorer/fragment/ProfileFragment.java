package hu.bme.aut.exhibitionexplorer.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.exhibitionexplorer.LoadingActivity;
import hu.bme.aut.exhibitionexplorer.MainActivity;
import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.interfaces.QuizHistoryDeletedListener;

/**
 * Created by Zay on 2016.12.03..
 */

public class ProfileFragment extends Fragment {

    public static String TAG = "ProfileFragment";
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
    private QuizHistoryDeletedListener onQuizHistoryDeleted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof QuizHistoryDeletedListener) {
            onQuizHistoryDeleted = (QuizHistoryDeletedListener) activity;
        } else {
            throw new RuntimeException("Activity(" +activity.toString() + ") must implement QuizHistoryDeletedListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        tvFavoriteCount = (TextView) rootView.findViewById(R.id.tvFavoriteCount);
        tvTrivia = (TextView) rootView.findViewById(R.id.tvTriviaStatus);
        tvUserEmail = (TextView) rootView.findViewById(R.id.tvUserEmail);
        userStatistics = new HashMap<>();
        fillTextViews();

        tvClearQuizAnswers = (TextView) rootView.findViewById(R.id.tvClear_quiz_answers);
        tvClearQuizAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Quiz history")
                        .setMessage("Are you sure you want to delete your quiz answers?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child(Uid).removeValue();
                                onQuizHistoryDeleted.updateUserStatistics();
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
        return rootView;
    }

    private void fillTextViews() {
        long count = Artifact.count(Artifact.class);
        Uid = getArguments().getString(MainActivity.KEY_USER_ID);
        Log.d("Uid: ", Uid);
        tvFavoriteCount.setText(String.valueOf(count));
        tvUserEmail.setText(getArguments().getString(MainActivity.KEY_USER_EMAIL));
        userStatistics.putAll((HashMap<String, Boolean>) getArguments().getSerializable(MainActivity.KEY_USER_STATISTICS));
        answerAll = userStatistics.size();

        for (Map.Entry<String, Boolean> entry : userStatistics.entrySet()) {
            if (entry.getValue() == true)
                answerGood++;
        }
        tvTrivia.setText(String.format(getResources().getString(R.string.trivia_status_value), answerGood, answerAll));
    }
}
