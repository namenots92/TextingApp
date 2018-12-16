package ie.textr.textingapp;

public class ChatActivity{}
/*
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ie.textr.textingapp.GetTimeAgo.getTimeAgo;

public class ChatActivity extends AppCompatActivity {

    private String chatUser;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private FirebaseAuth auth;

    private Toolbar chatToolBar;
    private TextView titleView;
    private TextView lastSeen;
    private ImageView imageBar;

    private String userId;

    private ImageButton chatAddBtn;
    private ImageButton chatSendBtn;

    private EditText chatMessageView;

    private RecyclerView rMessageList;
    private SwipeRefreshLayout swipeLayout;

    private LinearLayoutManager linearLayout;


    private final static int TOTAL_ITEMS_TO_LOAD = 10;
    private int currentPage = 1;

    private static final int GALLERY_PICK = 1;

    private StorageReference imageStorage;

    private int itemPosition = 0;

    private String lastKey = "";
    private String previousKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();


        chatUser = getIntent().getStringExtra("user_id");
        rootRef = FirebaseDatabase.getInstance().getReference();

        userId = auth.getCurrentUser().getUid();

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String userName = getIntent().getStringExtra("user_name");

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());


        //--  Action bar
        titleView = (TextView) findViewById(R.id.c_bar_title);
        lastSeen = (TextView) findViewById(R.id.c_bar_seen);
        imageBar = (ImageView) findViewById(R.id.c_bar_img);

        chatAddBtn = (ImageButton) findViewById(R.id.chat_addBtn);
        chatSendBtn = (ImageButton) findViewById(R.id.chat_sendBtn);
        chatMessageView = (EditText) findViewById(R.id.chat_messageView);

        //rMessageList = (RecyclerView) findViewById(R.id.messages_list);

        titleView.setText(userName);

        //----------------

        rootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("Users").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                if (online.equals("true")) {
                    lastSeen.setText("Online");
                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);
                    String mlastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    lastSeen.setText(mlastSeenTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rootRef.child("Chat").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(chatUser)) {
                    Map chatMap = new HashMap();
                    chatMap.put("seen", false);
                    chatMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + userId + "/" + chatUser, chatMap);
                    chatUserMap.put("Chat/" + chatUser + "/" + userId, chatMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String message = chatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "messages/" + userId + "/" + chatUser;
            String chat_user_ref = "messages/" + chatUser + "/" + userId;

            DatabaseReference user_message_push = rootRef.child("messages")
                    .child(userId).child(chatUser).push();

            String pushId = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", userId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + pushId, messageMap);
            messageUserMap.put(chat_user_ref + "/" + pushId, messageMap);


            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null)
        {
            sendToStart();
        } else {
            userRef.child("online").setValue(true);
        }
    }

    private void sendToStart(){
        Intent startIntent = new Intent(ChatActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


}





 */