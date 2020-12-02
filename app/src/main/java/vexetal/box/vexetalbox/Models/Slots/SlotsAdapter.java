package vexetal.box.vexetalbox.Models.Slots;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;


public class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.ViewHolder>{

    private ArrayList<String> Slots;
    private ArrayList<String> Slotsfiltered;

    ArrayList<String> iname = new ArrayList<String>();
    ArrayList<String> iprice = new ArrayList<String>();


    public SlotsAdapter(ArrayList<String> Slots) {
        this.Slots = Slots;
        this.Slotsfiltered = Slots;
    }


    @NonNull
    @Override
    public SlotsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=(View) LayoutInflater.from(parent.getContext()).inflate(R.layout.slots_row,parent,false);
        return new SlotsAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final SlotsAdapter.ViewHolder holder, final int position) {
       final String slot=Slots.get(position);

        holder.name.setText(slot);
        holder.pushid.setText(slot);
        final Session session=new Session(holder.view.getContext());
        if(Integer.parseInt(session.getselection())==position) {
            holder.selector.setImageResource(R.drawable.ic_success);
        }
        else{
            holder.selector.setImageResource(R.drawable.ic_empty);
        }

        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setselection(""+position);
                session.setdeliveryslot(slot);

                notifyDataSetChanged();
            }
        });

    }



    @Override
    public int getItemCount() {
        if(Slotsfiltered!=null){
            return Slotsfiltered.size();
        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public final View view;
        TextView name,pushid;
        ImageView selector;
        LinearLayout linear;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            pushid = view.findViewById(R.id.pushid);
            selector = view.findViewById(R.id.selector);
            linear = view.findViewById(R.id.linear);

        }


        @Override
        public void onClick(View v) {
        }
    }

}



