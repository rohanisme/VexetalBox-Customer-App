package vexetal.box.vexetalbox.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Cart.Cart1;
import vexetal.box.vexetalbox.Models.Cart.CartAdapter1;
import vexetal.box.vexetalbox.R;


public class Cart extends Fragment {

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private TextView total;
    private ImageView checkout;
    private Session session;
    private CartAdapter1 cartAdapter;
    private ArrayList<Cart1> cart=new ArrayList<Cart1>();
    private DecimalFormat form;
    private String type="";


    public Cart() {
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
        View v=inflater.inflate(R.layout.fragment_cart, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.GONE);
        }

        if(getActivity()!=null) {
            ImageView i1, i4, i3;
            i1 = getActivity().findViewById(R.id.i1);
            i4 = getActivity().findViewById(R.id.i4);
            i3 = getActivity().findViewById(R.id.i3);

            i1.setImageResource(R.drawable.b1);
            i3.setImageResource(R.drawable.hb3);
            i4.setImageResource(R.drawable.b4);
        }

        final ImageView back = v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    getActivity().onBackPressed();
                }
            }
        });

        mRecyclerView=v.findViewById(R.id.recyclerView);
        progressBar=v.findViewById(R.id.progressbar);
        total=v.findViewById(R.id.total);
        checkout=v.findViewById(R.id.checkout);
        form = new DecimalFormat("0.00");

        cartAdapter = new CartAdapter1(cart);

        session =  new Session(getContext());
        session.setextras("");

        FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        cart.clear();
                        mRecyclerView.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot v : dataSnapshot.getChildren()) {
                                if (v.exists()) {
                                    String a="",b="",c="",d="",e="",f="",g="",h="",i="";
                                    if(v.child("Name").exists())
                                        a=v.child("Name").getValue().toString();
                                  if(v.child("Price").exists())
                                        c=v.child("Price").getValue().toString();
                                    if(v.child("PushId").exists())
                                        d=v.child("PushId").getValue().toString();
                                    if(v.child("Qty").exists())
                                        e=v.child("Qty").getValue().toString();
                                    if(v.child("Total").exists())
                                        f=v.child("Total").getValue().toString();
                                    if(v.child("Units").exists())
                                        g=v.child("Units").getValue().toString();
                                    if(v.child("Image").exists())
                                        h=v.child("Image").getValue().toString();
                                    if(v.child("Min").exists())
                                        i=v.child("Min").getValue().toString();

                                    cart.add(new Cart1(
                                            a,c,d,e,f,g,h,i));
                                }
                            }
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            cartAdapter = new CartAdapter1(cart);
                            mRecyclerView.setAdapter(cartAdapter);
                            progressBar.setVisibility(View.GONE);
                            mRecyclerView.setEnabled(true);
                            type = "Groceries";
                        } else {
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            cartAdapter = new CartAdapter1(cart);
                            mRecyclerView.setAdapter(cartAdapter);
                            progressBar.setVisibility(View.GONE);
                            mRecyclerView.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername())
                .child("Cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                double gtot = Double.parseDouble(session.getcarttotal());
                                total.setText("\u20b9" + Math.round(gtot));
                            }
                            else{
                                total.setText("\u20b90.00");
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Double.parseDouble(total.getText().toString().substring(1))==0){
                    Toast.makeText(getContext(),"Your cart seems to be empty, please add items to proceed",Toast.LENGTH_LONG).show();
                    return;
                }

                if(Double.parseDouble(session.getcarttotal())<=1000){
                    Toast.makeText(getContext(),"Minimum order value is 1000",Toast.LENGTH_LONG).show();
                    return;
                }

                if(getActivity()!=null) {
                    Fragment fragment = new SavedAddress();
                    Bundle bundle = new Bundle();
                    bundle.putString("total", new DecimalFormat("##.##").format(Double.parseDouble(total.getText().toString().substring(1))));
                    fragment.setArguments(bundle);
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
