package hu.bme.aut.exhibitionexplorer.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.altbeacon.beacon.BeaconManager;

import hu.bme.aut.exhibitionexplorer.R;

/**
 * Created by Adam on 2016. 11. 18..
 */

public class SearchBeaconFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 104;
    public static final int PERMISSIONS_REQUEST_BLUETOOTH = 105;
    private LinearLayout lySearchBeacon;
    private LinearLayout lyNeedBluetooth;
    private LinearLayout lyNotSupportedBluetoothLE;
    private Button btnBluetoothTurnOn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_beacon, container, false);

        lySearchBeacon = (LinearLayout) rootView.findViewById(R.id.lySearchBeacon);
        lyNeedBluetooth = (LinearLayout) rootView.findViewById(R.id.lyNeedBluetooth);
        lyNotSupportedBluetoothLE = (LinearLayout) rootView.findViewById(R.id.lyNotSupportedBluetooth);
        btnBluetoothTurnOn = (Button) rootView.findViewById(R.id.btnBluetoothTurnOn);
        btnBluetoothTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBluetoothPermission();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });

        verifyBluetooth();

        return rootView;
    }

    private void checkBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.BLUETOOTH)) {
                Snackbar.make(getView(), "Bluetooth access is required to display the camera preview.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.BLUETOOTH},
                                PERMISSIONS_REQUEST_BLUETOOTH);
                    }
                }).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.BLUETOOTH},
                        PERMISSIONS_REQUEST_BLUETOOTH);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_BLUETOOTH) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(getView(), "Camera permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                verifyBluetooth();
            } else {
                // Permission request was denied.
                Snackbar.make(getView(), "Camera permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void verifyBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if (Build.VERSION.SDK_INT < 18 || bluetoothAdapter == null) {
                lyNotSupportedBluetoothLE.setVisibility(LinearLayout.VISIBLE);
                lySearchBeacon.setVisibility(LinearLayout.INVISIBLE);
                lyNeedBluetooth.setVisibility(LinearLayout.INVISIBLE);

            } else if (!bluetoothAdapter.isEnabled()) {
                lyNotSupportedBluetoothLE.setVisibility(LinearLayout.INVISIBLE);
                lySearchBeacon.setVisibility(LinearLayout.INVISIBLE);
                lyNeedBluetooth.setVisibility(LinearLayout.VISIBLE);
            } else {
                lyNotSupportedBluetoothLE.setVisibility(LinearLayout.INVISIBLE);
                lySearchBeacon.setVisibility(LinearLayout.VISIBLE);
                lyNeedBluetooth.setVisibility(LinearLayout.INVISIBLE);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();


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


}
