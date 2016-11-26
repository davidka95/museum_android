package hu.bme.aut.exhibitionexplorer;

import android.*;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.data.BeaconData;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;
import hu.bme.aut.exhibitionexplorer.fragment.CatalogFragment;
import hu.bme.aut.exhibitionexplorer.fragment.ExplorerFragment;
import hu.bme.aut.exhibitionexplorer.fragment.FavoriteFragment;
import hu.bme.aut.exhibitionexplorer.fragment.NullExhibitionFragment;
import hu.bme.aut.exhibitionexplorer.fragment.SearchBeaconFragment;
import hu.bme.aut.exhibitionexplorer.interfaces.OnArtifactItemClickListener;
import hu.bme.aut.exhibitionexplorer.interfaces.OnFavoriteListener;
import hu.bme.aut.exhibitionexplorer.interfaces.OnSearchExhibitionClickListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnSearchExhibitionClickListener,
        OnArtifactItemClickListener, BeaconConsumer, OnFavoriteListener {

    public static final int REQUEST_EXHIBITION = 102;
    public static final int REQUEST_QR_READER = 103;
    public static final int PERMISSIONS_REQUEST_CAMERA = 201;
    public static final String BEACON_TAG = "BEACON_TAG";
    public static final String KEY_NEAREST_BEACON = "KEY_NEAREST_BEACON";
    public static final String KEY_ACUTAL_ARTIFACT = "KEY_ACUTAL_ARTIFACT";
    public static final String KEY_CHECKED_NAV_MENU_ID = "KEY_CHECKED_NAV_MENU_ID";
    public static final int MIN_RSSI = -55;

    private String exhibitionUuID;
    private Exhibition exhibition = null;
    private HashMap<String, Boolean> artifactsHere;
    private ArrayList<Artifact> artifacts = new ArrayList<>();


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private NavigationView navigationView;
    private int checkedNavMenuID = R.id.nav_explore;

    private Boolean isAutomaticallyShowBeacon;
    private String nearestIBeacon = null;
    private Artifact actualArtifact = null;

    private BeaconManager beaconManager;

    private Toolbar toolbar;

    private Button btnNextArtifact;
    private TextView tvNextArtifactName;
    private ImageView ivNextArtifact;
    private CardView nextArtifactLayout;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (savedInstanceState != null) {
            nearestIBeacon = savedInstanceState.getString(KEY_NEAREST_BEACON);
            actualArtifact = savedInstanceState.getParcelable(KEY_ACUTAL_ARTIFACT);
            checkedNavMenuID = savedInstanceState.getInt(KEY_CHECKED_NAV_MENU_ID);
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        initNextArtifactLayout();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                if (key.equals(getString(R.string.key_pref_ibeacon))) {
                    isAutomaticallyShowBeacon = prefs.getBoolean(key, true);
                }

            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

        isAutomaticallyShowBeacon = prefs.getBoolean((getString(R.string.key_pref_ibeacon)), true);
        exhibitionUuID = prefs.getString(Exhibition.KEY_CHOOSED_EXHIBITION, null);

        if (mFirebaseUser == null) {
            loadLoginActivity();
        }

        exhibition = getIntent().getParcelableExtra(Exhibition.KEY_EXHIBITION_PARCELABLE);
        if (exhibition != null) {
            artifactsHere = exhibition.getArtifactsHere();
        }

        getArtifact();


        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));


    }

    private void initNextArtifactLayout() {
        nextArtifactLayout = (CardView) findViewById(R.id.nextArtifactLayout);
        ivNextArtifact = (ImageView) findViewById(R.id.ivNextArtifact);
        tvNextArtifactName = (TextView) findViewById(R.id.tvNextArtifactName);
        btnNextArtifact = (Button) findViewById(R.id.btnNextArtifact);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (exhibition != null) {
            if (exhibition.isBeacon()) {
                beaconManager.bind(this);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (exhibition != null) {
            if (exhibition.isBeacon()) {
                beaconManager.unbind(this);
            }
        }
    }

    private void getArtifact() {
        artifacts.clear();
        FirebaseDatabase.getInstance().getReference().child("artifacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LoadArtifactHelper loadArtifactHelper = new LoadArtifactHelper();
                loadArtifactHelper.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CHECKED_NAV_MENU_ID, checkedNavMenuID);
        outState.putString(KEY_NEAREST_BEACON, nearestIBeacon);
        outState.putParcelable(KEY_ACUTAL_ARTIFACT, actualArtifact);
    }

    private void loadLoginActivity() {
        Intent startLoginActivity = new Intent(this, LoginActivity.class);
        startLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startLoginActivity);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_explore) {
            setNavigationViewTitle(R.id.nav_explore);
            if (exhibition != null) {
                if (nearestIBeacon == null) {
                    showFragmentWithNoBackStack(new SearchBeaconFragment(), null);
                } else if (actualArtifact != null) {
                    showExplorerFragmentByID(actualArtifact.getUuID(), false);
                }
            } else {
                showFragmentWithNoBackStack(new NullExhibitionFragment(), NullExhibitionFragment.TAG);
            }
        } else if (id == R.id.nav_catalog) {
            setNavigationViewTitle(R.id.nav_catalog);
            if (exhibition != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Exhibition.KEY_EXHIBITION_PARCELABLE, exhibition);
                CatalogFragment catalogFragment = getCatalogFragment();
                catalogFragment.setArguments(bundle);
                showFragmentWithNoBackStack(catalogFragment, CatalogFragment.TAG);
            } else {
                showFragmentWithNoBackStack(new NullExhibitionFragment(), NullExhibitionFragment.TAG);
            }
        } else if (id == R.id.nav_favorite) {
            setNavigationViewTitle(R.id.nav_favorite);
            showFragmentWithNoBackStack(new FavoriteFragment(), FavoriteFragment.TAG);
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_help_and_feedbeck) {

        } else if (id == R.id.nav_sign_out) {
            writeExhibitionUuIDToSharedPreferences(null);
            mFirebaseAuth.signOut();
            loadLoginActivity();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @NonNull
    private CatalogFragment getCatalogFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(CatalogFragment.ExhibitionTag, exhibitionUuID);
        CatalogFragment catalogFragment = new CatalogFragment();
        catalogFragment.setArguments(bundle);
        return catalogFragment;
    }

    private void showFragmentWithNoBackStack(Fragment fragment, String tag) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        localFragmentTransaction.replace(R.id.content_main, fragment, tag)
                .commit();
    }

    private void showFragmentWithAnimation(Fragment fragment, String tag) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        localFragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.content_main, fragment, tag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();
        if (id == R.id.menu_item_exhibition) {
            startExhibitionActivity();
        } else if (id == R.id.menu_item_qr_code) {
            startQrReading();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startQrReading() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            startQrReaderActivity();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(findViewById(R.id.content_main), "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startQrReaderActivity();
                } else {
                    Snackbar.make(findViewById(R.id.content_main), "Camera access denied, cannot read Qr code",
                            Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void startQrReaderActivity() {
        Intent qrReaderIntent = new Intent(this, QrReaderActivity.class);
        startActivityForResult(qrReaderIntent, REQUEST_QR_READER);
    }

    private void initNavigationDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.activity_main_drawer_signed_in);

        navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onSearchExhibitionClick() {
        startExhibitionActivity();
    }

    private void startExhibitionActivity() {
        Intent startExhibitionIntent = new Intent(this, ExhibitionActivity.class);
        startActivityForResult(startExhibitionIntent, REQUEST_EXHIBITION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EXHIBITION) {
                exhibition = data.getParcelableExtra(Exhibition.KEY_EXHIBITION_PARCELABLE);
                writeExhibitionUuIDToSharedPreferences(exhibition.getUuID());
                artifactsHere = exhibition.getArtifactsHere();
                actualArtifact = null;
                nearestIBeacon = null;
                showFragmentWithNoBackStack(new SearchBeaconFragment(), SearchBeaconFragment.TAG);
                getArtifact();
            } else if (requestCode == REQUEST_QR_READER) {
                String artifactID = data.getStringExtra(Artifact.KEY_ARTIFACT_ID);
                showExplorerFragmentByID(artifactID, true);
            }
        }
    }

    public void showExplorerFragmentByID(String artifactID, boolean withAnimation) {
        Bundle bundle = new Bundle();
        getArtifactByID(artifactID);
        Artifact artifact = getArtifactByID(artifactID);
        if (artifact != null) {
            bundle.putParcelable(Artifact.KEY_ARTIFACT_PARCELABLE, artifact);
            actualArtifact = artifact;
            Fragment explorerFragment = new ExplorerFragment();
            explorerFragment.setArguments(bundle);
            if (withAnimation) {
                showFragmentWithAnimation(explorerFragment, ExplorerFragment.TAG);
            } else {
                showFragmentWithNoBackStack(explorerFragment, ExplorerFragment.TAG);
            }
        }
    }

    private Artifact getArtifactByID(String artifactID) {
        for (Artifact artifact : artifacts) {
            if (artifact.getUuID().equals(artifactID)) {
                return artifact;
            }
        }

        return null;
    }

    private void writeExhibitionUuIDToSharedPreferences(String exhibitionUuID) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Exhibition.KEY_CHOOSED_EXHIBITION, exhibition.getUuID());
        editor.commit();
    }

    @Override
    public void onArtifactItemClick(Artifact artifact) {
        Intent startArtifactActivity = new Intent(this, ArtifactActivity.class);
        startArtifactActivity.putExtra(Artifact.KEY_ARTIFACT_PARCELABLE, artifact);
        startActivity(startArtifactActivity);
    }

    private void initCheckedItem() {
        navigationView.setCheckedItem(checkedNavMenuID);
        onNavigationItemSelected(navigationView.getMenu().findItem(checkedNavMenuID));
        setNavigationViewTitle(checkedNavMenuID);
    }

    private void setNavigationViewTitle(int id) {
        checkedNavMenuID = id;
        getSupportActionBar().setTitle(navigationView.getMenu().findItem(checkedNavMenuID).getTitle());
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0 && exhibition != null && checkedNavMenuID == R.id.nav_explore) {
                    ArrayList<Beacon> arrayBeacons = new ArrayList<Beacon>(beacons);
                    Collections.sort(arrayBeacons, new Comparator<Beacon>() {
                        @Override
                        public int compare(Beacon b1, Beacon b2) {
                            return b2.getRssi() - b1.getRssi();
                        }
                    });

                    for (Beacon beacon : arrayBeacons) {
                        if (beacon.getRssi() > MIN_RSSI)
                        manageBeacon(beacon);
                    }
                }
            }
        });
        try {
            if (exhibition != null) {
                UUID uuid = UUID.fromString(exhibition.getBeaconRegion());
                beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", Identifier.fromUuid(uuid)
                        , null, null));
            }
        } catch (RemoteException e) {
        }
    }

    private void manageBeacon(final Beacon beacon) {
        if (nearestIBeacon == null || !nearestIBeacon.equals(beacon.getId3().toString())) {
            nearestIBeacon = beacon.getId3().toString();
            final Bundle bundle = new Bundle();
            FirebaseDatabase.getInstance().getReference().child("beacons").child(beacon.getId3().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final BeaconData beaconData;
                            beaconData = dataSnapshot.getValue(BeaconData.class);
                            beaconData.setMinorID(Long.valueOf(dataSnapshot.getKey()));
                            if (beaconData != null) {
                                if (isAutomaticallyShowBeacon) {
                                    showExplorerFragmentByID(beaconData.getArtifactId(), true);
                                } else {
                                    loadNextArtifactViewData(beaconData.getArtifactId());
                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        Log.d(BEACON_TAG, "manageBeacon: " + beacon.getId1() + " ID");

    }

    private void loadNextArtifactViewData(final String artifactId) {
        Artifact artifact = getArtifactByID(artifactId);
        if (artifact != null) {
            Picasso.with(this).load(artifact.getImageURL()).into(ivNextArtifact);
            tvNextArtifactName.setText(artifact.getName());
            btnNextArtifact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showExplorerFragmentByID(artifactId, true);
                    nextArtifactLayout.setVisibility(View.GONE);
                }
            });
            nextArtifactLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void ArtifactToFavorite(Artifact artifact) {
        artifact.save();
    }

    @Override
    public void ArtifactRemovedFromFavorite(Artifact artifact) {
        artifact.delete();
    }

    class LoadArtifactHelper extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... params) {
            if (artifactsHere != null) {
                for (DataSnapshot datasnapshot : params[0].getChildren()) {
                    if (artifactsHere.containsKey(datasnapshot.getKey())) {
                        Artifact artifact = datasnapshot.getValue(Artifact.class);
                        artifact.setUuID(datasnapshot.getKey());
                        artifacts.add(artifact);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            initCheckedItem();
        }
    }
}