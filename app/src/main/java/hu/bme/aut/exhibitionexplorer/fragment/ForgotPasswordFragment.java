package hu.bme.aut.exhibitionexplorer.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.exhibitionexplorer.R;

/**
 * Created by Adam on 2016. 10. 23..
 */

public class ForgotPasswordFragment extends Fragment {
    public static final String TAG = "ForgotPasswordFragment";

    private FirebaseAuth firebaseAuth;

    EditText edEmail;

    Button btnSendEmail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);

        btnSendEmail = (Button) rootView.findViewById(R.id.btnSendEmail);
        setButtonSendEmailClickListener();
    }

    private void setButtonSendEmailClickListener() {
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edEmail.getText().toString().trim();


                if (email.isEmpty()) {
                    showMissDataAlertDialog();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                showSentForgotPasswordEmailAlertDialog(email);
                            } else {
                                showForgotPasswordErrorAlertDialog(task);
                            }
                        }
                    });

                }
            }
        });
    }

    private void showSentForgotPasswordEmailAlertDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getContext().getString(R.string.forgot_password_succesfull_sent_email_message, email))
                .setTitle(R.string.forgot_password_succesfull_sent_email_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showForgotPasswordErrorAlertDialog(Task<Void> task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(task.getException().getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMissDataAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.forgot_password_miss_data_error_message)
                .setTitle(R.string.forgot_password_miss_data_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
