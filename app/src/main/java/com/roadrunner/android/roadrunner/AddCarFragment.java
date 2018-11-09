package com.roadrunner.android.roadrunner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddCarFragment extends Fragment {

    private EditText mEdtCarName, mEdtMan, mEdtModel, mEdtYear, mEdtNoPlate, mEdtFuel, mEdtCapacity;
    private Button mBtnAddCar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;
    private ProgressBar mProgressAdd;
    private View mView;


    public AddCarFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_add_car, container, false);

        mProgressAdd = (ProgressBar) mView.findViewById(R.id.progressBarAdd);


        mEdtCarName = (EditText) mView.findViewById(R.id.etName);
        mEdtMan = (EditText) mView.findViewById(R.id.edtMan);
        mEdtModel = (EditText) mView.findViewById(R.id.edtModel);
        mEdtYear = (EditText) mView.findViewById(R.id.edtYear);
        mEdtNoPlate = (EditText) mView.findViewById(R.id.edtNoPlate);
        mEdtFuel = (EditText) mView.findViewById(R.id.edtFuel);
        mEdtCapacity = (EditText) mView.findViewById(R.id.edtCapacity);
        mBtnAddCar = (Button) mView.findViewById(R.id.btn_addcar);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("vehicle").push();
        mDatabaseUser.keepSynced(true);

        mBtnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addCar();
            }
        });

        return mView;


    }

    private void addCar() {

        mProgressAdd.setVisibility(View.VISIBLE);

        mEdtCarName.setError(null);
        mEdtMan.setError(null);
        mEdtModel.setError(null);
        mEdtYear.setError(null);
        mEdtNoPlate.setError(null);
        mEdtFuel.setError(null);
        mEdtCapacity.setError(null);

        final String carname = mEdtCarName.getText().toString().trim();
        final String man = mEdtMan.getText().toString().trim();
        final String model = mEdtModel.getText().toString().trim();
        final String year = mEdtYear.getText().toString().trim();
        final String plate = mEdtNoPlate.getText().toString().trim();
        final String fuel = mEdtFuel.getText().toString().trim();
        final String capacity = mEdtCapacity.getText().toString().trim();



        if (TextUtils.isEmpty(carname) || TextUtils.isEmpty(man) || TextUtils.isEmpty(model) ||
        TextUtils.isEmpty(year) || TextUtils.isEmpty(plate) || TextUtils.isEmpty(fuel) || TextUtils.isEmpty(capacity)) {

            mProgressAdd.setVisibility(View.INVISIBLE);

            Toast.makeText(getContext(), "Fill in Details", Toast.LENGTH_SHORT).show();

            // Check for a valid password, if the user entered one.
        }

        else  {

            mProgressAdd.setVisibility(View.VISIBLE);

            mDatabaseUser.child("carname").setValue(carname);
            mDatabaseUser.child("manufacturer").setValue(man);
            mDatabaseUser.child("model").setValue(model);
            mDatabaseUser.child("year").setValue(year);
            mDatabaseUser.child("noplate").setValue(plate);
            mDatabaseUser.child("fuel").setValue(fuel);
            mDatabaseUser.child("capacity").setValue(capacity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isComplete()) {
                        mProgressAdd.setVisibility(View.INVISIBLE);

                        Toast.makeText(getContext(), "Vehicle Info Updated", Toast.LENGTH_SHORT).show();


                    }else {

                        mProgressAdd.setVisibility(View.VISIBLE);

                    }
                }
            });


        }
    }



}
