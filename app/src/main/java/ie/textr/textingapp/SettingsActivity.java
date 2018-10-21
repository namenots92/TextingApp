package ie.textr.textingapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference userDb;
    private FirebaseUser currentU;

    private ImageView imgView;
    private TextView nameS;
    private TextView statusS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgView = (ImageView) findViewById(R.id.profPic);
        nameS = (TextView) findViewById(R.id.users_name);
        statusS = (TextView) findViewById(R.id.setStat);

        currentU = FirebaseAuth.getInstance().getCurrentUser();

        String currentUid = currentU.getUid();

        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                //Toast.makeText(SettingsActivity.this, dataSnapshot.toString(), Toast.LENGTH_LONG).show();
                // data binding of the user data model
                nameS.setText(name);
                statusS.setText(status);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }
}
