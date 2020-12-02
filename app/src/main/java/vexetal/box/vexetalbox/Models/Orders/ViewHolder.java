package vexetal.box.vexetalbox.Models.Orders;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

    public void setDetails(Context ctx,String Address,String Amount,String CName,String DeliveryCharges,String Flat,String LocationCoordinates,String Number,String OrderDate,
                           String OrderDateTime,String OrderNo,String OrderType,String Payment,String Pushid,String Qty,String RazorpayId,String Status,String Total,String UserId,String ItemDetails){


        TextView orderid,items,date,total,status,details,pushid,payment;

        orderid = mView.findViewById(R.id.orderid);
        items = mView.findViewById(R.id.items);
        date = mView.findViewById(R.id.date);
        total = mView.findViewById(R.id.total);
        status = mView.findViewById(R.id.status);
        pushid = mView.findViewById(R.id.pushid);
        payment = mView.findViewById(R.id.payment);

        orderid.setText("Order Id: #"+OrderNo.substring(5));
        items.setText(Qty+" Items");
        date.setText(OrderDateTime);
        total.setText("\u20b9"+Total);
        if(TextUtils.isEmpty(Payment)){
            payment.setText("Payment : Pending");
        }
        else{
            payment.setText("Payment : "+Payment);
        }
        pushid.setText(Pushid);
            if (Status.equals("1")) {
                status.setText("  Order Placed");
                status.setTextColor(Color.parseColor("#0000FF"));
                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue, 0, 0, 0);
            } else if (Status.equals("2")) {
                status.setText("  Order Accepted");
                status.setTextColor(Color.parseColor("#FFFF00"));
                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.yellow, 0, 0, 0);
            } else if (Status.equals("3")) {
                status.setText("  Out for Delivery");
                status.setTextColor(Color.parseColor("#FFFF00"));
                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.yellow, 0, 0, 0);
            }
            else if (Status.equals("4")) {
                status.setText("  Delivered");
                status.setTextColor(Color.parseColor("#008000"));
                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green, 0, 0, 0);
            } else if (Status.equals("10")) {
                status.setText("  Cancelled");
                status.setTextColor(Color.parseColor("#FF0000"));
                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red, 0, 0, 0);
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
