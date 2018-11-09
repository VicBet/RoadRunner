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

public class GasFragment extends Fragment {

    private EditText mEdtOd, mEdtFuelT, mEdtCapacityT, mEdtLitres, mEdtCost, mEdtStation, mEdtReason;
    private Button mBtnSaveF;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;
    private ProgressBar mProgressAdd;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_gas, container, false);

        mProgressAdd = (ProgressBar) mView.findViewById(R.id.progressBarSaveF);


        mEdtOd = (EditText) mView.findViewById(R.id.edtOd);
        mEdtFuelT = (EditText) mView.findViewById(R.id.edtFuelT);
        mEdtCapacityT = (EditText) mView.findViewById(R.id.edtCapacityT);
        mEdtLitres = (EditText) mView.findViewById(R.id.edtLitres);
        mEdtCost = (EditText) mView.findViewById(R.id.edtTotal);
        mEdtStation = (EditText) mView.findViewById(R.id.edtGasStation);
        mEdtReason = (EditText) mView.findViewById(R.id.edtReason);
        mBtnSaveF = (Button) mView.findViewById(R.id.btn_saveF);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("fuelExpense").push();
        mDatabaseUser.keepSynced(true);

        mBtnSaveF.setOnClickListener(new View.OnClickListener() {
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
        final String man = mEdtFuelT.getText().toString().trim();
        final String model = mEdtCapacityT.getText().toString().trim();
        final String year = mEdtLitres.getText().toString().trim();
        final String plate = mEdtCost.getText().toString().trim();
        final String fuel = mEdtStation.getText().toString().trim();
        final String capacity = mEdtReason.getText().toString().trim();



        if (TextUtils.isEmpty(carname) || TextUtils.isEmpty(man) || TextUtils.isEmpty(model) ||
                TextUtils.isEmpty(year) || TextUtils.isEmpty(plate) || TextUtils.isEmpty(fuel) || TextUtils.isEmpty(capacity)) {

            mProgressAdd.setVisibility(View.INVISIBLE);

            Toast.makeText(getContext(), "Fill in Details", Toast.LENGTH_SHORT).show();

            // Check for a valid password, if the user entered one.
        }

        else  {

            mProgressAdd.setVisibility(View.VISIBLE);

            mDatabaseUser.child("odometer").setValue(carname);
            mDatabaseUser.child("fuel").setValue(man);
            mDatabaseUser.child("capacity").setValue(model);
            mDatabaseUser.child("litres").setValue(year);
            mDatabaseUser.child("total").setValue(plate);
            mDatabaseUser.child("station").setValue(fuel);
            mDatabaseUser.child("reason").setValue(capacity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isComplete()) {
                                mProgressAdd.setVisibility(View.INVISIBLE);

                                Toast.makeText(getContext(), "Fuel Expense Updated", Toast.LENGTH_SHORT).show();


                            }else {

                                mProgressAdd.setVisibility(View.VISIBLE);

                            }
                        }
                    });


        }
    }
}
