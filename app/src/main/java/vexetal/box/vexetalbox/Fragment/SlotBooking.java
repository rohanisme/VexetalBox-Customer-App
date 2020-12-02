package vexetal.box.vexetalbox.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Constants;
import vexetal.box.vexetalbox.Configuration.MyNotificationManager;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Slots.SlotsAdapter;
import vexetal.box.vexetalbox.R;


public class SlotBooking extends Fragment {


    private Button proceed;
    private Session session;


    private LinearLayout l1,l2,l3,l4,l5,l6,l7,bottom;
    private TextView t1,t2,t3,t4,t5,t6,t7;


    private RecyclerView mRecyclerView;
    private DatabaseReference mref;

    private ArrayList<String> days=new ArrayList<String>();
    private ArrayList<String> datedb=new ArrayList<String>();
    private ArrayList<String> slots=new ArrayList<String>();

    private ArrayList<String> cartpushid=new ArrayList<String>();
    private ArrayList<String> quantity=new ArrayList<String>();

    public SlotBooking() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_slot_booking, container, false);

        ImageView back=v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    getActivity().onBackPressed();
                }
            }
        });

        l1=v.findViewById(R.id.l1);
        l2=v.findViewById(R.id.l2);
        l3=v.findViewById(R.id.l3);
        l4=v.findViewById(R.id.l4);
        l5=v.findViewById(R.id.l5);
        l6=v.findViewById(R.id.l6);
        l7=v.findViewById(R.id.l7);

        t1=v.findViewById(R.id.t1);
        t2=v.findViewById(R.id.t2);
        t3=v.findViewById(R.id.t3);
        t4=v.findViewById(R.id.t4);
        t5=v.findViewById(R.id.t5);
        t6=v.findViewById(R.id.t6);
        t7=v.findViewById(R.id.t7);
        bottom=v.findViewById(R.id.bottom);

        mRecyclerView=v.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        session=new Session(getActivity());
        session.setdeliveryslot("");
        slots.clear();

        cartpushid.clear();
        quantity.clear();

        proceed=v.findViewById(R.id.proceed);

        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        days.add("Sat");
        days.add("Sun");

        t1.setTextColor(Color.parseColor("#FF953B"));

        LocalDate startDate = LocalDate.now() ;
        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("dd MMM");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("EEE");
        DateTimeFormatter fmt3 = DateTimeFormat.forPattern("yyyy-MM-dd");
        t1.setText("Today\n"+startDate.toString(fmt1));
        datedb.add(startDate.toString(fmt3));

        startDate = startDate.plusDays(1);
        t2.setText("Tomorrow\n"+startDate.toString(fmt1));
        datedb.add(startDate.toString(fmt3));

        startDate = startDate.plusDays(1);
        t3.setText(startDate.toString(fmt2)+"\n"+startDate.toString(fmt1));
        datedb.add(startDate.toString(fmt3));

        startDate = startDate.plusDays(1);
        t4.setText(startDate.toString(fmt2)+"\n"+startDate.toString(fmt1));
        datedb.add(startDate.toString(fmt3));

        startDate = startDate.plusDays(1);
        t5.setText(startDate.toString(fmt2)+"\n"+startDate.toString(fmt1));
        datedb.add(startDate.toString(fmt3));

        startDate = startDate.plusDays(1);
        t6.setText(startDate.toString(fmt2)+"\n"+startDate.toString(fmt1));
        datedb.add(startDate.toString(fmt3));

        startDate = startDate.plusDays(1);
        t7.setText(startDate.toString(fmt2)+"\n"+startDate.toString(fmt1));
        datedb.add(startDate.toString(fmt3));


        FirebaseDatabase.getInstance().getReference().child("Masters").child("Slot")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot v:dataSnapshot.getChildren()){
                                slots.add(v.child("Name").getValue().toString());
                            }

                            SlotsAdapter slotsAdapter = new SlotsAdapter(slots);
                            mRecyclerView.setAdapter(slotsAdapter);

                            session.setdeliverydate(datedb.get(1));
                            session.setdeliveryslot(slots.get(0));
                            session.setselection("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setdeliverydate(datedb.get(0));
                session.setselection("0");
                t1.setTextColor(Color.parseColor("#FF953B"));
                t2.setTextColor(Color.parseColor("#000000"));
                t3.setTextColor(Color.parseColor("#000000"));
                t4.setTextColor(Color.parseColor("#000000"));
                t5.setTextColor(Color.parseColor("#000000"));
                t6.setTextColor(Color.parseColor("#000000"));
                t7.setTextColor(Color.parseColor("#000000"));
            }
        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setselection("0");
                session.setdeliverydate(datedb.get(1));
                t2.setTextColor(Color.parseColor("#FF953B"));
                t1.setTextColor(Color.parseColor("#000000"));
                t3.setTextColor(Color.parseColor("#000000"));
                t4.setTextColor(Color.parseColor("#000000"));
                t5.setTextColor(Color.parseColor("#000000"));
                t6.setTextColor(Color.parseColor("#000000"));
                t7.setTextColor(Color.parseColor("#000000"));
            }
        });

        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setselection("0");
                session.setdeliverydate(datedb.get(2));
                t3.setTextColor(Color.parseColor("#FF953B"));
                t2.setTextColor(Color.parseColor("#000000"));
                t1.setTextColor(Color.parseColor("#000000"));
                t4.setTextColor(Color.parseColor("#000000"));
                t5.setTextColor(Color.parseColor("#000000"));
                t6.setTextColor(Color.parseColor("#000000"));
                t7.setTextColor(Color.parseColor("#000000"));
            }
        });

        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setselection("0");
                session.setdeliverydate(datedb.get(3));
                t4.setTextColor(Color.parseColor("#FF953B"));
                t2.setTextColor(Color.parseColor("#000000"));
                t3.setTextColor(Color.parseColor("#000000"));
                t1.setTextColor(Color.parseColor("#000000"));
                t5.setTextColor(Color.parseColor("#000000"));
                t6.setTextColor(Color.parseColor("#000000"));
                t7.setTextColor(Color.parseColor("#000000"));
            }
        });

        l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setselection("0");
                session.setdeliverydate(datedb.get(4));
                t5.setTextColor(Color.parseColor("#FF953B"));
                t2.setTextColor(Color.parseColor("#000000"));
                t3.setTextColor(Color.parseColor("#000000"));
                t4.setTextColor(Color.parseColor("#000000"));
                t1.setTextColor(Color.parseColor("#000000"));
                t6.setTextColor(Color.parseColor("#000000"));
                t7.setTextColor(Color.parseColor("#000000"));
            }
        });

        l6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setselection("0");
                session.setdeliverydate(datedb.get(5));
                t6.setTextColor(Color.parseColor("#FF953B"));
                t2.setTextColor(Color.parseColor("#000000"));
                t3.setTextColor(Color.parseColor("#000000"));
                t4.setTextColor(Color.parseColor("#000000"));
                t5.setTextColor(Color.parseColor("#000000"));
                t1.setTextColor(Color.parseColor("#000000"));
                t7.setTextColor(Color.parseColor("#000000"));
            }
        });

        l7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setselection("0");
                session.setdeliverydate(datedb.get(6));
                t7.setTextColor(Color.parseColor("#FF953B"));
                t2.setTextColor(Color.parseColor("#000000"));
                t3.setTextColor(Color.parseColor("#000000"));
                t4.setTextColor(Color.parseColor("#000000"));
                t5.setTextColor(Color.parseColor("#000000"));
                t6.setTextColor(Color.parseColor("#000000"));
                t1.setTextColor(Color.parseColor("#000000"));
            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(session.getdeliverydate())) {
                    Toast.makeText(getActivity(), "Select Delivery Date", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(session.getdeliveryslot())) {
                    Toast.makeText(getActivity(), "Select Delivery Slots", Toast.LENGTH_LONG).show();
                    return;
                }

                if(getActivity()!=null){
                    Fragment fragment = new OrderConfirmation();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }

            }
        });


        return v;
    }
}
