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

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;

public class CartAdapter1  extends RecyclerView.Adapter<CartAdapter1.ViewHolder>{

    ArrayList<Cart1> carts;
    ArrayList<String> iname = new ArrayList<String>();
    ArrayList<String> iprice = new ArrayList<String>();
    ArrayList<String> isubscription = new ArrayList<String>();
    Session session;

    public CartAdapter1(ArrayList<Cart1> carts) {
        this.carts = carts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=(View) LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter1.ViewHolder holder, final int position) {

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

        final DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart");



        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = Integer.parseInt(holder.qty.getText().toString());
                int min=Integer.parseInt(holder.min.getText().toString());
                a=a-min;
                if (a < Integer.parseInt(holder.min.getText().toString())) {

                    double ctot = Double.parseDouble(session.getcarttotal());
                    ctot -= Double.parseDouble(cart.Price)*min;
                    session.setcarttotal(""+ctot);

                    int cqty = Integer.parseInt(session.getcartitem());
                    cqty-=min;
                    session.setcartitem(Integer.toString(cqty));

                    iname=session.getitemname("INAME");
                    iprice=session.getitemname("IPRICE");
                    int tempno=0,count=0;
                    String a1[]=new String[iname.size()];
                    a1=iname.toArray(a1);
                    for (int i=0;i<a1.length;i++) {
                        if (a1[i].contains(holder.pushid.getText().toString())) {
                            tempno=i;
                            count++;
                        }
                    }

                    iname.remove(tempno);
                    iprice.remove(tempno);
                    session.setitemname(iname,"INAME");
                    session.setitemname(iprice,"IPRICE");

                    mref.child(holder.pushid.getText().toString()).removeValue();

                }
                else{
                    holder.qty.setText("" + a);

                    double ctot = Double.parseDouble(session.getcarttotal());
                    ctot -= Double.parseDouble(cart.Price)*min;
                    session.setcarttotal(""+ctot);

                    int cqty = Integer.parseInt(session.getcartitem());
                    cqty=cqty-min;
                    session.setcartitem(Integer.toString(cqty));

                    iname=session.getitemname("INAME");
                    iprice = session.getitemname("IPRICE");
                    int tempno=0,count=0;
                    String a1[]=new String[iname.size()];
                    a1=iname.toArray(a1);
                    String b1[]=new String[iprice.size()];
                    b1=iname.toArray(b1);
                    for (int i=0;i<a1.length;i++) {
                        if (a1[i].contains(holder.pushid.getText().toString())) {
                            tempno=i;
                            count++;
                        }
                    }

                    String temp=holder.pushid.getText().toString()+":"+holder.qty.getText().toString();
                    String temp1=cart.Price+" / "+cart.Units;
                    iname.set(tempno,temp);
                    iprice.set(tempno,temp1);
                    session.setitemname(iname,"INAME");
                    session.setitemprice(iprice,"IPRICE");



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

                double ctot = Double.parseDouble(session.getcarttotal());
                ctot += Double.parseDouble(cart.Price)*min;
                session.setcarttotal(""+ctot);

                int cqty = Integer.parseInt(session.getcartitem());
                cqty=cqty+min;
                session.setcartitem(Integer.toString(cqty));

                iname=session.getitemname("INAME");
                iprice = session.getitemname("IPRICE");
                int tempno=0,count=0;
                String a1[]=new String[iname.size()];
                a1=iname.toArray(a1);
                String b1[]=new String[iprice.size()];
                b1=iname.toArray(b1);
                for (int i=0;i<a1.length;i++) {
                    if (a1[i].contains(holder.pushid.getText().toString())) {
                        tempno=i;
                        count++;
                    }
                }

                String temp=holder.pushid.getText().toString()+":"+holder.qty.getText().toString();
                String temp1=cart.Price+" / "+ cart.Units;
                iname.set(tempno,temp);
                iprice.set(tempno,temp1);
                session.setitemname(iname,"INAME");
                session.setitemprice(iprice,"IPRICE");




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

                int q=Integer.parseInt(holder.qty.getText().toString());

                double ctot = Double.parseDouble(session.getcarttotal());
                ctot -= q*Double.parseDouble(cart.Price);
                session.setcarttotal(""+ctot);

                int cqty = Integer.parseInt(session.getcartitem());
                cqty -= q;
                session.setcartitem(Integer.toString(cqty));


                int mainno=-1;

                iname=session.getitemname("INAME");
                iprice=session.getitemname("IPRICE");
                String a[]=new String[iname.size()];
                a=iname.toArray(a);
                String b[]=new String[iprice.size()];
                b=iname.toArray(b);
                for (int i=0;i<a.length;i++) {
                    if (a[i].contains(cart.PushId)) {
                        mainno = i;
                        break;
                    }
                }



                iname = session.getitemname("INAME");
                iprice = session.getitemname("IPRICE");

                iname.remove(mainno);
                iprice.remove(mainno);


                holder.qty.setText("1");

                session.setitemprice(iprice,"IPRICE");
                session.setitemname(iname,"INAME");


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



