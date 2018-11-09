package com.roadrunner.android.roadrunner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GarageFragment extends Fragment {

    private EditText mEdtOd, mEdtService, mEdtCost, mEdtLocation, mEdtReason;
    private Button mBtnSave;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;
    private ProgressBar mProgressAdd;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_garage, container, false);

        mProgressAdd = (ProgressBar) mView.findViewById(R.id.progressBarSaveS);


        mEdtOd = (EditText) mView.findViewById(R.id.edtOdS);
        mEdtService = (EditText) mView.findViewById(R.id.edtServiceType);
        mEdtCost = (EditText) mView.findViewById(R.id.edtCostS);
        mEdtLocation = (EditText) mView.findViewById(R.id.edtLocationS);
        mEdtReason = (EditText) mView.findViewById(R.id.edtReasonS);

        mBtnSave = (Button) mView.findViewById(R.id.btn_saveS);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("service").push();
        mDatabaseUser.keepSynced(true);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                save();
            }
        });


        return mView;
    }

    private void save() {

        mProgressAdd.setVisibility(View.VISIBLE);


        final String carname = mEdtOd.getText().toString().trim();
        final String man = mEdtService.getText().toString().trim();
        final String model = mEdtCost.getText().toString().trim();
        final String year = mEdtLocation.getText().toString().trim();
        final String plate = mEdtReason.getText().toString().trim();


        if (TextUtils.isEmpty(carname) || TextUtils.isEmpty(man) || TextUtils.isEmpty(model) ||
                TextUtils.isEmpty(year) || TextUtils.isEmpty(plate)) {

            mProgressAdd.setVisibility(View.INVISIBLE);

            Toast.makeText(getContext(), "Fill in Details", Toast.LENGTH_SHORT).show();

            // Check for a valid password, if the user entered one.
        }

        else  {

            mProgressAdd.setVisibility(View.VISIBLE);

            mDatabaseUser.child("odometer").setValue(carname);
            mDatabaseUser.child("service").setValue(man);
            mDatabaseUser.child("cost").setValue(model);
            mDatabaseUser.child("location").setValue(year);
            mDatabaseUser.child("reason").setValue(plate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isComplete()) {
                                mProgressAdd.setVisibility(View.INVISIBLE);

                                Toast.makeText(getContext(), "Service Info Updated", Toast.LENGTH_SHORT).show();


                            }else {

                                mProgressAdd.setVisibility(View.VISIBLE);

                            }
                        }
                    });


        }
    }
}
