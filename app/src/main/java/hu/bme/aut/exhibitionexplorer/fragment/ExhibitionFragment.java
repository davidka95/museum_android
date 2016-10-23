package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.adapter.ExhibitionAdapter;

/**
 * Created by Zay on 2016.10.22..
 */

public class ExhibitionFragment extends Fragment
{
    public static final String TAG = "ExhibitionFragment";
    protected ExhibitionAdapter adapter;
    protected RecyclerView recyclerView;

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState)
    {
        View localView = layoutInflater.inflate(R.layout.exhibitions_fragment_layout, null, false);
        Log.d("ExhibitionFragment", "ExhibitionFragment started");
        this.recyclerView = ((RecyclerView)localView.findViewById(R.id.ExchibitionsRecyclerView));
        this.adapter = new ExhibitionAdapter();
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView.setAdapter(adapter);
        return localView;
    }
}
