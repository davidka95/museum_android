package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.adapter.FavoriteAdapter;
import hu.bme.aut.exhibitionexplorer.adapter.TouchHelperCallback;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.interfaces.FavoriteTouchHelperAdapter;

/**
 * Created by Zay on 2016.11.14..
 */

public class FavoriteFragment extends Fragment {

    public static final String TAG = "FavoriteFragment";
    protected FavoriteAdapter adapter;
    protected RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.FragmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FavoriteAdapter(getContext());
        loadItemsInBackground();
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        return rootView;
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<Artifact>>() {

            @Override
            protected List<Artifact> doInBackground(Void... voids) {
                return Artifact.listAll(Artifact.class);
            }

            @Override
            protected void onPostExecute(List<Artifact> shoppingItems) {
                super.onPostExecute(shoppingItems);
                adapter.update(shoppingItems);
            }
        }.execute();
    }
}
