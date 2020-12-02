package vexetal.box.vexetalbox.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Cart.Cart1;
import vexetal.box.vexetalbox.Models.Cart.CartAdapter2;
import vexetal.box.vexetalbox.Models.OrderDetails.ViewHolder;
import vexetal.box.vexetalbox.R;


public class OrderDetails extends Fragment {

    private String pushid = "";
    private String Status="";


    private Session session;
    private RecyclerView mRecyclerView;
    private DatabaseReference mref;
    private TextView orderid, date, total, daname, address, status,delivery,grandtotal,discount;
    private TextView text,text1;
    private TextView name,number,textView50;
    private ImageView call;
    private LinearLayout linearLayout5;
    private String no="";
    private Button cancel,help;
    private ProgressBar progressbar;
    private String ordno="";

    private ArrayList<String> cartpushid=new ArrayList<String>();
    private ArrayList<String> quantity=new ArrayList<String>();

    private CartAdapter2 cartAdapter;
    private ArrayList<Cart1> cart=new ArrayList<Cart1>();

    public OrderDetails() {
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
        View v=inflater.inflate(R.layout.fragment_order_details, container, false);



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
        cancel = v.findViewById(R.id.cancel);
        help = v.findViewById(R.id.help);
        progressbar = v.findViewById(R.id.progressbar);

        textView50.setVisibility(View.GONE);
        linearLayout5.setVisibility(View.GONE);

        text = v.findViewById(R.id.text);

        DecimalFormat form=new DecimalFormat("0.00");


        session = new Session(getContext());
        mRecyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        cancel.setVisibility(View.GONE);
        help.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);

        cartpushid.clear();
        quantity.clear();

        if(getArguments()!=null)
            pushid = getArguments().getString("pushid");


        FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("Cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            double tot=0;
                            for(DataSnapshot v:dataSnapshot.getChildren()){
                                if(v.child("Total").exists())
                                    tot+=Double.parseDouble(v.child("Total").getValue().toString());
                                if(v.child("PushId").exists()&&v.child("Qty").exists()) {
                                    cartpushid.add(v.child("PushId").getValue().toString());
                                    quantity.add(v.child("Qty").getValue().toString());
                                }
                            }
                            total.setText("\u20b9"+new DecimalFormat("0.00").format(tot));
                            grandtotal.setText("\u20b9"+new DecimalFormat("0.00").format(tot));
                            FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("Total").setValue(""+tot);
                            FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("SubTotal").setValue(""+tot);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


        FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            orderid.setText("Order ID : #"+dataSnapshot.child("OrderNo").getValue().toString().substring(5));
                            date.setText(dataSnapshot.child("OrderDateTime").getValue().toString());
                            total.setText("\u20b9"+new DecimalFormat("0.00").format(Double.parseDouble(dataSnapshot.child("SubTotal").getValue().toString())));
                            delivery.setText("\u20b9"+new DecimalFormat("0.00").format(Double.parseDouble(dataSnapshot.child("DeliveryCharges").getValue().toString())));
                            discount.setText("\u20b9"+new DecimalFormat("0.00").format(Double.parseDouble(dataSnapshot.child("Discount").getValue().toString())));
                            grandtotal.setText("\u20b9"+new DecimalFormat("0.00").format(Double.parseDouble(dataSnapshot.child("Total").getValue().toString())));
                            address.setText(dataSnapshot.child("Address").getValue().toString());
                            daname.setText(dataSnapshot.child("CName").getValue().toString());
                                Status =dataSnapshot.child("Status").getValue().toString();
                                ordno =dataSnapshot.child("OrderNo").getValue().toString();

                                if(Status.equals("3")){
                                    if(dataSnapshot.child("DeliveryName").exists())
                                        name.setText(dataSnapshot.child("DeliveryName").getValue().toString());
                                    number.setText(dataSnapshot.child("DeliveryBoy").getValue().toString());
                                    no=dataSnapshot.child("DeliveryBoy").getValue().toString();
                                    textView50.setVisibility(View.VISIBLE);
                                    linearLayout5.setVisibility(View.VISIBLE);
                                }
                                else{
                                    textView50.setVisibility(View.GONE);
                                    linearLayout5.setVisibility(View.GONE);
                                }

                                session.settemp(pushid);

//                                if(Status.equals("1")){
//                                    cart.clear();
//                                    cartAdapter = new CartAdapter2(cart);
//                                    session =  new Session(getContext());
//                                    session.setextras("");
//                                    FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("Cart")
//                                            .addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                                                    cart.clear();
//                                                    mRecyclerView.setEnabled(false);
//                                                    if (dataSnapshot.exists()) {
//                                                        for (DataSnapshot v : dataSnapshot.getChildren()) {
//                                                            if (v.exists()) {
//                                                                String a="",b="",c="",d="",e="",f="",g="",h="",i="";
//                                                                if(v.child("Name").exists())
//                                                                    a=v.child("Name").getValue().toString();
//                                                                if(v.child("Price").exists())
//                                                                    c=v.child("Price").getValue().toString();
//                                                                if(v.child("PushId").exists())
//                                                                    d=v.child("PushId").getValue().toString();
//                                                                if(v.child("Qty").exists())
//                                                                    e=v.child("Qty").getValue().toString();
//                                                                if(v.child("Total").exists())
//                                                                    f=v.child("Total").getValue().toString();
//                                                                if(v.child("Units").exists())
//                                                                    g=v.child("Units").getValue().toString();
//                                                                if(v.child("Image").exists())
//                                                                    h=v.child("Image").getValue().toString();
//                                                                if(v.child("Min").exists())
//                                                                    i=v.child("Min").getValue().toString();
//                                                                cart.add(new Cart1(
//                                                                        a,c,d,e,f,g,h,i));
//                                                            }
//                                                        }
//                                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                                                        mRecyclerView.setLayoutManager(mLayoutManager);
//                                                        cartAdapter = new CartAdapter2(cart);
//                                                        mRecyclerView.setAdapter(cartAdapter);
//                                                    } else {
//                                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                                                        mRecyclerView.setLayoutManager(mLayoutManager);
//                                                        cartAdapter = new CartAdapter2(cart);
//                                                        mRecyclerView.setAdapter(cartAdapter);
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                                }
//                                            });
//                                }
//                                else{
                                    mref = FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("Cart");

