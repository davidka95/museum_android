package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import hu.bme.aut.exhibitionexplorer.R;

/**
 * Created by Adam on 2016. 10. 28..
 */

public class ExplorerFragment extends Fragment{

    public static String TAG = "ExplorerFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explorer, container, false);

        Toast.makeText(getContext(), "ittvagyokragyogok", Toast.LENGTH_SHORT).show();

        Bundle exhibitionBundle = getArguments();


        return rootView;
    }
}
