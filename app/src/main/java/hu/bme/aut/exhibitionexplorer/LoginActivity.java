package hu.bme.aut.exhibitionexplorer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.exhibitionexplorer.fragment.SignInFragment;
import hu.bme.aut.exhibitionexplorer.fragment.SignUpFragment;
import hu.bme.aut.exhibitionexplorer.interfaces.OnSuccesfullLoginListener;

public class LoginActivity extends AppCompatActivity implements SignInFragment.OnSignUpButtonClickListener, OnSuccesfullLoginListener{
    FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        fragmentManager = getSupportFragmentManager();
        Fragment signInFragment = new SignInFragment();
        fragmentManager.beginTransaction().replace(R.id.activity_login, signInFragment).commit();
    }

    @Override
    public void onSignUp() {
        fragmentManager = getSupportFragmentManager();
        Fragment signUpFragment = new SignUpFragment();
        fragmentManager.beginTransaction().replace(R.id.activity_login, signUpFragment).commit();
    }

    @Override
    public void onSuccesfullLogin() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
