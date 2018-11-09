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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private Button mSigInBack,mRegister;
    private EditText mEdtConfirmPasswordR, mEdtEmailR, mEdtPasswordR;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseUsers;
    private ProgressBar mProgressSignUp;
    public static int APP_REQUEST_CODE = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mSigInBack = (Button) findViewById(R.id.btnSignInBack);
        mRegister = (Button) findViewById(R.id.btnRegister);
        mEdtConfirmPasswordR = (EditText) findViewById(R.id.edtConfirmPasswordR);
        mEdtEmailR = (EditText) findViewById(R.id.edtEmailR);
        mEdtPasswordR = (EditText) findViewById(R.id.edtPasswordR);
        mProgressSignUp = (ProgressBar) findViewById(R.id.progressBarSignUp);


        mSigInBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
            }
        });
    }
    private void userRegister() {

        mProgressSignUp.setVisibility(View.VISIBLE);

        // Reset errors.
        mEdtEmailR.setError(null);
        mEdtPasswordR.setError(null);
        mEdtConfirmPasswordR.setError(null);

        final String cnpassword = mEdtConfirmPasswordR.getText().toString().trim();
        final String email = mEdtEmailR.getText().toString().trim();
        final String password = mEdtPasswordR.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(cnpassword)) {
            mProgressSignUp.setVisibility(View.GONE);

            String errorMessage;

            errorMessage = "Enter Your Email and Password";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;

        }
        else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mProgressSignUp.setVisibility(View.GONE);

            mEdtPasswordR.setError("Password is too short");
            focusView = mEdtPasswordR;
            cancel = true;


        }
        else if (!TextUtils.isEmpty(cnpassword) && !isConPassValid(cnpassword)) {
            mProgressSignUp.setVisibility(View.GONE);

            mEdtConfirmPasswordR.setError("The password you have entered does not match");
            focusView = mEdtConfirmPasswordR;
            cancel = true;


        }else if (TextUtils.isEmpty(cnpassword)){

            mProgressSignUp.setVisibility(View.GONE);
            String errorMessage;

            errorMessage = "Confirm Your Password";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;

        }

        // Check for a valid email address.
        else if (TextUtils.isEmpty(email)) {
            mProgressSignUp.setVisibility(View.GONE);
            String errorMessage;

            errorMessage = "Enter Your Email";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;
        } else if (!TextUtils.isEmpty(email) && !isEmailValid(email)) {
            mProgressSignUp.setVisibility(View.GONE);
            mEdtEmailR.setError("Invalid Email");
            focusView = mEdtEmailR;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password)) {

            mProgressSignUp.setVisibility(View.GONE);
            String errorMessage;

            errorMessage = "Enter Your Password";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;
        }




        else if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();


        }else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(cnpassword) && isConPassValid(cnpassword)
                && isEmailValid(email) && isPasswordValid(password)) {

            mProgressSignUp.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressSignUp.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().sendEmailVerification();


                                String uid = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabaseUsers.child(uid);
                                //DatabaseReference current_user_image = mDatabase.child(username);

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                current_user_db.child("device_token").setValue(deviceToken);
                                current_user_db.child("username").setValue("No Username");
                                current_user_db.child("uid").setValue(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            mProgressSignUp.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this, "Registered Successfully",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, FinishSetupActivity.class));
                                            finish();
                                        }

                                    }
                                });
                                //current_user_image.child("profimage").setValue("default");





                            } else {

                                mProgressSignUp.setVisibility(View.GONE);

                                String errorMessage;

                                errorMessage = "Could not Register! Try Again.";

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

    private boolean isConPassValid(String confirm) {
        //TODO: Replace this with your own logic
        return confirm.equals(mEdtPasswordR.getText().toString().trim());}

}

