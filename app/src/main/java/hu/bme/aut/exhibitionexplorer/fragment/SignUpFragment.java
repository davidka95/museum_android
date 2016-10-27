package hu.bme.aut.exhibitionexplorer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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
import hu.bme.aut.exhibitionexplorer.interfaces.OnSuccesfullLoginListener;

/**
 * Created by Adam on 2016. 10. 22..
 */

public class SignUpFragment extends Fragment {

    public static final String TAG = "SignUpFragment";

    private FirebaseAuth firebaseAuth;

    private EditText edEmail;
    private EditText edPassword;
    private EditText edPasswordConfirm;

    private Button btnSignUp;

    private TextView tvBack;

    private OnSuccesfullLoginListener onSuccesfullLoginListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();

        if (activity instanceof OnSuccesfullLoginListener) {
            onSuccesfullLoginListener = (OnSuccesfullLoginListener) activity;
        } else {
            throw new RuntimeException("Activity must implement OnSuccesFullLoginListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        initView(rootView);

        return rootView;
    }

    private void initView(final View rootView) {
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        edPassword = (EditText) rootView.findViewById(R.id.edPassword);
        edPasswordConfirm = (EditText) rootView.findViewById(R.id.edPasswordConfirm);

        tvBack = (TextView) rootView.findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnSignUp = (Button) rootView.findViewById(R.id.btnSIgnUp);
        setSignUpClickListener();
    }

    private void setSignUpClickListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edPassword.getText().toString();
                String confirmPassword = edPasswordConfirm.getText().toString();
                String email = edEmail.getText().toString();

                password = password.trim();
                confirmPassword = confirmPassword.trim();
                email = email.trim();

                if (password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {
                    showMissDataAlertDialog();
                } else if (!password.equals(confirmPassword)) {
                    showConfirmPasswordErrorDialog();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        onSuccesfullLoginListener.onSuccesfullLogin();
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

    private void showConfirmPasswordErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.confirm_email_error)
                .setTitle(R.string.signup_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMissDataAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.signup_error_message)
                .setTitle(R.string.signup_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
