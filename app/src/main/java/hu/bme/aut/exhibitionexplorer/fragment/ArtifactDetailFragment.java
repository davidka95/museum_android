package hu.bme.aut.exhibitionexplorer.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import hu.bme.aut.exhibitionexplorer.PicassoCache;
import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.interfaces.OnFavoriteListener;
import hu.bme.aut.exhibitionexplorer.quiz.QuizHelper;

/**
 * Created by Zay on 2016.11.06..
 */

public class ArtifactDetailFragment extends Fragment {

    public static final String TAG = "ArtifactDetailFragment";
    private Artifact artifact;

    private KenBurnsView ivArtifactImage;
    private TextView tvArtifactName;
    private TextView tvArtifactDescription;
    private FloatingActionButton fabFavorite;
    private OnFavoriteListener onFavoriteAddedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnFavoriteListener) {
            onFavoriteAddedListener = (OnFavoriteListener) activity;
        } else {
            throw new RuntimeException("Activity must implement OnFavoriteListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artifact_detail, container, false);
        artifact = getArguments().getParcelable(Artifact.KEY_ARTIFACT_PARCELABLE);
        if (artifact != null) {
            initView(rootView);
        }

        return rootView;
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

        loadDataToViews();
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

    private void loadDataToViews() {
        Log.d("a kiirando: ", artifact.getImageURL());
        PicassoCache.makeImageRequest(getContext(), ivArtifactImage, artifact.getImageURL());

        tvArtifactName.setText(artifact.getName());
        tvArtifactDescription.setText(artifact.getDescription());
    }
}
