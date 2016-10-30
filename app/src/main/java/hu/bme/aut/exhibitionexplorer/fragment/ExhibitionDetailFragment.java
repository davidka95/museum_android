package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;

/**
 * Created by Adam on 2016. 10. 29..
 */

public class ExhibitionDetailFragment extends Fragment {

    public static final String TAG = "ExhibitionDetailFragment";

    private Exhibition exhibition;

    private OnSelectExhibitionListener listener;

    private FloatingActionButton fabSelect;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnSelectExhibitionListener) {
            listener = (OnSelectExhibitionListener) activity;
        } else {
            throw new RuntimeException("Activity must implement OnSelectExhibitionListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exhibition_detail, container, false);


        fabSelect = (FloatingActionButton) rootView.findViewById(R.id.fabSelect);
        fabSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectExhibition(exhibition);
            }
        });
        exhibition = getArguments().getParcelable(Exhibition.KEY_EXHIBITION_PARCELABLE);
        if (exhibition != null) {
            initView(rootView);
        }

        return rootView;
    }

    private void initView(View rootView) {
        //TODO
    }

    public interface OnSelectExhibitionListener {
        public void onSelectExhibition(Exhibition exhibition);
    }
}
