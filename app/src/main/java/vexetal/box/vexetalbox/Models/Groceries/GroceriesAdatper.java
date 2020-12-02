package vexetal.box.vexetalbox.Models.Groceries;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

public class GroceriesAdatper extends RecyclerView.Adapter<GroceriesAdatper.ViewHolder> implements Filterable {

    private ArrayList<Grocery> Grocerys;
    private ArrayList<Grocery> Grocerysfiltered;

    ArrayList<String> iname = new ArrayList<String>();
    ArrayList<String> iprice = new ArrayList<String>();

    Session session;


    public GroceriesAdatper(ArrayList<Grocery> Grocerys) {

        this.Grocerys = Grocerys;
        this.Grocerysfiltered = Grocerys;

    }


    @NonNull
    @Override
    public GroceriesAdatper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=(View) LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row,parent,false);
        return new GroceriesAdatper.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroceriesAdatper.ViewHolder holder, int position) {
        final Grocery Grocery=Grocerysfiltered.get(position);

        session = new Session(holder.view.getContext());

        holder.name.setText(Grocery.Name);
        holder.desc.setText(Grocery.Desc);
        holder.pushid.setText(Grocery.PushId);

        if(session.getslab().equals("1")) {
            holder.units.setText("₹" + Grocery.Price + " / " + Grocery.Units);
            holder.price.setText(Grocery.Price);
            holder.min.setText(Grocery.Qty);
        }
        else  if(session.getslab().equals("2")) {
            holder.units.setText("₹" + Grocery.Price1 + " / " + Grocery.Units);
            holder.price.setText(Grocery.Price1);
            holder.min.setText(Grocery.Qty);
        }
        else  if(session.getslab().equals("3")) {
            holder.units.setText("₹" + Grocery.Price2 + " / " + Grocery.Units);
            holder.price.setText(Grocery.Price2);
            holder.min.setText(Grocery.Qty);
        }

        if(Grocery.Stock <= 0){
            holder.cardView.setAlpha(0.5f);
            holder.stock.setVisibility(View.VISIBLE);
        }
        else{
            holder.stock.setVisibility(View.GONE);
        }


        if(!TextUtils.isEmpty(Grocery.Status)) {
            if (!Grocery.Status.equals("Active")) {
                holder.cardView.setAlpha(0.5f);
                holder.stock.setVisibility(View.VISIBLE);
            }
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(20));


        Glide.with(holder.view.getContext())
                .load(Grocery.Image)
                .apply(requestOptions)
                .into(holder.image);

        int count=0;
        int selection=0;


        final Session session = new Session(holder.view.getContext());
        final ArrayList<String> spinner1 = new ArrayList<String>();
        spinner1.clear();



        int mainno=-1;

        iname=session.getitemname("INAME");
        iprice=session.getitemname("IPRICE");
        String a[]=new String[iname.size()];
        a=iname.toArray(a);
        String b[]=new String[iprice.size()];
        b=iname.toArray(b);
        for (int i=0;i<a.length;i++) {
            if (a[i].contains(Grocery.PushId)) {
                holder.add.setVisibility(View.GONE);
                holder.count1.setVisibility(View.VISIBLE);
                String a100[] = a[i].split(":");
                holder.qty.setText(a100[1]);
                mainno = i;
                break;
            }
        }



        final DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart");

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.cardView.getAlpha()==0.5f){
                    Toast.makeText(holder.view.getContext(),"Item Not available at the moment",Toast.LENGTH_LONG).show();
                    return;
                }


                holder.count1.setVisibility(View.VISIBLE);
                holder.add.setVisibility(View.GONE);
                int min=Integer.parseInt(holder.min.getText().toString());
                double ctot = Double.parseDouble(session.getcarttotal());
                holder.qty.setText(holder.min.getText().toString());
                ctot += Double.parseDouble(holder.price.getText().toString())*min;
                session.setcarttotal(""+ctot);


                session.setcartname(session.getsub());

                int cqty = Integer.parseInt(session.getcartitem());
                cqty=cqty+min;
                session.setcartitem(Integer.toString(cqty));

                iname = session.getitemname("INAME");
                iprice = session.getitemname("IPRICE");
                String temp1 = holder.pushid.getText().toString() +":"+holder.qty.getText().toString();
                iname.add(temp1);
                iprice.add(holder.price.getText().toString() + " / " + holder.units.getText().toString());
                session.setitemname(iname, "INAME");
                session.setitemname(iprice, "IPRICE");
                String z[]=holder.units.getText().toString().split(" / ");
                double tot = Double.parseDouble(holder.price.getText().toString()) * Integer.parseInt(holder.qty.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Name").setValue(holder.name.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Qty").setValue(holder.qty.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Units").setValue(z[1]);
                mref.child(holder.pushid.getText().toString()).child("Price").setValue(holder.price.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Total").setValue("" + tot);
                mref.child(holder.pushid.getText().toString()).child("PushId").setValue(holder.pushid.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Image").setValue(Grocery.Image);
                mref.child(holder.pushid.getText().toString()).child("Min").setValue(holder.min.getText().toString());

            }
        });


        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = Integer.parseInt(holder.qty.getText().toString());
                int min=Integer.parseInt(holder.min.getText().toString());
                a=a-min;
                if (a < Integer.parseInt(holder.min.getText().toString())) {
                    holder.add.setVisibility(View.VISIBLE);
                    holder.count1.setVisibility(View.GONE);

                    double ctot = Double.parseDouble(session.getcarttotal());
                    ctot -= Double.parseDouble(holder.price.getText().toString())*min;
                    session.setcarttotal(""+ctot);

                    int cqty = Integer.parseInt(session.getcartitem());
                    cqty=cqty-min;
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
                    ctot -= Double.parseDouble(holder.price.getText().toString())*min;
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
                    String temp1=holder.price.getText().toString()+" / "+holder.units.getText().toString();
                    iname.set(tempno,temp);
                    iprice.set(tempno,temp1);
                    session.setitemname(iname,"INAME");
                    session.setitemprice(iprice,"IPRICE");

                    String z[]=holder.units.getText().toString().split(" / ");

                    double tot = Double.parseDouble(holder.price.getText().toString()) * Integer.parseInt(holder.qty.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Name").setValue(holder.name.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Qty").setValue(holder.qty.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Units").setValue(z[1]);
                    mref.child(holder.pushid.getText().toString()).child("Price").setValue(holder.price.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Total").setValue("" + tot);
                    mref.child(holder.pushid.getText().toString()).child("PushId").setValue(holder.pushid.getText().toString());
                    mref.child(holder.pushid.getText().toString()).child("Image").setValue(Grocery.Image);
                    mref.child(holder.pushid.getText().toString()).child("Min").setValue(holder.min.getText().toString());
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
                ctot += Double.parseDouble(holder.price.getText().toString())*min;
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
                String temp1=holder.price.getText().toString()+" / "+holder.units.getText().toString();
                iname.set(tempno,temp);
                iprice.set(tempno,temp1);
                session.setitemname(iname,"INAME");
                session.setitemprice(iprice,"IPRICE");
                String z[]=holder.units.getText().toString().split(" / ");
                double tot = Double.parseDouble(holder.price.getText().toString()) * Integer.parseInt(holder.qty.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Name").setValue(holder.name.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Qty").setValue(holder.qty.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Units").setValue(z[1]);
                mref.child(holder.pushid.getText().toString()).child("Price").setValue(holder.price.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Total").setValue("" + tot);
                mref.child(holder.pushid.getText().toString()).child("PushId").setValue(holder.pushid.getText().toString());
                mref.child(holder.pushid.getText().toString()).child("Image").setValue(Grocery.Image);
                mref.child(holder.pushid.getText().toString()).child("Min").setValue(holder.min.getText().toString());

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
        if(Grocerysfiltered!=null){
            return Grocerysfiltered.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    Grocerysfiltered = Grocerys;
                } else {
                    ArrayList<Grocery> filteredList = new ArrayList<>();
                    for (Grocery row : Grocerys) {
                        if (row.Name.toLowerCase().contains(charString.toLowerCase()) || row.Desc.toLowerCase().contains(charSequence) ) {
                            filteredList.add(row);
                        }
                    }
                    Grocerysfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = Grocerysfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Grocerysfiltered = (ArrayList<Grocery>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public final View view;
        TextView name,desc,pushid,stock,units,qty,price,add,min;
        ImageView image;
        LinearLayout count1,minus,plus,buttons;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.desc);
            pushid = view.findViewById(R.id.pushid);
            stock = view.findViewById(R.id.stock);
            units = view.findViewById(R.id.units);
            image = view.findViewById(R.id.image);
            count1 = view.findViewById(R.id.count);
            qty = view.findViewById(R.id.qty);
            price = view.findViewById(R.id.price);
            add = view.findViewById(R.id.add);
            buttons = view.findViewById(R.id.buttons);
            minus = view.findViewById(R.id.minus);
            plus = view.findViewById(R.id.plus);
            cardView = view.findViewById(R.id.cardView);
            min = view.findViewById(R.id.min);
        }


        @Override
        public void onClick(View v) {
        }
    }

}



