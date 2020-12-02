package vexetal.box.vexetalbox.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Orders.Orders;
import vexetal.box.vexetalbox.Models.Orders.ViewHolder;
import vexetal.box.vexetalbox.R;

public class OrderHistory extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mref;
    private Session sessions;

    public OrderHistory() {
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
        View v=inflater.inflate(R.layout.fragment_order_history, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.GONE);
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

        sessions = new Session(getContext());
        mRecyclerView = v.findViewById(R.id.recyclerView);

        sessions.setextras("");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mFirebaseDatabase.getReference().child("Orders");


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query firebasequery = mref.orderByChild("UserId").equalTo(sessions.getusername());
        FirebaseRecyclerAdapter<Orders, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Orders, ViewHolder>(
                        Orders.class,
                        R.layout.orders_row,
                        ViewHolder.class,
                        firebasequery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Orders order, int position) {
                        viewHolder.setDetails(getContext(),order.Address,order.Amount,order.CName,order.DeliveryCharges,order.Flat,order.LocationCoordinates,order.Number,order.OrderDate,order.OrderDateTime,order.OrderNo,order.OrderType,order.Payment,order.Pushid,order.Qty,order.RazorpayId,order.Status,order.Total,order.UserId,order.ItemsDetails);

                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {


                                TextView pushid = v.findViewById(R.id.pushid);

                                if(getActivity()!=null) {
                                    Bundle bundle = new Bundle();
                                    OrderDetails fragment = new OrderDetails();
                                    bundle.putString("pushid", pushid.getText().toString());
                                    fragment.setArguments(bundle);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .addToBackStack(null)
                                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                }


                            }

                            @Override
                            public void onItemLongClick(View v, int position) {

                            }
                        });
                        return viewHolder;
                    }

                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}

