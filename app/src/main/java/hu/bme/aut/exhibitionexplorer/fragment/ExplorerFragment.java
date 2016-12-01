package hu.bme.aut.exhibitionexplorer.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;

import hu.bme.aut.exhibitionexplorer.ArtifactActivity;
import hu.bme.aut.exhibitionexplorer.PicassoCache;
import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.adapter.CatalogAdapter;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;
import hu.bme.aut.exhibitionexplorer.interfaces.OnFavoriteListener;
import hu.bme.aut.exhibitionexplorer.quiz.QuizHelper;

/**
 * Created by Adam on 2016. 10. 28..
 */

public class ExplorerFragment extends Fragment implements View.OnClickListener {

    public static String TAG = "ExplorerFragment";

    private DatabaseReference mArtifactsReference;

    private KenBurnsView ivArtifactImage;
    private TextView tvArtifactName;
    private TextView tvArtifactDescription;
    private FloatingActionButton fabFavorite;
    private TextView tvQuizQuestion;
    private Button btnAnswerA;
    private Button btnAnswerB;
    private Button btnAnswerC;
    private Button btnAnswerD;

    private QuizHelper quizHelper;
    private Artifact artifact;
    private Boolean userStatistics;
    private Boolean haveUserStatistics = false;

    private FirebaseUser firebaseUser;

