package vexetal.box.vexetalbox.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.willy.ratingbar.RotationRatingBar;

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
import vexetal.box.vexetalbox.Models.Cart.Cart1;
import vexetal.box.vexetalbox.Models.Cart.CartAdapter2;
import vexetal.box.vexetalbox.Models.OrderDetails.OrderDetails;
import vexetal.box.vexetalbox.Models.OrderDetails.ViewHolder;
import vexetal.box.vexetalbox.R;

public class OrderConfirmation extends Fragment {

    private Session session;
    private RecyclerView mRecyclerView;
    private DatabaseReference mref;
    private TextView orderid, date, total, daname, address, status,delivery,grandtotal,discount;
    private TextView text,text1;
    private TextView name,number,textView50;
    private ImageView call;
    private LinearLayout linearLayout5;
    private String no="";
    private Button place;
    private ProgressBar progressbar;
    private String orderno="";
    private ArrayList<String> cartpushid=new ArrayList<String>();
    private ArrayList<String> quantity=new ArrayList<String>();

    private CartAdapter2 cartAdapter;
    private ArrayList<Cart1> cart=new ArrayList<Cart1>();


    public OrderConfirmation() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_order_confirmation, container, false);

        orderid = v.findViewById(R.id.orderid);
        date =v.findViewById(R.id.date);
        total = v.findViewById(R.id.total);
        daname = v.findViewById(R.id.daname);
        address = v.findViewById(R.id.address);
        status = v.findViewById(R.id.status);

        delivery = v.findViewById(R.id.delivery);
        discount = v.findViewById(R.id.discount);
        grandtotal = v.findViewById(R.id.grandtotal);
        name = v.findViewById(R.id.name);
        number = v.findViewById(R.id.number);
        call = v.findViewById(R.id.call);
        textView50 = v.findViewById(R.id.textView50);
        linearLayout5 = v.findViewById(R.id.linearLayout5);
        place = v.findViewById(R.id.place);
        progressbar = v.findViewById(R.id.progressbar);

        textView50.setVisibility(View.GONE);
        linearLayout5.setVisibility(View.GONE);

        text = v.findViewById(R.id.text);

        DecimalFormat form=new DecimalFormat("0.00");


        session = new Session(getContext());
        mRecyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        progressbar.setVisibility(View.GONE);

        cartpushid.clear();
        quantity.clear();

        mref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart");

        total.setText("\u20b9" + form.format(Double.parseDouble(session.getcarttotal())));
        if(Double.parseDouble(session.getcartitem())>300)
            delivery.setText("\u20b9300.00");
        else
            delivery.setText("\u20b9" + form.format(Double.parseDouble(session.getcartitem())));

        double gtot = Double.parseDouble(session.getcarttotal()) + Double.parseDouble(session.getcartitem());
        grandtotal.setText("\u20b9" + Math.round(gtot));

        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressbar.setVisibility(View.VISIBLE);
                if (getContext() != null) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure u want to place the order?")
                            .setConfirmText("Yes")
                            .setCancelText("No")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    session = new Session(getActivity());

                                    Date currentDate = new Date(System.currentTimeMillis());
                                    SimpleDateFormat df = new SimpleDateFormat("ddMMyy");
                                    final String date1 = df.format(currentDate);


                                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                    final String date2 = df1.format(currentDate);

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("LastOrder").equalTo(date2);


                                    SimpleDateFormat df2 = new SimpleDateFormat("dd,MMM yyyy HH:mm");
                                    final String date3 = df2.format(currentDate);


                                    mref = FirebaseDatabase.getInstance().getReference().child("Orders").push();
                                    mref.child("Pushid").setValue(mref.getKey());
                                    mref.child("UserId").setValue(session.getusername());
                                    mref.child("Status").setValue("1");
                                    double gtot = Double.parseDouble(session.getcarttotal()) + Double.parseDouble(session.getcartitem());
                                    mref.child("Total").setValue(""+grandtotal.getText().toString().substring(1));
                                    mref.child("SubTotal").setValue(""+total.getText().toString().substring(1));
                                    mref.child("DeliveryCharges").setValue(""+delivery.getText().toString().substring(1));
                                    mref.child("Discount").setValue("0.00");
                                    mref.child("Number").setValue(session.getnumber());
                                    mref.child("Flat").setValue(session.gethouseno());
                                    mref.child("LocationCoordinates").setValue(session.getloc());
                                    mref.child("Address").setValue(session.getaddress());
                                    mref.child("CName").setValue(session.getname());
                                    mref.child("Referral").setValue(session.getreferral());
                                    final int random = new Random().nextInt(10000);
                                    orderno = session.getusername().substring(3, 6) + date1 + random;

                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                                    mref.child("OrderNo").setValue("" + timestamp.getTime());
                                    mref.child("OrderDate").setValue(date2);
                                    mref.child("OrderDateTime").setValue(date3);
                                    mref.child("DeliveryDate").setValue(session.getdeliverydate());
                                    mref.child("DeliverySlot").setValue(session.getdeliveryslot());


                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername());
                                    ref.child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            mref.child("Cart").setValue(dataSnapshot.getValue());

                                            String items = "";
                                            for (DataSnapshot v : dataSnapshot.getChildren()) {
                                                items += v.child("Name").getValue().toString() + ":" + v.child("Qty").getValue().toString() + ",";
                                                cartpushid.add(v.child("PushId").getValue().toString());
                                                quantity.add(v.child("Qty").getValue().toString());
                                            }

                                            ref.child("Cart").removeValue();
                                            mref.child("Items").setValue(session.getitemname("INAME"));
                                            mref.child("ItemsDetails").setValue(items);
                                            mref.child("Qty").setValue("" + session.getcartitem());


                                            int step = quantity.size();

                                            if(0 < step) {
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(0)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(0));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(1 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(1)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(1));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(2 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(2)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(2));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(3 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(3)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(3));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(4 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(4)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(4));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(5 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(5)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(5));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(6 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(6)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(6));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(7 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(7)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(7));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(8 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(8)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(8));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(9 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(9)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(9));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(10 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(10)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(10));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(11 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(11)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(11));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(12 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(12)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(12));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(13 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(13)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(13));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(14 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(14)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(14));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(15 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(15)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(15));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(16 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(16)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(16));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(17 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(17)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(17));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(18 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(18)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(18));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(19 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(19)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(19));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(20 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(20)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(20));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(21 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(21)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(21));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(22 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(22)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(22));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(23 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(23)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(23));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(24 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(24)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(24));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(25 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(25)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(25));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(26 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(26)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(26));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(27 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(27)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(27));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(28 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(28)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(28));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(29 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(29)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(29));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(30 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(30)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(30));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(31 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(31)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(31));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(32 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(32)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(32));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(33 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(33)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(33));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(34 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(34)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(34));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(35 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(35)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(35));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(36 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(36)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(36));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(37 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(37)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(37));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(38 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(38)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(38));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(39 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(39)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(39));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(40 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(40)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(40));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(41 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(41)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(41));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(42 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(42)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(42));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(43 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(43)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(43));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(44 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(44)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(44));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(45 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(45)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(45));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(46 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(46)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(46));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(47 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(47)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(47));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(48 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(48)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(48));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }

                                            if(49 < step){
                                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(49)).child("Stock");
                                                dref.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        double value = 0;
                                                        if (currentData.getValue() != null) {
                                                            value = Double.parseDouble(currentData.getValue().toString()) - Integer.parseInt(quantity.get(49));
                                                        }
                                                        currentData.setValue(value);
                                                        return Transaction.success(currentData);
                                                    }
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                    }
                                                });
                                            }


                                            if(getActivity()!=null) {
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                    NotificationManager mNotificationManager =
                                                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                                    int importance = NotificationManager.IMPORTANCE_HIGH;
                                                    NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
                                                    mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                                                    mChannel.enableLights(true);
                                                    mChannel.setLightColor(Color.RED);
                                                    mChannel.enableVibration(true);
                                                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                                    mNotificationManager.createNotificationChannel(mChannel);
                                                }
                                            }

                                            /*
                                             * Displaying a notification locally
                                             */
                                            MyNotificationManager.getInstance(getContext()).displayNotification("Order Placed Successfully!!", "Order is Confirmed and will be delivered shortly");


                                            session.setextras("Orders");
                                            sweetAlertDialog.dismiss();
                                            if(getActivity()!=null) {
                                                Fragment fragment = new OrderCompleted();
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                fragmentManager.beginTransaction()
                                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {


                                        }
                                    });



                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();


                }
            }
        });

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<OrderDetails, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<vexetal.box.vexetalbox.Models.OrderDetails.OrderDetails, ViewHolder>(
                        vexetal.box.vexetalbox.Models.OrderDetails.OrderDetails.class,
                        R.layout.orders_details_row,
                        ViewHolder.class,
                        mref
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, vexetal.box.vexetalbox.Models.OrderDetails.OrderDetails orderDetails, int position) {
                        viewHolder.setDetails(getContext(),orderDetails.Image,orderDetails.Name,orderDetails.Price,orderDetails.PushId,orderDetails.Qty,orderDetails.Total,orderDetails.Units);

                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        final  ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View v, final int position) {

                            }

                            @Override
                            public void onItemLongClick(View v, int position) {

                            }
                        });
                        return viewHolder;
                    }

                    @Override
                    protected void onDataChanged() {
                        super.onDataChanged();
                        if (getItemCount() > 0) {
                            text.setVisibility(View.VISIBLE);
                        }
                        else{
                            text.setVisibility(View.GONE);
                        }
                    }

                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

}