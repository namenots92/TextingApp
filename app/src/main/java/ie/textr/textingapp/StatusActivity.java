package ie.textr.textingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private TextInputLayout statusC;
    private Button btnChange;

    // look within the database
    private DatabaseReference dbRef;
    private FirebaseUser fbUser;

    //progress dialog
    private ProgressDialog statProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        statusC = (TextInputLayout) findViewById(R.id.stat_input);
        btnChange = (Button) findViewById(R.id.stat_change_btn);

        String statValue = getIntent().getStringExtra("statValue");

        statusC.getEditText().setText(statValue);
        //Firebase
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = fbUser.getUid();

        statProgress = new ProgressDialog(this);


        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statProgress = new ProgressDialog(StatusActivity.this);
                statProgress.setTitle("Saving Changes");
                statProgress.setMessage("Please wait while we save your changes");
                statProgress.show();

                final String status = statusC.getEditText().getText().toString();


                dbRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            statProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Your status has been changed.", Toast.LENGTH_LONG).show();
                        }else{
                            statProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry, unable to change status.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });
    }
}
