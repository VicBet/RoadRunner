package com.roadrunner.android.roadrunner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignInActivity extends AppCompatActivity {

    private Button mBtnSignIn, mBtnRegister;
    private TextView mTxtForgotPassword;
    private EditText mEdtEmail, mEdtPassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseUsers;
    private ProgressBar mProgressSignIn;
    private FirebaseUser user;
    public static int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mBtnSignIn = (Button) findViewById(R.id.btnSignIn);
        mBtnRegister = (Button) findViewById(R.id.btnRegister);
        mTxtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtPassword = (EditText) findViewById(R.id.edtPassword);
        mProgressSignIn = (ProgressBar) findViewById(R.id.progressBarSignIn);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){

            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();

        }

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });

        mTxtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(SignInActivity.this, PasswordResetActivity.class));
            }
        });


    }

    private void userLogin() {

        mProgressSignIn.setVisibility(View.VISIBLE);

        // Reset errors.
        mEdtEmail.setError(null);
        mEdtPassword.setError(null);


        final String email = mEdtEmail.getText().toString().trim();
        String password = mEdtPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {

            mProgressSignIn.setVisibility(View.INVISIBLE);
            String errorMessage;

            errorMessage = "Enter Your Email and Password";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
            return;

            // Check for a valid password, if the user entered one.
        } else if (!TextUtils.isEmpty(email) && !isEmailValid(email)) {
            mProgressSignIn.setVisibility(View.INVISIBLE);
            mEdtEmail.setError(("Invalid Email"));
            focusView = mEdtEmail;
            cancel = true;


        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mProgressSignIn.setVisibility(View.INVISIBLE);

            mEdtPassword.setError(("Password is too short"));
            focusView = mEdtPassword;
            cancel = true;

        } else if (TextUtils.isEmpty(password)) {

            mProgressSignIn.setVisibility(View.INVISIBLE);
            String errorMessage;

            errorMessage = "Enter Your Password";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;
        }

        // Check for a valid email address.
        else if (TextUtils.isEmpty(email)) {

            mProgressSignIn.setVisibility(View.INVISIBLE);
            String errorMessage;

            errorMessage = "Enter Your Email";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;


        }  else if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();


        } else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && isEmailValid(email) && isPasswordValid(password)) {
            mProgressSignIn.setVisibility(View.VISIBLE);

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressSignIn.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser mCurrentUser = mAuth.getCurrentUser();
                                String value2 = mCurrentUser.getUid();

                                mDatabaseUsers.child(value2).child("device_token").setValue(deviceToken);
                                mDatabaseUsers.child(value2).child("uid").setValue(value2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(SignInActivity.this, "Welcome back",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                            finish();

                                        }
                                    }
                                });


                            } else {


                                String errorMessage;

                                errorMessage = "Could not Sign In! Try Again.";

                                Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

                            }
                        }

                    });


        }
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
