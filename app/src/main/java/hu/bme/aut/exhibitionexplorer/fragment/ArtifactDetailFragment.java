package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.quiz.QuizHelper;

/**
 * Created by Zay on 2016.11.06..
 */

public class ArtifactDetailFragment extends Fragment {

    public static final String TAG = "ArtifactDetailFragment";
    private Artifact artifact;

    private ImageView ivArtifactImage;
    private TextView tvArtifactName;
    private TextView tvArtifactDescription;
    private FloatingActionButton fabFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artifact_detail, container, false);
        artifact = getArguments().getParcelable(Artifact.KEY_ARTIFACT_PARCELABLE);
        if (artifact != null) {
            initView(rootView);
        }

        return rootView;
    }

    private void initView(View rootView) {
        fabFavorite = (FloatingActionButton) rootView.findViewById(R.id.fabFavorite);
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Még nincs implementálva", Toast.LENGTH_SHORT).show();
            }
        });
        
        ivArtifactImage = (ImageView) rootView.findViewById(R.id.ivArtifactImage);
        tvArtifactName = (TextView) rootView.findViewById(R.id.tvArtifactName);
        tvArtifactDescription = (TextView) rootView.findViewById(R.id.tvArtifactDescription);

        loadDataToViews();
    }

    private void loadDataToViews() {
        Log.d("a kiirando: ",artifact.getImageURL());
        Picasso.with(getContext()).load(artifact.getImageURL()).fit().centerCrop()
                .placeholder(R.mipmap.ic_launcher).into(ivArtifactImage);

        tvArtifactName.setText(artifact.getName());
        tvArtifactDescription.setText(artifact.getDescription());
    }
}
