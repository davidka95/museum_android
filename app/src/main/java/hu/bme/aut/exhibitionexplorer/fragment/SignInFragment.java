package hu.bme.aut.exhibitionexplorer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.exhibitionexplorer.R;

/**
 * Created by Adam on 2016. 10. 22..
 */

public class SignInFragment extends Fragment{
    private EditText edEmail;
    private EditText edPassword;
    private Button btnSignIn;
    private TextView tvSignUp;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log_in, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        initView(rootView);

        return rootView;
    }

    private void initView(final View rootView) {
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        edPassword = (EditText) rootView.findViewById(R.id.edPassword);

        btnSignIn = (Button) rootView.findViewById(R.id.btnSignIn);
        setSignInButtonClickListener();

        tvSignUp = (TextView) rootView.findViewById(R.id.tvSignUp);
    }

    private void setSignInButtonClickListener() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();

                email = email.trim();
                password = password.trim();

                if(email.isEmpty() || password.isEmpty()){
                    showMissDataAlertDialog();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "LoginSuccesFull", Toast.LENGTH_SHORT);
                                    } else {
                                        showSignInErrorAlertDialog(task);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void showSignInErrorAlertDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(task.getException().getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showMissDataAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.login_error_message)
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface OnSignUpButtonClickListener{
        public void onSignUp();
    }


}
