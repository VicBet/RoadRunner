package com.roadrunner.android.roadrunner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.michaelbel.bottomsheet.BottomSheet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;
import id.zelory.compressor.Compressor;

public class FinishSetupActivity extends AppCompatActivity {

    private EditText mEdtDisplayName, mEdtUsername;
    private Button mFinish;
    private CircleImageView mCprof;
    private ProgressBar mProgressFinish;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseUsers, mDatabaseUser, mDatabaseId;
    private static final int GALLERY_REQUEST = 1;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_setup);


        mEdtDisplayName = (EditText) findViewById(R.id.edtDisplayNameF);
        mEdtUsername = (EditText) findViewById(R.id.edtuserNameF);
        mFinish = (Button) findViewById(R.id.btnFinish);
        mProgressFinish = (ProgressBar) findViewById(R.id.progressBarFinish);
        mCprof = (CircleImageView) findViewById(R.id.mProfFS) ;

        mEdtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    userFinish();

                    return true;
                }
                return false;
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabaseId = FirebaseDatabase.getInstance().getReference().child("UsernameID");
        mDatabaseId.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();
        FirebaseUser mCurrentUser = firebaseAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mProgress = new ProgressDialog(FinishSetupActivity.this);

        mCprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup();
            }
        });

        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFinish();
            }
        });

        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String post_image = (String) dataSnapshot.child("profimage").getValue();
                final String post_name = (String) dataSnapshot.child("username").getValue();

                if (dataSnapshot.hasChild("usernameId")){

                    startActivity(new Intent(FinishSetupActivity.this, MainActivity.class));
                    finish();

                }

                mEdtDisplayName.setText(post_name);

                if (dataSnapshot.child("profimage").exists()) {

                    Picasso.get().load(post_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.userprof2).into(mCprof, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(post_image).placeholder(R.drawable.userprof2).into(mCprof);

                        }

                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .setAspectRatio(2, 2)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //mProgress.setCanceledOnTouchOutside(false);
                mProgressFinish.setVisibility(View.VISIBLE);

                mImageUri = result.getUri();

                Uri resultUri = result.getUri();


                File thumb_filePath = new File(resultUri.getPath());

                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                FirebaseUser mCurrentUser2 = firebaseAuth.getCurrentUser();
                String value= mCurrentUser2.getUid();

                final StorageReference thumb_filepath = mStorage.child("Profile_Images").child(value + ".jpg");

                thumb_filepath.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()){

                                        Map updatehashMap = new HashMap();
                                        updatehashMap.put("profimage", thumb_downloadUrl);

                                        mDatabaseUser.updateChildren(updatehashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    mProgressFinish.setVisibility(View.GONE);

                                                    Toast.makeText(FinishSetupActivity.this, "Profile Photo Updated", Toast.LENGTH_SHORT).show();

                                                }else {

                                                    mProgressFinish.setVisibility(View.GONE);


                                                    Toast.makeText(FinishSetupActivity.this, "Could Not Update Profile Photo!", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    }else {

                                        mProgressFinish.setVisibility(View.GONE);
                                        Toast.makeText(FinishSetupActivity.this, "Could Not Update Profile Photo! Try Again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }else {

                            mProgressFinish.setVisibility(View.GONE);
                            Toast.makeText(FinishSetupActivity.this, "Could Not Update Profile Photo! Try Again.", Toast.LENGTH_SHORT).show();


                        }
                    }

                });

                //----------------------------------------------






                mCprof.setImageURI(mImageUri);
                // startPosting();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }
    }


    private void popup(){

        String[] items = {"Choose Photo","Remove Photo"};
        int[] icons = {
                R.drawable.ic_insert_photo,
                R.drawable.ic_delete_black
        };

        BottomSheet.Builder builder = new BottomSheet.Builder(FinishSetupActivity.this);
        builder.setItems(items,icons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                 if (which==0){

                    TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(FinishSetupActivity.this)
                            .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                                @Override
                                public void onImageSelected(Uri uri) {
                                    // here is selected uri
                                    CropImage.activity(uri)
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .setMultiTouchEnabled(true)
                                            .setAspectRatio(2,2)
                                            .start((Activity) FinishSetupActivity.this);

                                    mProgressFinish.setVisibility(View.VISIBLE);

                                }
                            })
                            .create();

                    tedBottomPicker.show(getSupportFragmentManager());

                }else {
                    new android.app.AlertDialog.Builder(FinishSetupActivity.this).setIcon(R.drawable.ic_delete_black)
                            .setTitle("Remove Photo").setMessage("Are you sure you want to Remove this photo?")
                            .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mProgressFinish.setVisibility(View.VISIBLE);

                                    FirebaseUser mCurrentUser2 = firebaseAuth.getCurrentUser();
                                    String value= mCurrentUser2.getUid();
                                    final StorageReference filepath = mStorage.child("Profile_Images").child(value + ".jpg");

                                    Boolean mRemove = mDatabaseUser.child("profimage").removeValue().isComplete();



                                    if (mRemove.equals(true)) {

                                        filepath.delete();
                                        mProgressFinish.setVisibility(View.GONE);
                                        Toast.makeText(FinishSetupActivity.this, "Profile Photo Removed", Toast.LENGTH_SHORT).show();

                                    } else {

                                        filepath.delete();
                                        mProgressFinish.setVisibility(View.GONE);
                                        Toast.makeText(FinishSetupActivity.this, "Profile Photo Removed", Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getContext(), "Could Not Remove Feed. Try Again", Toast.LENGTH_LONG).show();
                                    }



                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    }).show();
                }

            }
        }).show();


    }

    private void userFinish() {

        mProgressFinish.setVisibility(View.VISIBLE);

        mEdtDisplayName.setError(null);
        mEdtUsername.setError(null);

        final String displayname = mEdtDisplayName.getText().toString().trim();
        final String username = mEdtUsername.getText().toString().trim();

        Pattern ps = Pattern.compile("^[a-z A-Z ]+$");
        Matcher ms = ps.matcher(username);

        boolean bs = ms.matches();

        boolean hasLowercase = !username.equals(username.toUpperCase());




        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(displayname) && TextUtils.isEmpty(username)) {

            mProgressFinish.setVisibility(View.INVISIBLE);
            String errorMessage;

            errorMessage = "Enter Your Display Name and Username";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
            return;

            // Check for a valid password, if the user entered one.
        }
        else if (!TextUtils.isEmpty(displayname) && !isDisplayNameValid(displayname)) {
            mProgressFinish.setVisibility(View.INVISIBLE);
            mEdtDisplayName.setError("Invalid Display Name");
            focusView = mEdtDisplayName;
            cancel = true;


        }
        else if (TextUtils.isEmpty(username)){
            mProgressFinish.setVisibility(View.INVISIBLE);

            String errorMessage;

            errorMessage = "Enter Your Username";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;

        }
        else if (TextUtils.isEmpty(displayname)){
            mProgressFinish.setVisibility(View.INVISIBLE);

            String errorMessage;

            errorMessage = "Enter Your Display Name";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;

        }
        else if (!TextUtils.isEmpty(displayname) && !isUsernameValidLenght(username)) {
            mProgressFinish.setVisibility(View.INVISIBLE);

            mEdtUsername.setError("This username is too short");
            focusView = mEdtUsername;
            cancel = true;


        }
        else if (!TextUtils.isEmpty(username) && !bs){

            mProgressFinish.setVisibility(View.INVISIBLE);

            mEdtUsername.setError("Username should not contain numeric characters");
            focusView = mEdtUsername;
            cancel = true;

        }

        else if (!TextUtils.isEmpty(username) && !hasLowercase){

            mProgressFinish.setVisibility(View.INVISIBLE);

            mEdtUsername.setError("Username must be in small caps");
            focusView = mEdtUsername;
            cancel = true;

        }
        else if (!TextUtils.isEmpty(username) && isUsernameHasSpace(username)){

            mProgressFinish.setVisibility(View.INVISIBLE);

            mEdtUsername.setError("Username should not contain a space");
            focusView = mEdtUsername;
            cancel = true;

        }
        else if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();


        }

        else if (!TextUtils.isEmpty(displayname) && !TextUtils.isEmpty(username)
                && isUsernameValidLenght(username) && !isUsernameHasSpace(username)
                && isDisplayNameValid(displayname)) {

            mProgressFinish.setVisibility(View.VISIBLE);

            mDatabaseId.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(username)){

                        String uid = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabaseUsers.child(uid);
                        current_user_db.child("username").setValue(displayname);
                        mDatabaseId.child(username).setValue(uid);
                        current_user_db.child("usernameId").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mProgressFinish.setVisibility(View.INVISIBLE);
                                    Intent mainIntent = new Intent(FinishSetupActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }else {
                                    mProgressFinish.setVisibility(View.INVISIBLE);
                                    String errorMessage;

                                    errorMessage = "Could not Finish Setup! Try Again.";

                                    Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }else {

                        mProgressFinish.setVisibility(View.INVISIBLE);

                        boolean cancel = false;
                        View focusView = null;

                        mEdtUsername.setError("Username is already taken");
                        focusView = mEdtUsername;
                        cancel = true;




                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    private boolean isDisplayNameValid(String displayname) {
        //TODO: Replace this with your own logic
        return displayname.length() > 3;}

    private boolean isUsernameValidLenght(String username) {
        //TODO: Replace this with your own logic

        return username.length() > 3;}

    private boolean isUsernameHasSpace(String username) {
        //TODO: Replace this with your own logic

        return username.contains(" ");}



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