    private OnFavoriteListener onFavoriteAddedListener;
    private OnAnswerClickListener onAnswerClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnFavoriteListener) {
            onFavoriteAddedListener = (OnFavoriteListener) activity;
        } else {
            throw new RuntimeException("Activity(" + activity.toString() + ") must implement OnFavoriteListener interface");
        }

        if (activity instanceof OnAnswerClickListener) {
            onAnswerClickListener = (OnAnswerClickListener) activity;
        } else {
            throw new RuntimeException("Activity(" + activity.toString() + ") must implement OnFavoriteListener interface");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explorer, container, false);

        artifact = getArguments().getParcelable(Artifact.KEY_ARTIFACT_PARCELABLE);
        final String KEY_USER_STATISTICS = getString(R.string.key_user_statistics);
        if (getArguments().containsKey(KEY_USER_STATISTICS)){
            haveUserStatistics = true;
            userStatistics = getArguments().getBoolean(KEY_USER_STATISTICS);
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setHasOptionsMenu(true);

        initView(rootView);

        return rootView;
    }

    private void loadDataToViews() {
        Log.d("a kiirando: ", artifact.getImageURL());

        PicassoCache.makeImageRequest(getContext(), ivArtifactImage, artifact.getImageURL());
        tvArtifactName.setText(artifact.getName());
        tvArtifactDescription.setText(artifact.getDescription());
    }

    private void initView(final View rootView) {
        fabFavorite = (FloatingActionButton) rootView.findViewById(R.id.fabFavorite);
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteAddedListener.ArtifactToFavorite(artifact);
                disableFabButton();

                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
                Snackbar addedSnackbar = Snackbar.make(coordinatorLayout,
                        "Artifact added to favorites", Snackbar.LENGTH_LONG);
                addedSnackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        switch (event) {
                            case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                Snackbar undoSnackbar = Snackbar.make(coordinatorLayout, "Removed from favorites", Snackbar.LENGTH_SHORT);
                                undoSnackbar.show();
                                onFavoriteAddedListener.ArtifactRemovedFromFavorite(artifact);
                                enableFabButton();
                                break;
                            default:
                                break;
                        }
                    }
                });
                addedSnackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                        .setActionTextColor(Color.YELLOW);
                addedSnackbar.show();
            }
        });

        setFabButton();

        ivArtifactImage = (KenBurnsView) rootView.findViewById(R.id.kvArtifactImage);
        tvArtifactName = (TextView) rootView.findViewById(R.id.tvArtifactName);
        tvArtifactDescription = (TextView) rootView.findViewById(R.id.tvArtifactDescription);

        initQuestion(rootView);

        loadDataToViews();
    }

    private void initQuestion(View rootView) {
        tvQuizQuestion = (TextView) rootView.findViewById(R.id.tvQuizQuestion);
        btnAnswerA = (Button) rootView.findViewById(R.id.btnAnswerA);
        btnAnswerB = (Button) rootView.findViewById(R.id.btnAnswerB);
        btnAnswerC = (Button) rootView.findViewById(R.id.btnAnswerC);
        btnAnswerD = (Button) rootView.findViewById(R.id.btnAnswerD);

        if (!artifact.getQuiz().equals("-")) {
            loadDataToQuestionView();
            if (haveUserStatistics){
                showTheCorrectAnswer();
            } else {
                btnAnswerA.setOnClickListener(this);
                btnAnswerB.setOnClickListener(this);
                btnAnswerC.setOnClickListener(this);
                btnAnswerD.setOnClickListener(this);
            }
        } else {
            hideQuestionLayout();

        }
    }

    private void showTheCorrectAnswer() {
        int correctAnswerID = quizHelper.getCorrectAnswerID();
        if (correctAnswerID == QuizHelper.ANSWER_A) {
            btnAnswerA.setBackgroundColor(getResources().getColor(R.color.correct_answer));
        } else if (correctAnswerID == QuizHelper.ANSWER_B) {
            btnAnswerB.setBackgroundColor(getResources().getColor(R.color.correct_answer));
        } else if (correctAnswerID == QuizHelper.ANSWER_C) {
            btnAnswerC.setBackgroundColor(getResources().getColor(R.color.correct_answer));
        } else if (correctAnswerID == QuizHelper.ANSWER_D) {
            btnAnswerD.setBackgroundColor(getResources().getColor(R.color.correct_answer));
        }
    }

    private void hideQuestionLayout() {
        tvQuizQuestion.setVisibility(View.GONE);
        btnAnswerA.setVisibility(View.GONE);
        btnAnswerB.setVisibility(View.GONE);
        btnAnswerC.setVisibility(View.GONE);
        btnAnswerD.setVisibility(View.GONE);
    }

    private void loadDataToQuestionView() {
        quizHelper = new QuizHelper(artifact.getQuiz());
        tvQuizQuestion.setText(quizHelper.getQuestionTitle());
        btnAnswerA.setText(quizHelper.getQuestionA());
        btnAnswerB.setText(quizHelper.getQuestionB());
        btnAnswerC.setText(quizHelper.getQuestionC());
        btnAnswerD.setText(quizHelper.getQuestionD());
    }

    private void setFabButton() {
        if (!Artifact.find(Artifact.class, "name = ?", new String[]{artifact.getName()}).isEmpty()) {
            disableFabButton();
        } else enableFabButton();
    }

    private void enableFabButton() {
        fabFavorite.setVisibility(FloatingActionButton.VISIBLE);
    }

    private void disableFabButton() {
        fabFavorite.setVisibility(FloatingActionButton.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        boolean correctAnswer = false;
        correctAnswer = isCorrectAnswer(id);
        setAnswerViewColor(correctAnswer, v);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(artifact.getUuID(), correctAnswer);
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).updateChildren(hashMap);
        onAnswerClickListener.onAnswerClick(artifact.getUuID(), correctAnswer);
    }

    private boolean isCorrectAnswer(int id) {
        boolean correctAnswer = false;
        switch (id) {
            case R.id.btnAnswerA:
                correctAnswer = quizHelper.isCorrectAnswer(QuizHelper.ANSWER_A);
                break;
            case R.id.btnAnswerB:
                correctAnswer = quizHelper.isCorrectAnswer(QuizHelper.ANSWER_B);
                break;
            case R.id.btnAnswerC:
                correctAnswer = quizHelper.isCorrectAnswer(QuizHelper.ANSWER_C);
                break;
            case R.id.btnAnswerD:
                correctAnswer = quizHelper.isCorrectAnswer(QuizHelper.ANSWER_D);
                break;
        }
        ;

        return correctAnswer;
    }

    private void setAnswerViewColor(boolean correctAnswer, View v) {
        if (correctAnswer) {
            v.setBackgroundColor(getResources().getColor(R.color.correct_answer));
            btnAnswerA.setClickable(false);
        } else {
            v.setBackgroundColor(getResources().getColor(R.color.in_correct_answer));
            showTheCorrectAnswer();
        }
        setUnClickableButton();

    }

    private void setUnClickableButton() {
        btnAnswerA.setClickable(false);
        btnAnswerB.setClickable(false);
        btnAnswerC.setClickable(false);
        btnAnswerD.setClickable(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.explorer_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public interface OnAnswerClickListener{
        public void onAnswerClick(String artifactUuID, boolean answer);
    }
}
