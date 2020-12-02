package vexetal.box.vexetalbox.Models.Cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;

public class CartAdapter2 extends RecyclerView.Adapter<CartAdapter2.ViewHolder>{

    ArrayList<Cart1> carts;
    ArrayList<String> iname = new ArrayList<String>();
    ArrayList<String> iprice = new ArrayList<String>();
    ArrayList<String> isubscription = new ArrayList<String>();
    Session session;

    public CartAdapter2(ArrayList<Cart1> carts) {
        this.carts = carts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=(View) LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter2.ViewHolder holder, final int position) {

        final Cart1 cart = carts.get(position);

        holder.name.setText(cart.Name);
        holder.pushid.setText(cart.PushId);
        holder.total.setText("\u20b9"+cart.Total);
        holder.units.setText("\u20b9"+cart.Price+" / "+cart.Units);
        holder.qty.setText(cart.Qty);
        holder.min.setText(cart.Min);



        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(20));


        Glide.with(holder.view.getContext())
                .load(cart.Image)
                .apply(requestOptions)
                .into(holder.image);

        session=new Session(holder.view.getContext());

        final DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Orders").child(session.gettemp()).child("Cart");



        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = Integer.parseInt(holder.qty.getText().toString());
                int min=Integer.parseInt(holder.min.getText().toString());
                a=a-min;
                if (a < Integer.parseInt(holder.min.getText().toString())) {
                    new SweetAlertDialog(holder.view.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure u want to remove this item from the order?")
                            .setConfirmText("Yes")
                            .setCancelText("No")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    mref.child(holder.pushid.getText().toString()).removeValue();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }
                else{
                    holder.qty.setText("" + a);
                    double tot = Double.parseDouble(cart.Price) * Integer.parseInt(holder.qty.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Name").setValue(holder.name.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Qty").setValue(holder.qty.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Units").setValue(cart.Units);
                    mref.child(holder.pushid.getText().toString()).child("Price").setValue(cart.Price);
                    mref.child(holder.pushid.getText().toString()).child("Total").setValue("" + tot);
                    mref.child(holder.pushid.getText().toString()).child("PushId").setValue(holder.pushid.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Image").setValue(cart.Image);
                    mref.child(holder.pushid.getText().toString()).child("Min").setValue(cart.Min);
                }
            }
        });


        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int min=Integer.parseInt(holder.min.getText().toString());
                int a = Integer.parseInt(holder.qty.getText().toString());
                a=a+min;
                holder.qty.setText("" + a);

                double tot = Double.parseDouble(cart.Price) * Integer.parseInt(holder.qty.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Name").setValue(holder.name.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Qty").setValue(holder.qty.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Units").setValue(cart.Units);
                mref.child(holder.pushid.getText().toString()).child("Price").setValue(cart.Price);
                mref.child(holder.pushid.getText().toString()).child("Total").setValue("" + tot);
                mref.child(holder.pushid.getText().toString()).child("PushId").setValue(holder.pushid.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Image").setValue(cart.Image);
                mref.child(holder.pushid.getText().toString()).child("Min").setValue(cart.Min);

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart").child(holder.pushid.getText().toString()).removeValue();

                carts.remove(position);
                notifyDataSetChanged();

            }
        });



    }


    @Override
    public int getItemCount() {
        if(carts!=null){
            return carts.size();
        }
        else {
            return 0;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public final View view;
        TextView name,units,total,qty,pushid,min;
        LinearLayout minus,plus,linearLayout;
        ImageView delete,image;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
             name = view.findViewById(R.id.name);
             total = view.findViewById(R.id.total);
             min = view.findViewById(R.id.min);
             pushid = view.findViewById(R.id.pushid);
             units = view.findViewById(R.id.units);
             qty = view.findViewById(R.id.qty);
             minus = view.findViewById(R.id.minus);
             plus = view.findViewById(R.id.plus);
             image = view.findViewById(R.id.image);
             delete = view.findViewById(R.id.delete);
             linearLayout = view.findViewById(R.id.linearLayout);

        }


        @Override
        public void onClick(View v) {
        }
    }

}



