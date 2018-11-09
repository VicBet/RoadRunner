package com.roadrunner.android.roadrunner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private Button buttonResetPassword;
    private EditText editTextEmail;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        buttonResetPassword = (Button) findViewById(R.id.buttonResetPassword);
        editTextEmail = (EditText) findViewById(R.id.emailReset);

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

    }
    private void forgotPassword(){
        String email = editTextEmail.getText().toString().trim();

        editTextEmail.setError(null);

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(email)){
            String errorMessage;

            errorMessage = "Enter Your Email";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;
        }
        if (!isEmailValid(email)) {
            editTextEmail.setError("Invalid Email");
            focusView = editTextEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();



        }
        progressDialog.setMessage("Reseting password... ");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            Intent mainIntent = new Intent(PasswordResetActivity.this, SignInActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();

                            String errorMessage;

                            errorMessage = "A Password Reset Email Has Been Sent To Your Email";

                            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();

                            //Toast.makeText(PasswordResetActivity.this, "An Email With Your Password Reset Has Been Sent!", Toast.LENGTH_SHORT).show();

                        } else {

                            String errorMessage;

                            errorMessage = "Could Not Reset Your Password! Try Again.";

                            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
                            //Toast.makeText(PasswordResetActivity.this, "Could Not Reset Your Password! Try Again Later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}

