package vexetal.box.vexetalbox.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;

public class Splash extends AppCompatActivity {

    private static final String TAG ="LOGIN DATA" ;
    protected boolean _active = true;
    protected int _splashTime = 500;
    private Session session;
    private HashMap<String, Object> firebaseDefaultMap;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);


        session = new Session(Splash.this);

        database = FirebaseDatabase.getInstance().getReference();
        final Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (Exception e) {

                } finally {
                    if(TextUtils.isEmpty(session.getisfirsttime()))
                    {

                        startActivity(new Intent(Splash.this,
                                Permission.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    }
                    else {
                        if (session.getusername() != "") {
                            startActivity(new Intent(Splash.this,
                                    MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();

                        } else {
                            startActivity(new Intent(Splash.this,
                                    Login.class));
                            finish();
                        }
                    }

                }
            }


        };
        splashTread.start();

    }
}

