package ie.textr.textingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ie.textr.textingapp.R;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;

    private TextInputLayout email, password;


    private TextView userReg;

    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        mLoginProgress = new ProgressDialog(this);

        loginButton = (Button) findViewById(R.id.login_button);
        email = (TextInputLayout) findViewById(R.id.login_email);
        password = (TextInputLayout) findViewById(R.id.login_password);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String eml = email.getEditText().getText().toString();
                String pwd = password.getEditText().getText().toString();

                if(!TextUtils.isEmpty(eml) && !TextUtils.isEmpty(pwd)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(eml, pwd);
                }
            }
        });
    }



    private void loginUser(String email1, String password1){
        mAuth.signInWithEmailAndPassword(email1, password1).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override // check if user is signed in or not
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            mLoginProgress.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }  else {

                            mLoginProgress.hide();
                            Toast.makeText(LoginActivity.this, "Wrong email or password. Please Try again.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

}
