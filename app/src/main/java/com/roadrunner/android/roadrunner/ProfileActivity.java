package com.roadrunner.android.roadrunner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProfileActivity extends AppCompatActivity {

    private EditText medtUsername;
    private static final int GALLERY_REQUEST = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorage;
    private CircleImageView mCprof;
    private Uri mImageUri;
    private ProgressBar mProgressProf;
    private Button mBtnSignOut, mBtnUsernameVal, mBtnPhoneVal;
    private static final int REQUEST_READ_EXTERNAL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabaseUser.keepSynced(true);

        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value= mCurrentUser.getEmail();
        String valueP= mCurrentUser.getPhoneNumber();
        final String value2 = mCurrentUser.getUid();

        mProgressProf = (ProgressBar) findViewById(R.id.progressBarProf);


        mCprof = (CircleImageView) findViewById(R.id.mProfpic);
        mCprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup();
            }
        });

        mBtnUsernameVal = (Button) findViewById(R.id.btnUsernameValP);
        mBtnPhoneVal = (Button) findViewById(R.id.btnPhoneValP);


        mBtnPhoneVal.setText(value +" ");


        mBtnSignOut = (Button) findViewById(R.id.btnSignOut);
        mBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ProfileActivity.this).setIcon(R.drawable.ic_exit_to_app)
                        .setTitle("Sign Out").setMessage("Are you sure you want to Sign Out?")
                        .setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(ProfileActivity.this, "You were Signed Out Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ProfileActivity.this, SignInActivity.class));



                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }).show();

            }

        });

        medtUsername = (EditText) findViewById(R.id.edtUsername);
        medtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                medtUsername.setCursorVisible(true);
            }
        });

        medtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here

                    changeUsername();

                    return true;
                }
                return false;
            }
        });



        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String post_image = (String) dataSnapshot.child("profimage").getValue();
                final String navusername = (String) dataSnapshot.child("username").getValue();
                final String userR = (String) dataSnapshot.child("usernameId").getValue();
                String navstatus = (String) dataSnapshot.child("status").getValue();



                if (navusername!=null) {
                    medtUsername.setText(navusername);
                }
                else {
                    mDatabaseUser.child("username").setValue("No Name");
                }

                if (userR!=null) {
                    mBtnUsernameVal.setText("@" + userR +" ");
                }
                else {
                    mDatabaseUser.child("usernameId").setValue("No Username");
                }

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeUsername() {
        final String username = medtUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)){

            String errorMessage;

            errorMessage = "Enter Your Username";

            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();

            return;

        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value2 = mCurrentUser.getUid();
        DatabaseReference mDatabaseusername = FirebaseDatabase.getInstance().getReference("Users").child(value2);
        final View view = ProfileActivity.this.getCurrentFocus();

        mDatabaseusername.child("username").setValue(username).addOnCompleteListener(new  OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {


                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    medtUsername.setCursorVisible(false);
                    Toast.makeText(ProfileActivity.this, "Username Changed", Toast.LENGTH_SHORT).show();
                }else {

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    Toast.makeText(ProfileActivity.this, "Could Not Change Your Username. Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case REQUEST_READ_EXTERNAL:{

                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    // permission was granted, yay!
                    TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(ProfileActivity.this)
                            .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                                @Override
                                public void onImageSelected(Uri uri) {
                                    // here is selected uri
                                    CropImage.activity(uri)
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .setMultiTouchEnabled(true)
                                            .setAspectRatio(2,2)
                                            .start((Activity) ProfileActivity.this);

                                    mProgressProf.setVisibility(View.VISIBLE);

                                }
                            })
                            .create();

                    tedBottomPicker.show(getSupportFragmentManager());


                }else {

                    String errorMessage;
                    errorMessage = "Permissions are required to access Gallery";
                    Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL);
                                }
                            });

                }

                return;
            }
        }
    }


    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if ((ContextCompat.checkSelfPermission(ProfileActivity.this, WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ){

                // Permission is not granted


                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE }, 1);



            }else {

                TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(ProfileActivity.this)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                // here is selected uri
                                CropImage.activity(uri)
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMultiTouchEnabled(true)
                                        .setAspectRatio(2,2)
                                        .start((Activity) ProfileActivity.this);

                                mProgressProf.setVisibility(View.VISIBLE);

                            }
                        })
                        .create();

                tedBottomPicker.show(getSupportFragmentManager());

            }



        }
    }

    private void popup(){

        String[] items = {"Edit Name","Choose Photo","Remove Photo"};
        int[] icons = {
                R.drawable.ic_edit_black,
                R.drawable.ic_insert_photo,
                R.drawable.ic_delete_black
        };

        BottomSheet.Builder builder = new BottomSheet.Builder(ProfileActivity.this);
        builder.setItems(items,icons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0){

                    medtUsername.setCursorVisible(true);

                }
                else if (which==1){

                    checkPermissions();

                }else {
                    new AlertDialog.Builder(ProfileActivity.this).setIcon(R.drawable.ic_delete_black)
                            .setTitle("Remove Photo").setMessage("Are you sure you want to Remove this photo?")
                            .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mProgressProf.setVisibility(View.VISIBLE);

                                    FirebaseUser mCurrentUser2 = mAuth.getCurrentUser();
                                    String value= mCurrentUser2.getUid();
                                    final StorageReference filepath = mStorage.child("Profile_Images").child(value + ".jpg");

                                    Boolean mRemove = mDatabaseUser.child("profimage").removeValue().isComplete();



                                    if (mRemove.equals(true)) {

                                        filepath.delete();
                                        mProgressProf.setVisibility(View.GONE);
                                        Toast.makeText(ProfileActivity.this, "Profile Photo Removed", Toast.LENGTH_SHORT).show();

                                    } else {

                                        filepath.delete();
                                        mProgressProf.setVisibility(View.GONE);
                                        Toast.makeText(ProfileActivity.this, "Profile Photo Removed", Toast.LENGTH_SHORT).show();
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
                mProgressProf.setVisibility(View.VISIBLE);

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


                FirebaseUser mCurrentUser2 = mAuth.getCurrentUser();
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

                                                    mProgressProf.setVisibility(View.GONE);

                                                    Toast.makeText(ProfileActivity.this, "Profile Photo Updated", Toast.LENGTH_SHORT).show();

                                                }else {

                                                    mProgressProf.setVisibility(View.GONE);


                                                    Toast.makeText(ProfileActivity.this, "Could Not Update Profile Photo!", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    }else {

                                        mProgressProf.setVisibility(View.GONE);
                                        Toast.makeText(ProfileActivity.this, "Could Not Update Profile Photo! Try Again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }else {

                            mProgressProf.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, "Could Not Update Profile Photo! Try Again.", Toast.LENGTH_SHORT).show();


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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_searchP) {
            popup();
            return true;
        }





        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }
}
