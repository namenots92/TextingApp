package ie.textr.textingapp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class Textr extends Application {

    private DatabaseReference userDatabase;
    private FirebaseAuth auth;

    @Override
    public void onCreate(){
        super.onCreate();
        // enable offline capabilities
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {

            userDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(auth.getCurrentUser().getUid());


            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null) {
                        userDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }
}
