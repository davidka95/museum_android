package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.exhibitionexplorer.R;

/**
 * Created by Zay on 2016.10.22..
 */

public class ExchibitionsFragment extends Fragment {

    public static final String TAG  = "ExchibitionFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exchibitions_fragment_layout, null, false);
        Log.d(TAG, "ExhibitionFragment started:");
        return rootView;
    }
}
