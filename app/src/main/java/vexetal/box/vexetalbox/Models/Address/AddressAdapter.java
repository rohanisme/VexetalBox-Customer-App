package vexetal.box.vexetalbox.Models.Address;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Activities.MainActivity;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Fragment.Home;
import vexetal.box.vexetalbox.Fragment.LocationEdit;
import vexetal.box.vexetalbox.Fragment.SlotBooking;
import vexetal.box.vexetalbox.R;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder>  {

    private ArrayList<Address> Addresss;



    public AddressAdapter(ArrayList<Address> Addresss) {

        this.Addresss = Addresss;

    }


    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=(View) LayoutInflater.from(parent.getContext()).inflate(R.layout.address_row,parent,false);
        return new AddressAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddressAdapter.ViewHolder holder, int position) {
        final Address address=Addresss.get(position);


        holder.name.setText(address.Name);
        holder.houseno.setText(address.Flat);
        holder.address.setText(address.Address);
        holder.coord.setText(address.Coord);
        holder.pushid.setText(address.PushId);


        Session session=new Session(holder.view.getContext());

        if(TextUtils.isEmpty(session.getdefault())){
            holder.image.setImageResource(R.drawable.ic_empty);
        }
        else if(session.getdefault().equals(address.PushId)){
            holder.image.setImageResource(R.drawable.ic_success);
        }
        else{
            holder.image.setImageResource(R.drawable.ic_empty);
        }


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseDatabase.getInstance().getReference().child("Masters").child("Radius")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    String coordindates=dataSnapshot.child("Coord").getValue().toString();
                                    String slab1=dataSnapshot.child("Slab1").getValue().toString();
                                    String slab2=dataSnapshot.child("Slab2").getValue().toString();
                                    String slab3=dataSnapshot.child("Slab3").getValue().toString();

                                    float[] results = new float[1];
                                    String r[]=coordindates.split(",");
                                    String s[]=holder.coord.getText().toString().split(",");

                                    try {
                                        Location.distanceBetween(Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(r[0]), Double.parseDouble(r[1]), results);
                                        if (Math.round(results[0] / 1000) <= Double.parseDouble(slab1)) {

                                            session.sethouseno(holder.houseno.getText().toString());
                                            session.setaddress(holder.address.getText().toString());
                                            session.setdaname(holder.name.getText().toString());
                                            session.setloc(holder.coord.getText().toString());
                                            session.setdefault(holder.pushid.getText().toString());
                                            session.setlocationbacground("no");
                                            session.setslab("1");

                                            if(holder.view.getContext()!=null) {
                                                MainActivity mainActivity=(MainActivity)holder.view.getContext();
                                                Fragment fragment = new SlotBooking();
                                                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                                                fragmentManager.beginTransaction()
                                                        .addToBackStack(null)
                                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                            }

                                        }
                                        else   if (Math.round(results[0] / 1000) <= Double.parseDouble(slab2)) {
                                            session.sethouseno(holder.houseno.getText().toString());
                                            session.setaddress(holder.address.getText().toString());
                                            session.setdaname(holder.name.getText().toString());
                                            session.setloc(holder.coord.getText().toString());
                                            session.setdefault(holder.pushid.getText().toString());
                                            session.setlocationbacground("no");
                                            session.setslab("2");

                                            MainActivity mainActivity=(MainActivity)holder.view.getContext();
                                            Fragment fragment = new SlotBooking();
                                            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                                            fragmentManager.beginTransaction()
                                                    .addToBackStack(null)
                                                    .replace(R.id.frame_container, fragment).commitAllowingStateLoss();

                                        }
                                        else   if (Math.round(results[0] / 1000) <= Double.parseDouble(slab3)) {
                                            session.sethouseno(holder.houseno.getText().toString());
                                            session.setaddress(holder.address.getText().toString());
                                            session.setdaname(holder.name.getText().toString());
                                            session.setloc(holder.coord.getText().toString());
                                            session.setdefault(holder.pushid.getText().toString());
                                            session.setlocationbacground("no");
                                            session.setslab("3");

                                            MainActivity mainActivity=(MainActivity)holder.view.getContext();
                                            Fragment fragment = new SlotBooking();
                                            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                                            fragmentManager.beginTransaction()
                                                    .addToBackStack(null)
                                                    .replace(R.id.frame_container, fragment).commitAllowingStateLoss();

                                        }
                                        else{
                                            if(holder.view.getContext()!=null) {
                                                new SweetAlertDialog(holder.view.getContext(), SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Not Serviceable!")
                                                        .setContentText("Currently we don't offer any services in the location selected")
                                                        .show();
                                            }
                                        }
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                                else{
                                    if(holder.view.getContext()!=null) {
                                        new SweetAlertDialog(holder.view.getContext(), SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Not Serviceable!")
                                                .setContentText("Currently we don't offer any services in the location selected")
                                                .show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                
                notifyDataSetChanged();
            }
        });


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity=(MainActivity)holder.view.getContext();
                Fragment fragment = new LocationEdit();
                Bundle bundle=new Bundle();
                bundle.putString("pushid",address.PushId);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, fragment).commit();
            }
        });


        holder.setIsRecyclable(false);

    }
//
//    public void removeItem(int position) {
//        slit.remove(position);
//        notifyItemRemoved(position);
//    }

    @Override
    public int getItemCount() {
        if(Addresss!=null){
            return Addresss.size();
        }
        else {
            return 0;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public final View view;
        TextView name,houseno,address,coord,pushid,edit;
        ImageView image;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name=view.findViewById(R.id.name);
            houseno=view.findViewById(R.id.houseno);
            address=view.findViewById(R.id.address);
            coord=view.findViewById(R.id.coord);
            pushid=view.findViewById(R.id.pushid);
            image=view.findViewById(R.id.image);
            linearLayout=view.findViewById(R.id.linearLayout);
            edit=view.findViewById(R.id.edit);
        }


        @Override
        public void onClick(View v) {
        }
    }

}



