package hu.bme.aut.exhibitionexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.zip.Inflater;

import hu.bme.aut.exhibitionexplorer.data.Exhibition;
import hu.bme.aut.exhibitionexplorer.fragment.CatalogFragment;
import hu.bme.aut.exhibitionexplorer.fragment.ExhibitionFragment;
import hu.bme.aut.exhibitionexplorer.fragment.ExplorerFragment;
import hu.bme.aut.exhibitionexplorer.fragment.NullExhibitionFragment;
import hu.bme.aut.exhibitionexplorer.interfaces.OnSearchExhibitionClickListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnSearchExhibitionClickListener {

    public static final int REQUEST_EXHIBITION = 102;
    public static String KEY_CHOOSED_EXHIBITION = "KEY_CHOOSED_EXHIBITION";

    private String exhibitionUuID;
    private Exhibition exhibition = null;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    NavigationView navigationView;
    private int checkedNavMenuID;

    Toolbar toolbar;

    private void getExhibition(){
        if(exhibitionUuID!=null){

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("exhibitions").child(exhibitionUuID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exhibition = dataSnapshot.getValue(Exhibition.class);
                initNavigationDrawer();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        } else {
            initNavigationDrawer();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        exhibitionUuID = sp.getString(KEY_CHOOSED_EXHIBITION, null);
        getExhibition();

        if (mFirebaseUser == null) {
            loadLoginActivity();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            checkedNavMenuID = R.id.nav_explore;
            if (exhibition!=null) {
                showFragment(new ExplorerFragment(), ExplorerFragment.TAG);
            } else {
                showFragment(new NullExhibitionFragment(), NullExhibitionFragment.TAG);
            }
        } else if (id == R.id.nav_catalog) {
            checkedNavMenuID = R.id.nav_catalog;
            if (exhibition!=null) {
                showFragment(new CatalogFragment(), CatalogFragment.TAG);
            } else {
                showFragment(new NullExhibitionFragment(), NullExhibitionFragment.TAG);
            }
        } else if (id == R.id.nav_favorite) {
            checkedNavMenuID = R.id.nav_favorite;
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help_and_feedbeck) {

        } else if (id == R.id.nav_sign_out) {
            mFirebaseAuth.signOut();
            recreate();
        }
        getSupportActionBar().setTitle(navigationView.getMenu().findItem(checkedNavMenuID).getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();

        localFragmentTransaction.replace(R.id.content_main, fragment, tag);
        localFragmentTransaction.addToBackStack(null);

        localFragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();
        if(id == R.id.menu_item_exhibition){
            startExhibitionActivity();
        }
        return super.onOptionsItemSelected(item);
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
        checkedNavMenuID = R.id.nav_explore;
        navigationView.setCheckedItem(R.id.nav_explore);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_explore));
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
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_EXHIBITION){
                exhibition = data.getParcelableExtra(Exhibition.KEY_EXHIBITION_PARCELABLE);
                SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_CHOOSED_EXHIBITION, exhibition.getUuID());
                editor.commit();
                onNavigationItemSelected(navigationView.getMenu().findItem(checkedNavMenuID));
            }
        }
    }
}
