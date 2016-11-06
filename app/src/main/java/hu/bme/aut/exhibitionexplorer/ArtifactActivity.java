package hu.bme.aut.exhibitionexplorer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.fragment.ArtifactDetailFragment;

public class ArtifactActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artifact);

        Artifact artifact = getIntent().getParcelableExtra(Artifact.KEY_ARTIFACT_PARCELABLE);
        Bundle bundleArtifact = new Bundle();
        bundleArtifact.putParcelable(Artifact.KEY_ARTIFACT_PARCELABLE, artifact);
        Fragment artifactDetailFragment = new ArtifactDetailFragment();
        artifactDetailFragment.setArguments(bundleArtifact);
        showFragmentWithNoBackStack(artifactDetailFragment, ArtifactDetailFragment.TAG);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showFragmentWithNoBackStack(Fragment fragment, String tag) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        localFragmentTransaction.replace(R.id.fragment_container, fragment, tag)
                .commit();
    }
}
