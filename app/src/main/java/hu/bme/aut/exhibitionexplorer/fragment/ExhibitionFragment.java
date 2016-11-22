package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.adapter.ExhibitionAdapter;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;

/**
 * Created by Zay on 2016.10.22..
 */

public class ExhibitionFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static final String TAG = "ExhibitionFragment";
    protected ExhibitionAdapter adapter;
    protected RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private SearchView mSearchView;
    private MenuItem searchMenuItem;

    public interface OnItemClickListener {
        public void onItemClick(Exhibition exhibition);
    }

    OnItemClickListener onItemClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnItemClickListener) {
            onItemClickListener = (OnItemClickListener) activity;
        } else {
            throw new RuntimeException("Activity must implement ExhibitionFragment.OnArtifactItemClickListener interface");
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View localView = layoutInflater.inflate(R.layout.exhibitions_fragment_layout, container, false);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("exhibitions");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new ExhibitionDatabaseHelper().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setHasOptionsMenu(true);

        Log.d("ExhibitionFragment", "ExhibitionFragment started");
        this.recyclerView = ((RecyclerView) localView.findViewById(R.id.ExhibitionsRecyclerView));
        this.adapter = new ExhibitionAdapter(getContext(), onItemClickListener);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView.setAdapter(adapter);
        return localView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_favorite_search_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class ExhibitionDatabaseHelper extends AsyncTask<DataSnapshot, Void, ExhibitionAdapter> {

        @Override
        protected ExhibitionAdapter doInBackground(DataSnapshot... params) {
            for (DataSnapshot datasnapshot : params[0].getChildren()) {
                Exhibition exhibition = datasnapshot.getValue(Exhibition.class);
                exhibition.setUuID(datasnapshot.getKey());
                adapter.addExhibition(exhibition);
            }

            return adapter;
        }

        @Override
        protected void onPostExecute(ExhibitionAdapter adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }


}
