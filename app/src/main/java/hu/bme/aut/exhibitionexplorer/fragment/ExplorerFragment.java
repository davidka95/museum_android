package hu.bme.aut.exhibitionexplorer.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import hu.bme.aut.exhibitionexplorer.ArtifactActivity;
import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.adapter.CatalogAdapter;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;

/**
 * Created by Adam on 2016. 10. 28..
 */

public class ExplorerFragment extends Fragment{

    public static String TAG = "ExplorerFragment";

    DatabaseReference mArtifactsReference;

    private KenBurnsView ivArtifactImage;
    private TextView tvArtifactName;
    private TextView tvArtifactDescription;
    private FloatingActionButton fabFavorite;

    Artifact artifact;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explorer, container, false);

        artifact = getArguments().getParcelable(Artifact.KEY_ARTIFACT_PARCELABLE);

        initView(rootView);

        //verifyBluetooth();

        return rootView;
    }

    private void loadDataToViews() {
        Log.d("a kiirando: ",artifact.getImageURL());
        Picasso.with(getContext()).load(artifact.getImageURL()).fit().centerCrop()
                .placeholder(R.mipmap.ic_launcher).into(ivArtifactImage);

        tvArtifactName.setText(artifact.getName());
        tvArtifactDescription.setText(artifact.getDescription());
    }

    private void initView(View rootView) {
        fabFavorite = (FloatingActionButton) rootView.findViewById(R.id.fabFavorite);
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Még nincs implementálva", Toast.LENGTH_SHORT).show();
            }
        });

        ivArtifactImage = (KenBurnsView) rootView.findViewById(R.id.kvArtifactImage);
        tvArtifactName = (TextView) rootView.findViewById(R.id.tvArtifactName);
        tvArtifactDescription = (TextView) rootView.findViewById(R.id.tvArtifactDescription);

        loadDataToViews();
    }

   /* private void verifyBluetooth() {

        try {
            if (!beaconManager.checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.title_bluetooth_not_enabled));
                builder.setMessage(getString(R.string.enable_bluetooth));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle(getString(R.string.title_bluetooth_le_not_available));
            builder.setMessage(getString(R.string.sorry_bluetooth_not_supported));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //finish();
                }
            });
            builder.show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == getActivity().RESULT_OK) {
                verifyBluetooth();
            } else {
                //finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    */
}
