package vexetal.box.vexetalbox.Models.OrderDetails;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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

    public void setDetails(Context ctx,String Image,String Name,String Price,String PushId,String Qty,String Total,String Units){


        TextView name,units,total,qty;
        ImageView image;
        image=mView.findViewById(R.id.image);
        name=mView.findViewById(R.id.name);
        units=mView.findViewById(R.id.units);
        total=mView.findViewById(R.id.total);
        qty=mView.findViewById(R.id.qty);

        Glide.with(ctx)
                .load(Image)
                .into(image);

        name.setText(Name);
        units.setText("\u20b9"+Price+" / "+Units);
        total.setText("\u20b9"+Total);
        qty.setText("x"+Qty);




    }

    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }
}
