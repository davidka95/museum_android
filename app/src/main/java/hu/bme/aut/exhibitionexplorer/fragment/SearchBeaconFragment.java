package hu.bme.aut.exhibitionexplorer.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.altbeacon.beacon.BeaconManager;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;

/**
 * Created by Adam on 2016. 11. 18..
 */

public class SearchBeaconFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 104;
    public static final int PERMISSIONS_REQUEST_BLUETOOTH = 105;
    public static final String TAG = "SearchBeaconFragment";
    private RelativeLayout lySearchBeacon;
    private LinearLayout lyNeedBluetooth;
    private LinearLayout lyNotSupportedBluetoothLE;
    private Button btnBluetoothTurnOn;
    private ImageView ivBeaconIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_beacon, container, false);

        setHasOptionsMenu(true);

        lySearchBeacon = (RelativeLayout) rootView.findViewById(R.id.lySearchBeacon);
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
        ivBeaconIcon = (ImageView) rootView.findViewById(R.id.ivBeaconSearch);

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
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.beacon_icon_flash_fade);
                ivBeaconIcon.startAnimation(myFadeInAnimation);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String exhibitionUuID = sp.getString(Exhibition.KEY_CHOOSED_EXHIBITION, null);
        if (exhibitionUuID == null){
            inflater.inflate(R.menu.explorer_with_no_exhibition_menu, menu);
        } else {
            inflater.inflate(R.menu.explorer_menu, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
}
