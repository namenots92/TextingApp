package ie.textr.textingapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    //private TextView displayID;

    private ImageView profileImage;
    private TextView profileName, profileStatus, profileFriends;
    private Button profileButton, declineButton;

    private DatabaseReference friendReqDatabase;

    private DatabaseReference dbRef;
    private DatabaseReference friendDatabase;
    private DatabaseReference notificationDatabase;
    private DatabaseReference rootRef;

    private FirebaseUser current_user;

    private String current_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        rootRef = FirebaseDatabase.getInstance().getReference();

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_displayName);
        profileStatus = (TextView) findViewById(R.id.profile_status);
        profileFriends = (TextView) findViewById(R.id.profile_totalFriends);
        profileButton = (Button) findViewById(R.id.profile_send_req);
        declineButton = (Button) findViewById(R.id.profile_decline_btn);

        current_state = "not_friends";

        declineButton.setVisibility(View.INVISIBLE);
        declineButton.setEnabled(true);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                profileName.setText(display_name);
                profileStatus.setText(status);

                if(current_user.getUid().equals(user_id)){

                    declineButton.setEnabled(false);
                    declineButton.setVisibility(View.INVISIBLE);

                    profileButton.setEnabled(false);
                    profileButton.setVisibility(View.INVISIBLE);

                }

                // Friend List
                friendReqDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received"))
                            {
                                current_state = "req_received";
                                profileButton.setText("Accept friend Request");
                                declineButton.setVisibility(View.VISIBLE);
                                declineButton.setEnabled(true);

                            } else if(req_type.equals("sent")){
                                current_state = "req_sent";
                                profileButton.setText("Cancel friend Request");

                                declineButton.setVisibility(View.INVISIBLE);
                                declineButton.setEnabled(false);
                            }
                        } else {
                            friendDatabase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        current_state = "friends";
                                        profileButton.setText("Unfriend");

                                        declineButton.setVisibility(View.INVISIBLE);
                                        declineButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileButton.setEnabled(false);

                // -- Not friends state --

                if(current_state.equals("not_friends")) { // send request by running two queries

                    DatabaseReference newNotificationRef = rootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationRef.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", current_user.getUid()); // send notification from multiple functions
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + current_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + current_user.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Toast.makeText(ProfileActivity.this, "Error in sending request", Toast.LENGTH_SHORT).show();

                            } else {

                                current_state = "req_sent";
                                profileButton.setText("Cancel Friend Request");
                            }

                            profileButton.setEnabled(true);
                        }
                    });
                }
                    /*
                    friendReqDatabase.child(current_user.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                friendReqDatabase.child(user_id).child(current_user.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String,String> notificationData = new HashMap<>();
                                        notificationData.put("from", current_user.getUid()); // send notification from multiple functions
                                        notificationData.put("type", "request");

                                        notificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                current_state = "req_sent";
                                                profileButton.setText("Cancel friend Request");

                                                declineButton.setVisibility(View.INVISIBLE);
                                                declineButton.setEnabled(false);
                                            }
                                        });


                                        //Toast.makeText(ProfileActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                        */



                // -- Cancel Req friends state --
                if(current_state.equals("req_sent")){
                    friendReqDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendReqDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    profileButton.setEnabled(true);
                                    current_state = "not_friends";
                                    profileButton.setText("Send friend Request");

                                    declineButton.setVisibility(View.INVISIBLE);
                                    declineButton.setEnabled(false);
                                }
                            });
                        }
                    });
                }
                // Req received
                if(current_state.equals("req_received")) {

                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + current_user.getUid() + "/" + user_id + "/date", current_date);
                    friendsMap.put("Friends/" + user_id + "/" + current_user.getUid() + "/date", current_date);

                    friendsMap.put("Friends_req/" + current_user.getUid() + "/" + user_id, null);
                    friendsMap.put("Friends_req/" + user_id + "/" + current_user.getUid(), null);

                    rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                profileButton.setEnabled(true);
                                current_state = "friends";
                                profileButton.setText("Unfriend");

                                declineButton.setVisibility(View.INVISIBLE);
                                declineButton.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                    /*
                    friendDatabase.child(current_user.getUid()).child(user_id).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(user_id).child(current_user.getUid()).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendReqDatabase.child(current_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendReqDatabase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    profileButton.setEnabled(true);
                                                    current_state = "friends";
                                                    profileButton.setText("Unfriend");

                                                    declineButton.setVisibility(View.INVISIBLE);
                                                    declineButton.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });  */


                    if(current_state.equals("friends")){

                        Map unfriendMap = new HashMap();

                        unfriendMap.put("Friends/" + current_user.getUid() + "/" + user_id, null);
                        unfriendMap.put("Friends/" + user_id +  "/" + current_user.getUid() , null);

                        rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                if (databaseError == null) {

                                   current_state = "not_friends";
                                   profileButton.setText("Send friend request");

                                   declineButton.setVisibility(View.INVISIBLE);
                                   declineButton.setEnabled(false);
                                } else {

                                    String error = databaseError.getMessage();
                                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                }

                                profileButton.setEnabled(true);

                            }
                        });
                    }
                }
        });

    }

}
/// '/' added to create a new object within that child