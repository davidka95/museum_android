package hu.bme.aut.exhibitionexplorer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import hu.bme.aut.exhibitionexplorer.data.Exhibition;
import hu.bme.aut.exhibitionexplorer.fragment.ExhibitionDetailFragment;
import hu.bme.aut.exhibitionexplorer.fragment.ExhibitionFragment;

public class ExhibitionActivity extends AppCompatActivity implements ExhibitionFragment.OnItemClickListener, ExhibitionDetailFragment.OnSelectExhibitionListener{
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibition);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(savedInstanceState ==  null)
            showFragmentWithNoBackStack(new ExhibitionFragment(), ExhibitionFragment.TAG);

    }

    private void showFragmentWithNoBackStack(ExhibitionFragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    private void showFragmentWithBackStack(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onItemClick(Exhibition exhibition) {
        ExhibitionDetailFragment exhibitionDetailFragment = new ExhibitionDetailFragment();
        Bundle exhibitionBundle = new Bundle();
        exhibitionBundle.putParcelable(Exhibition.KEY_EXHIBITION_PARCELABLE, exhibition);
        exhibitionDetailFragment.setArguments(exhibitionBundle);
        showFragmentWithBackStack(exhibitionDetailFragment, ExhibitionDetailFragment.TAG);
    }

    @Override
    public void onSelectExhibition(Exhibition exhibition) {
        Intent intentData = new Intent();
        intentData.putExtra(Exhibition.KEY_EXHIBITION_PARCELABLE, exhibition);
        setResult(RESULT_OK, intentData);
        finish();
    }
}
