package vexetal.box.vexetalbox.Models.Coins;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import vexetal.box.vexetalbox.R;


public class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView Rewardsname, Rewardsid, Rewardsdate, Rewardsdetails, Rewardsorder, Rewardsamount, Rewardsbalance;
    ImageView Rewardsarrow;
    CardView Rewardscard;

    //    private  ViewHolder.ClickListener mClickListener;
    public ViewHolder(View itemView) {

        super(itemView);
        mView = itemView;

    }

    public void setDetails(Context ctx, String Date, String Generated, String PushId, String TransactionName, String TransactionType, String UserId, String UserBalance, String Amount, int position,String status){
        Rewardsname = (TextView) mView.findViewById(R.id.RewardsName);
        Rewardsid = (TextView) mView.findViewById(R.id.Rewardsid);
        Rewardsdate = (TextView) mView.findViewById(R.id.Rewardsdate);
        Rewardsdetails = (TextView) mView.findViewById(R.id.Rewardsdetails);
        Rewardsorder = (TextView) mView.findViewById(R.id.Rewardsorder);
        Rewardsamount = (TextView) mView.findViewById(R.id.Rewardsamount);
        Rewardsbalance = (TextView) mView.findViewById(R.id.Rewardsbalance);
        Rewardscard = (CardView) mView.findViewById(R.id.RewardsCard);
        String str = "\u20B9";



        Rewardsid.setVisibility(View.GONE);
        Rewardsorder.setVisibility(View.GONE);
        Rewardsname.setText(TransactionName);
        Rewardsdate.setText(Date);
        Rewardsdetails.setText(UserId);
        Rewardsdetails.setVisibility(View.GONE);

//        if (status.equals("Approved")) {
//            Rewardscard.setVisibility(View.VISIBLE);
//        } else {
//            Rewardscard.setVisibility(View.GONE);
//            Rewardscard.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
//        }

        if (TransactionType.equalsIgnoreCase("Cr")) {
            Rewardsamount.setText(str + Amount);
            Rewardsamount.setTextColor(Color.parseColor("#3AC21B"));
        } else {
            Rewardsamount.setText(str + Amount);
            Rewardsamount.setTextColor(Color.parseColor("#E90303"));
        }
        Rewardsbalance.setText(str + UserBalance);

//        if (position % 2 != 0) {
//            Rewardscard.setCardBackgroundColor(Color.parseColor("#F2F2F2"));
//        } else {
//            Rewardscard.setCardBackgroundColor(Color.parseColor("#F9F9F9"));
//        }
    }
}

