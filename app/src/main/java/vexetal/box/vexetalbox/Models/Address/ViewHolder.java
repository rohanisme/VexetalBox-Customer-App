package vexetal.box.vexetalbox.Models.Address;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;


public  class ViewHolder extends RecyclerView.ViewHolder {
    View mView;

    private ClickListener mClickListener;

    public ViewHolder(View itemView) {

        super(itemView);
        mView = itemView;
        //Item Click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });

        //Item Long Click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });
    }

    public void setDetails(Context ctx,String Address,String Coord,String Flat,String Name,String PushId){

        TextView name,houseno,address,coord,pushid;
        ImageView image;
        name=mView.findViewById(R.id.name);
        houseno=mView.findViewById(R.id.houseno);
        address=mView.findViewById(R.id.address);
        coord=mView.findViewById(R.id.coord);
        pushid=mView.findViewById(R.id.pushid);
        image=mView.findViewById(R.id.image);

        name.setText(Name);
        houseno.setText(Flat);
        address.setText(Address);
        coord.setText(Coord);
        pushid.setText(PushId);

        Session session=new Session(ctx);

        if(TextUtils.isEmpty(session.getdefault())){
            image.setImageResource(R.drawable.ic_empty);
        }
        else if(session.getdefault().equals(PushId)){
            image.setImageResource(R.drawable.ic_success);
        }
        else{
            image.setImageResource(R.drawable.ic_empty);
        }



    }

    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }
}
