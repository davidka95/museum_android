package hu.bme.aut.exhibitionexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hu.bme.aut.exhibitionexplorer.fragment.ExhibitionFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser == null){
            loadLoginActivity();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationDrawer();
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

        if (id == R.id.nav_exhibition) {
            showFragment(new ExhibitionFragment(), ExhibitionFragment.TAG);
        } else if (id == R.id.nav_favorite) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help_and_feedbeck) {

        } else if (id == R.id.nav_sign_in) {
            Intent startLoginActivity = new Intent(this, LoginActivity.class);
            startActivity(startLoginActivity);
        } else if (id == R.id.nav_sign_out) {
            mFirebaseAuth.signOut();
            recreate();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(ExhibitionFragment exhibitionFragment, String tag) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();

        localFragmentTransaction.replace(R.id.content_main, exhibitionFragment, tag);
        localFragmentTransaction.addToBackStack(null);

        localFragmentTransaction.commit();
    }

    private void initNavigationDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(mFirebaseUser == null){
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        } else {
            navigationView.inflateMenu(R.menu.activity_main_drawer_signed_in);
        }
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_exhibition);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_exhibition));
    }
}
