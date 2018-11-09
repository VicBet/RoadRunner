package com.roadrunner.android.roadrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyVehiclesFragment extends Fragment {

    private RecyclerView mNotificationList;
    private DatabaseReference mFriendsReqDatabase,mUserDatabse, mRootRef, mOnlineRef;
    private FirebaseAuth mAuth;
    private View mView;
    private String mCurrent_user_id;
    private TextView txtNoNotifications;
    private Query mQueryNotifications;
    private FloatingActionButton mFloat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myvehicle, container, false);

        getActivity().setTitle("My Cars");

        txtNoNotifications = (TextView) mView.findViewById(R.id.txtVehicle);

        mFloat = (FloatingActionButton) mView.findViewById(R.id.floatingActionButtonMC);

        mNotificationList = (RecyclerView) mView.findViewById(R.id.blog_list_myvehicle);

        mFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction;
                transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_container, new AddCarFragment());
                    transaction.commit();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsReqDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user_id).child("vehicle");
        mFriendsReqDatabase.keepSynced(true);

        mQueryNotifications = mFriendsReqDatabase.orderByChild("request_type").equalTo("received");

        mUserDatabse  = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabse.keepSynced(true);

        mRootRef  = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mRootRef.keepSynced(true);

        mFriendsReqDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (!dataSnapshot.exists()){

                    txtNoNotifications.setVisibility(View.VISIBLE);
                }else {

                    txtNoNotifications.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(layoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Notifications, NotificationsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notifications, NotificationsViewHolder>(

                Notifications.class,
                R.layout.blog_row_mv,
                NotificationsViewHolder.class,
                mFriendsReqDatabase
        )
        {
            @Override
            protected void populateViewHolder(final NotificationsViewHolder viewHolder, Notifications model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent viewIntent = new Intent(getContext(), VehicleActivity.class);
                        viewIntent.putExtra("uid", post_key);
                        startActivity(viewIntent);
                    }
                });


                mFriendsReqDatabase.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("request_time")){

                            String notTime = dataSnapshot.child("request_time").getValue().toString();


                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            long lastTime = Long.parseLong(notTime);
                            String lastNotTime = getTimeAgo.getTimeAgo(lastTime,getContext());

                            boolean today = DateUtils.isToday(Long.parseLong(notTime));
                            String ago = String.valueOf(DateUtils.getRelativeTimeSpanString(Long.parseLong(notTime)));


                            if (today == true) {

                                if (!ago.contains("hour")){

                                    viewHolder.mTime.setText(lastNotTime);
                                }else {

                                    String simpledate = new java.text.SimpleDateFormat("hh:mm a").format(new Date(Long.parseLong(notTime)));
                                    viewHolder.mTime.setText(simpledate);
                                }



                            } else if (today == false) {

                                String lastMessageTime = getTimeAgo.getTimeAgo(lastTime, getContext());
                                //viewHolder.mTime.setText(lastMessageTime);

                                String simpledate2 = new java.text.SimpleDateFormat("dd/MM/yy").format(new Date(Long.parseLong(notTime)));


                                if (ago.contains("hour")){

                                    viewHolder.mTime.setText("Yesterday");
                                }else {

                                    viewHolder.mTime.setText(simpledate2);
                                }
                            }

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mFriendsReqDatabase.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = (String) dataSnapshot.child("carname").getValue();
                        String userProf = (String) dataSnapshot.child("profimage").getValue();
                        String model = (String) dataSnapshot.child("manufacturer").getValue();

                        viewHolder.setUsername(userName);
                        viewHolder.setManufac(model);
                        viewHolder.setProfimage();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mNotificationList.setAdapter(firebaseRecyclerAdapter);

    }



    public static class NotificationsViewHolder extends RecyclerView.ViewHolder{


        ImageView mUserProf;
        TextView mUserName, mModel;
        TextView mTime;
        View mView;


        public NotificationsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mUserProf =(ImageView) mView.findViewById(R.id.ImageViewNfs);
            mUserName = (TextView) mView.findViewById(R.id.textViewNfs);
            mModel = (TextView) mView.findViewById(R.id.textModel);
            mTime =(TextView) mView.findViewById(R.id.textViewTimeNfs);



        }

        public void setUsername(String usermName){
            if (usermName!=null) {
                TextView post_username = (TextView) mView.findViewById(R.id.textViewNfs);
                post_username.setText("  " + usermName);
            }else {
                TextView post_username = (TextView) mView.findViewById(R.id.textViewNfs);
                post_username.setText("  No Username");
            }

        }

        public void setManufac(String usermName){
            if (usermName!=null) {
                TextView post_username = (TextView) mView.findViewById(R.id.textModel);
                post_username.setText("  " + usermName);
            }else {
                TextView post_username = (TextView) mView.findViewById(R.id.textModel);
                post_username.setText("  No Username");
            }

        }

        public void setProfimage() {



                final CircleImageView friends_profimage = (CircleImageView) mView.findViewById(R.id.ImageViewNfs);


                //Picasso.with(ctx).load(image).into(post_image);
                Picasso.get().load(R.drawable.car2).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.userprof2).into(friends_profimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(R.drawable.car2).placeholder(R.drawable.userprof2).into(friends_profimage);

                    }


                });

        }


    }
}
