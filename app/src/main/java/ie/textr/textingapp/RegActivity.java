package ie.textr.textingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegActivity extends AppCompatActivity {

    private TextInputLayout mName, mEmail, mPassword;
    private Button mButton;

    private FirebaseAuth mAuth;
    private DatabaseReference fbDatabase;

    private DatabaseReference db;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mName = (TextInputLayout) findViewById(R.id.reg_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mButton = (Button) findViewById(R.id.reg_btn);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mName.getEditText().getText().toString(); // get the text and change to string
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we set up your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    registerUser(name, email, password);

                }
            }
        });

    }



    private void registerUser(final String nameReg, final String emailReg, final String passwordReg) {
        // ensure all fields are entered
        mAuth.createUserWithEmailAndPassword(emailReg, passwordReg).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                mRegProgress.dismiss();
                if(task.isSuccessful()){

                    // get the current user thats logged in
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();
                    // add child instances from the database
                    fbDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    // hash map is a data type to retain data, can be in a tree form.
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", nameReg);
                    userMap.put("status", "Be back soon");
                    userMap.put("image", "textr");
                    userMap.put("thumb_image", "default");

                    fbDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                Intent mainIntent = new Intent(RegActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                Toast.makeText(RegActivity.this, "Welcome, your registraiton was sucessful.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });

                }else{
                    mRegProgress.hide();
                    Toast.makeText(RegActivity.this, "That email address is already registered please try again.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
