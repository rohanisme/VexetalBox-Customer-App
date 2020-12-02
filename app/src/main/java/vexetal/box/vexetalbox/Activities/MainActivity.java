package vexetal.box.vexetalbox.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Fragment.Cart;
import vexetal.box.vexetalbox.Fragment.Home;
import vexetal.box.vexetalbox.Fragment.LocationSearch;
import vexetal.box.vexetalbox.Fragment.ProductDetails;
import vexetal.box.vexetalbox.Fragment.Profile;
import vexetal.box.vexetalbox.R;

public class MainActivity extends AppCompatActivity {

    LinearLayout b1,b2,b3,b4;
    private ImageView i1,i2,i4,i3;
    Session session;
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);

        i1=findViewById(R.id.i1);
        i2=findViewById(R.id.i2);
        i4=findViewById(R.id.i4);
        i3=findViewById(R.id.i3);

        session = new Session(MainActivity.this);



        session.setisfirsttime("no");


        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        final double version = info.versionCode;

        final SweetAlertDialog sDialog = new SweetAlertDialog(this, SweetAlertDialog.BUTTON_CONFIRM);
        sDialog.setTitleText("App Update!");
        sDialog.setContentText("Please update the app for a faster and better experience!");
        sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {

                }
            }
        });
        sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sDialog.dismiss();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("AppContent").child("Application").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    double vno = Double.parseDouble(dataSnapshot.child("Version").getValue().toString());
                    String imp = dataSnapshot.child("IMP").getValue().toString();
                    if (version != vno) {
                        if (imp.equals("No")) {
                            sDialog.show();
                        } else {
                            sDialog.show();
                            sDialog.setCancelable(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
//
//

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        if (!TextUtils.isEmpty(session.getusername())) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            session.settoken(token);
                            FirebaseMessaging.getInstance().subscribeToTopic("Users");
                            FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("UMessagingToken").setValue(token);
                        }
                    });
        }

            FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart").setValue(null);
            session.setcartitem("0");
            session.setcarttotal("0");
            session.setcartrtotal("0");
            session.setcartname("");
            ArrayList<String> iname = new ArrayList<String>();
            iname.clear();
            ArrayList<String> iprice = new ArrayList<String>();
            iprice.clear();
            session.setitemname(iname, "INAME");
            session.setitemprice(iprice, "IPRICE");

//        final ArrayList<String> citynames=new ArrayList<String>();
//
//        FirebaseDatabase.getInstance().getReference().child("Masters").child("PinCode")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            for(DataSnapshot v:dataSnapshot.getChildren()){
//                                citynames.add(v.child("Name").getValue().toString());
//                            }
//                            session.setcitylist(citynames,"PINCODE");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });



//        if(!TextUtils.isEmpty(session.getdaname())) {
            Fragment fragment = new ProductDetails();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
            i1.setImageResource(R.drawable.hb1);
            i3.setImageResource(R.drawable.b3);
            i4.setImageResource(R.drawable.b4);

//        }
//        else{
//            Fragment fragment = new LocationSearch();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .addToBackStack(null)
//                    .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
//        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i1.setImageResource(R.drawable.hb1);
                i3.setImageResource(R.drawable.b3);
                i4.setImageResource(R.drawable.b4);

                Fragment fragment = new ProductDetails();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i1.setImageResource(R.drawable.b1);
                i3.setImageResource(R.drawable.hb3);
                i4.setImageResource(R.drawable.b4);

                Fragment fragment = new Cart();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();

            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i1.setImageResource(R.drawable.b1);
                i3.setImageResource(R.drawable.b3);
                i4.setImageResource(R.drawable.hb4);

                Fragment fragment = new Profile();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Referral")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            session.setreferral(dataSnapshot.getValue().toString());
                        }
                        else{
                            session.setreferral("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
