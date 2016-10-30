package hu.bme.aut.exhibitionexplorer.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.adapter.ExhibitionAdapter;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;

/**
 * Created by Zay on 2016.10.22..
 */

public class ExhibitionFragment extends Fragment
{
    public static final String TAG = "ExhibitionFragment";
    protected ExhibitionAdapter adapter;
    protected RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public interface OnItemClickListener{
        public void onItemClick(Exhibition exhibition);
    }

    OnItemClickListener onItemClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if(activity instanceof OnItemClickListener){
            onItemClickListener = (OnItemClickListener) activity;
        } else {
            throw new RuntimeException("Activity must implement ExhibitionFragment.OnItemClickListener interface");
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState)
    {
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

        Log.d("ExhibitionFragment", "ExhibitionFragment started");
        this.recyclerView = ((RecyclerView)localView.findViewById(R.id.ExchibitionsRecyclerView));
        this.adapter = new ExhibitionAdapter(getContext(), onItemClickListener);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView.setAdapter(adapter);
        return localView;
    }

    private class ExhibitionDatabaseHelper extends AsyncTask<DataSnapshot, Void , ExhibitionAdapter> {

        @Override
        protected ExhibitionAdapter doInBackground(DataSnapshot... params) {
            for (DataSnapshot datasnapshot: params[0].getChildren()){
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


}
