package vexetal.box.vexetalbox.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Address.Address;
import vexetal.box.vexetalbox.Models.Address.AddressAdapter;
import vexetal.box.vexetalbox.Models.Address.ViewHolder;
import vexetal.box.vexetalbox.R;

public class SavedAddress extends Fragment {

    private ImageView back;
    private TextView add;


    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mref;
    private Session session;

    private int count=0;
    private String tot="0";

    String orderno="";

    private ArrayList<Address> address=new ArrayList<Address>();
    private AddressAdapter addressAdapter;

    public SavedAddress() {
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
        View v=inflater.inflate(R.layout.fragment_saved_address, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.GONE);
        }
        mRecyclerView = v.findViewById(R.id.recyclerView);
        add = v.findViewById(R.id.add);
        back = v.findViewById(R.id.back);

        if(getArguments()!=null) {
            tot = getArguments().getString("total");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    getActivity().onBackPressed();
                }
            }
        });

        session = new Session(getActivity());

        address.clear();
        addressAdapter = new AddressAdapter(address);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new LocationFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        session = new Session(getContext());
        mRecyclerView = v.findViewById(R.id.recyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mFirebaseDatabase.getReference().child("Users").child(session.getusername()).child("Address");



//        FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername())
//                .child("Address")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            for(DataSnapshot v:dataSnapshot.getChildren()){
//                                String Address="",Coord="",Flat="",Name="",PushId="";
//                                if(v.child("Address").exists())
//                                    Address=v.child("Address").getValue().toString();
//                                if(v.child("Coord").exists())
//                                    Coord=v.child("Coord").getValue().toString();
//                                if(v.child("Flat").exists())
//                                    Flat=v.child("Flat").getValue().toString();
//                                if(v.child("Name").exists())
//                                    Name=v.child("Name").getValue().toString();
//                                if(v.child("PushId").exists())
//                                    PushId=v.child("PushId").getValue().toString();
//
//                                address.add(new Address(
//                                        Address,
//                                        Coord,
//                                        Flat,
//                                        Name,
//                                        PushId
//                                ));
//                            }
//
//                            addressAdapter = new AddressAdapter(address);
//                            mRecyclerView.setAdapter(addressAdapter);
//                            count=addressAdapter.getItemCount();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });


        Button proceed=v.findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(session.getaddress())){
                    Toast.makeText(getContext(),"Select Delivery Address",Toast.LENGTH_LONG).show();
                    return;
                }

                if(getActivity()!=null){
                Fragment fragment = new SlotBooking();
                    Bundle bundle = new Bundle();
                    bundle.putString("total", new DecimalFormat("##.##").format(Double.parseDouble(tot)));
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Address, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Address, ViewHolder>(
                        Address.class,
                        R.layout.address_row,
                        ViewHolder.class,
                        mref
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Address address, int position) {
                        viewHolder.setDetails(getContext(),address.Address,address.Coord,address.Flat,address.Name,address.PushId);

                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View mView, int position) {

                                TextView name,houseno,address,coord,pushid;
                                ImageView image;
                                name=mView.findViewById(R.id.name);
                                houseno=mView.findViewById(R.id.houseno);
                                address=mView.findViewById(R.id.address);
                                coord=mView.findViewById(R.id.coord);
                                image=mView.findViewById(R.id.image);
                                pushid=mView.findViewById(R.id.pushid);

                                image.setImageResource(R.drawable.ic_success);

                                session.sethouseno(houseno.getText().toString());
                                session.setaddress(address.getText().toString());
                                session.setdaname(name.getText().toString());
                                session.setloc(coord.getText().toString());
                                session.setdefault(pushid.getText().toString());

                                notifyDataSetChanged();



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
                        count = getItemCount();
                    }
                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

}
