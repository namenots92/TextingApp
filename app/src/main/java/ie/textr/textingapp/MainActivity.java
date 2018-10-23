package ie.textr.textingapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        ListView listView = (ListView) findViewById(R.id.customListView);
        ArrayList<Contacts> arrayList = new ArrayList<>();

        arrayList.add(new Contacts(1,"Sinead","0872623792"));
        arrayList.add(new Contacts(2,"Denise","0868208648"));
        arrayList.add(new Contacts(3,"Bill","0872548694"));
        arrayList.add(new Contacts(4,"Alice","087269284"));
        arrayList.add(new Contacts(5,"James","0876482054"));
        arrayList.add(new Contacts(6,"Rachel","0852047034"));
        arrayList.add(new Contacts(7,"Hannah","0892955093"));

        // custom adapter, call adapter

        CustomAdapter adapter = new CustomAdapter(this, arrayList);

        //set adapter into listview
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    Intent myintent = new Intent(view.getContext(), SmsActivity.class);
                    startActivityForResult(myintent, 0);
                }

                if (position == 1) {
                    Intent myintent = new Intent(view.getContext(), SmsActivity.class);
                    startActivityForResult(myintent, 0);
                }
                if (position == 2) {
                    Intent myintent = new Intent(view.getContext(), SmsActivity.class);
                    startActivityForResult(myintent, 1);

                }
                if (position == 3) {
                    Intent myintent = new Intent(view.getContext(), SmsActivity.class);
                    startActivityForResult(myintent, 0);
                }

                if (position == 4) {
                    Intent myintent = new Intent(view.getContext(), SmsActivity.class);
                    startActivityForResult(myintent, 0);
                }
                if (position == 5) {
                    Intent myintent = new Intent(view.getContext(), SmsActivity.class);
                    startActivityForResult(myintent, 1);

                }
                if (position == 6) {
                    Intent myintent = new Intent(view.getContext(), SmsActivity.class);
                    startActivityForResult(myintent, 1);

                }
            }
        });

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
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menuSignout){

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if(item.getItemId() == R.id.settings){
            Intent setIntent = new Intent(MainActivity.this , SettingsActivity.class);
            startActivity(setIntent);
        }
        return true;
    }
}
