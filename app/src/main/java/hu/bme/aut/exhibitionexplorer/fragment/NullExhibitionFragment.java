package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.interfaces.OnSearchExhibitionClickListener;

/**
 * Created by Adam on 2016. 10. 29..
 */

public class NullExhibitionFragment extends Fragment{

    public static String TAG = "nullExhibitionFragment";

    OnSearchExhibitionClickListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnSearchExhibitionClickListener){
            listener = (OnSearchExhibitionClickListener) activity;
        } else {
            throw new RuntimeException("Activity must implement OnSearchExhibitionClickListener interface");
        }
    }

    FloatingActionButton fabSearchExhibition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_null_exhibition, container, false);

        fabSearchExhibition = (FloatingActionButton) rootView.findViewById(R.id.fabSearchExhibition);
        fabSearchExhibition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSearchExhibitionClick();
            }
        });

        return rootView;
    }
}
