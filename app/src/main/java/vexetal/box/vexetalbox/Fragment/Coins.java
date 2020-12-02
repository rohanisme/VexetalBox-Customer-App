package vexetal.box.vexetalbox.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Coins.ViewHolder;
import vexetal.box.vexetalbox.R;

public class Coins extends Fragment {

    private TextView cash;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mref;
    private Session session;
    private RecyclerView mRecyclerView;


    public Coins() {
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
        View v=inflater.inflate(R.layout.fragment_coins, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.GONE);
        }

        cash=v.findViewById(R.id.cash);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        session = new Session(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mFirebaseDatabase.getReference().child("Users").child(session.getusername()).child("Transactions");


        FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                            cash.setText("Available Coins : \u20b9"+dataSnapshot.child("Coins").getValue().toString());
                        else
                            cash.setText("Available Coins : \u20b90");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<vexetal.box.vexetalbox.Models.Coins.Coins, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<vexetal.box.vexetalbox.Models.Coins.Coins, ViewHolder>(
                        vexetal.box.vexetalbox.Models.Coins.Coins.class,
                        R.layout.cardview_coins,
                        ViewHolder.class,
                        mref
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, vexetal.box.vexetalbox.Models.Coins.Coins rewards, int position) {
                        viewHolder.setDetails(getContext(), rewards.Date, rewards.Generated, rewards.PushId, rewards.TransactionName, rewards.TransactionType, rewards.UserId, rewards.UserBalance, rewards.Amount, position,rewards.Status);

                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
//
                        return viewHolder;
                    }

                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

}