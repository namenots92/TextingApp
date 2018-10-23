package ie.textr.textingapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SmsActivity extends AppCompatActivity {

    Button send;
    private ProgressBar progressBar;
    EditText editText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        mAuth = FirebaseAuth.getInstance();

        //textWatcher.addTextWatcherListener(editText);
        if (ContextCompat.checkSelfPermission(SmsActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SmsActivity.this,
                    Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(SmsActivity.this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(SmsActivity.this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {

        }
        send = (Button) findViewById(R.id.send);

        editText = (EditText) findViewById(R.id.editText);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sms = editText.getText().toString();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(null, null, sms, null, null);
                    Toast.makeText(SmsActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(SmsActivity.this, "Sent", Toast.LENGTH_SHORT).show(); // Will changed to failed once DB connected
                }
            }

        });
    }


    public void onRequestPermissionsesult (int requestCode, String [] permissions, int [] grantResults) {
        switch(requestCode){
            case 1: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(SmsActivity.this,
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, " No permission Granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
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
        Intent startIntent = new Intent(SmsActivity.this, StartActivity.class);
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
            Intent setIntent = new Intent(SmsActivity.this , MainActivity.class);
            startActivity(setIntent);
        }
        return true;

}

}