package ie.textr.textingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Random;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference userDb;
    private FirebaseUser currentU;

    private ImageView imgView;
    private TextView nameS;
    private TextView statusS;

    private Button imageButton;
    private Button statusButton;
    private Button deactivateButton;

    private FirebaseAuth mAuth;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference imgStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Creating firebase instance(Authentication)
        mAuth = FirebaseAuth.getInstance();

        imgView = (ImageView) findViewById(R.id.profPic);
        nameS = (TextView) findViewById(R.id.users_name);
        statusS = (TextView) findViewById(R.id.setStat);


        deactivateButton = (Button) findViewById(R.id.deactivate);

        imageButton = (Button) findViewById(R.id.change_img);

        statusButton = (Button) findViewById(R.id.change_status);


        currentU = FirebaseAuth.getInstance().getCurrentUser();

        String currentUid = currentU.getUid();

        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        imgStorage = FirebaseStorage.getInstance().getReference();


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

        deactivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactivate(v);
            }
        });

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statValue = statusS.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                //send Data by put Extra
                statusIntent.putExtra("statusValue", statValue);

                startActivity(statusIntent);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "This feature is coming soon.", Toast.LENGTH_LONG).show();

                /*Intent galleryIntent = new Intent();
                galleryIntent.setType("image/");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK); */
            }
        });
        if (statusButton != null)
        {
            Log.v("Status", "Really got the status button");
        }
    }

    public void deactivate(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intentDeactivate = new Intent(SettingsActivity.this, StartActivity.class);
                    finish();
                    startActivity(intentDeactivate);
                    Toast.makeText(getApplicationContext(), "You have deactivated your account :(", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Sorry we were unable to deactivate your account", Toast.LENGTH_LONG).show();
                }
                }
            });
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            sendToStart();
        }
    }

    private void sendToStart(){
        Intent startIntent = new Intent(SettingsActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.settings_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menuSignout) {

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if(item.getItemId() == R.id.home){
            Intent setIntent = new Intent(SettingsActivity.this , MainActivity.class);
            startActivity(setIntent);
        }
        return true;
    }
}