                                    FirebaseRecyclerAdapter<vexetal.box.vexetalbox.Models.OrderDetails.OrderDetails, ViewHolder> firebaseRecyclerAdapter =
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
//                                }




                            if (Status.equals("1")) {
                                status.setText("  Order Placed");
                                status.setTextColor(Color.parseColor("#0000FF"));
                                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue, 0, 0, 0);
                                cancel.setVisibility(View.VISIBLE);
                            } else if (Status.equals("2")) {
                                status.setText("  Order Accepted");
                                status.setTextColor(Color.parseColor("#FFFF00"));
                                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.yellow, 0, 0, 0);
                                cancel.setVisibility(View.GONE);
                            } else if (Status.equals("3")) {
                                status.setText("  Out for Delivery");
                                status.setTextColor(Color.parseColor("#FFFF00"));
                                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.yellow, 0, 0, 0);
                                cancel.setVisibility(View.GONE);
                            }
                            else if (Status.equals("4")) {
                                status.setText("  Delivered");
                                status.setTextColor(Color.parseColor("#008000"));
                                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green, 0, 0, 0);
                                cancel.setVisibility(View.GONE);
                                help.setVisibility(View.VISIBLE);
                            } else if (Status.equals("10")) {
                                status.setText("  Cancelled");
                                status.setTextColor(Color.parseColor("#FF0000"));
                                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red, 0, 0, 0);
                                cancel.setVisibility(View.GONE);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    final View ratingDialogView = getLayoutInflater().inflate(R.layout.ratings, null);
                    final AlertDialog ratingDialog = new AlertDialog.Builder(getActivity()).create();
                    ratingDialog.setView(ratingDialogView);
                    ratingDialog.show();
                    final RotationRatingBar ratingBar = ratingDialogView.findViewById(R.id.ratebar);
                    ratingBar.setNumStars(5);
                    ratingBar.setStepSize(0.5f);
                    ratingBar.setMinimumStars(1);

                    final EditText comments = ratingDialogView.findViewById(R.id.comments);
                    Button submit = ratingDialogView.findViewById(R.id.submit);


                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("Ratings").setValue("" + ratingBar.getRating());
                            FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("RatingsC").setValue("" + comments.getText().toString());

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Feedback").push();
                            ref.child("PushId").setValue(ref.getKey());
                            ref.child("Comments").setValue(comments.getText().toString());
                            ref.child("Ratings").setValue("" + ratingBar.getRating());
                            ref.child("OrderId").setValue(ordno);
                            ref.child("OrderPushId").setValue(pushid);
                            ref.child("CustomerName").setValue(session.getname());
                            ref.child("CustomerNumber").setValue(session.getnumber());
                            ref.child("Referral").setValue(session.getreferral());
                            ratingDialog.dismiss();
                            help.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Thank you for your valuable feedback", Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getContext()!=null) {
                    SweetAlertDialog sDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Are you sure u want to cancel the order?")
                            .setCancelText("No")
                            .setConfirmText("Yes")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();

                                    progressbar.setVisibility(View.VISIBLE);

                                    FirebaseDatabase.getInstance().getReference().child("Orders").child(pushid).child("Status").setValue("10");

                                    int step = quantity.size();
                                    if(0 < step) {
                                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Products").child(cartpushid.get(0)).child("Stock");
                                        dref.runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                double value = 0;
                                                if (currentData.getValue() != null) {
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(0));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(1));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(2));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(3));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(4));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(5));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(6));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(7));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(8));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(9));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(10));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(11));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(12));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(13));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(14));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(15));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(16));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(17));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(18));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(19));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(20));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(21));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(22));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(23));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(24));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(25));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(26));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(27));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(28));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(29));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(30));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(31));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(32));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(33));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(34));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(35));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(36));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(37));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(38));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(39));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(40));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(41));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(42));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(43));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(44));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(45));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(46));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(47));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(48));
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
                                                    value = Double.parseDouble(currentData.getValue().toString()) + Integer.parseInt(quantity.get(49));
                                                }
                                                currentData.setValue(value);
                                                return Transaction.success(currentData);
                                            }
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                            }
                                        });
                                    }

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            sweetAlertDialog.dismiss();
                                            if(getActivity()!=null){
                                                progressbar.setVisibility(View.GONE);
                                                getActivity().onBackPressed();
                                            }
                                        }
                                    }, 100*step);



                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            });
                    sDialog.setCancelable(false);
                    sDialog.show();
                }
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+no));
                startActivity(intent);
            }
        });

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();





    }

}