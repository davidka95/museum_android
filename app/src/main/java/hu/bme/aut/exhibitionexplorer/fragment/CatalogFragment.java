package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.adapter.CatalogAdapter;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;

/**
 * Created by Adam on 2016. 10. 29..
 */

public class CatalogFragment extends Fragment {

    protected CatalogAdapter adapter;
    protected RecyclerView recyclerView;

    public static final String TAG = "CatalogFragment";
    public static final String ExhibitionTag = "ExhibitionID";
    FirebaseDatabase database;
    DatabaseReference mArtifactsReference;
    private Exhibition exhibition;
    private HashMap<String, Boolean> artifactsHere;

    OnArtifactItemClickListener onItemClickListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnArtifactItemClickListener) {
            onItemClickListener = (OnArtifactItemClickListener) activity;
        } else {
            throw new RuntimeException("Activity must implement CatalogFragment.OnArtifactItemClickListener interface");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);

        database = FirebaseDatabase.getInstance();
        mArtifactsReference = database.getReference("artifacts");
        exhibition = getArguments().getParcelable(Exhibition.KEY_EXHIBITION_PARCELABLE);
         artifactsHere = exhibition.getArtifactsHere();



        mArtifactsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asyncTask.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerView = (RecyclerView) rootView.findViewById(R.id.CatalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CatalogAdapter(getContext(), onItemClickListener);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public interface OnArtifactItemClickListener {
        public void onArtifactItemClick(Artifact artifact);
    }

    final AsyncTask<DataSnapshot, Void, CatalogAdapter> asyncTask = new AsyncTask<DataSnapshot, Void, CatalogAdapter>() {
        @Override
        protected CatalogAdapter doInBackground(DataSnapshot... params) {
            if (artifactsHere != null){
            for (DataSnapshot datasnapshot: params[0].getChildren()){
                if(artifactsHere.containsKey(datasnapshot.getKey())){
                    Artifact artifact = datasnapshot.getValue(Artifact.class);
                    artifact.setUuID(datasnapshot.getKey());
                    adapter.addArtifact(artifact);
                }
            }
            }

            return adapter;
        }

        @Override
        protected void onPostExecute(CatalogAdapter catalogAdapter) {
            catalogAdapter.notifyDataSetChanged();
        }
    };
}
