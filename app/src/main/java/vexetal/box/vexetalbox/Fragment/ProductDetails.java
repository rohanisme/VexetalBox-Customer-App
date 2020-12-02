package vexetal.box.vexetalbox.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Groceries.GroceriesAdatper;
import vexetal.box.vexetalbox.Models.Groceries.Grocery;
import vexetal.box.vexetalbox.R;


public class ProductDetails extends Fragment {

    public ProductDetails() {
        // Required empty public constructor
    }

    private Session session;
    private RecyclerView mRecyclerView,mRecyclerView1;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mref;
    private ShimmerFrameLayout mShimmerViewContainer;
    private EditText search;
    private ImageView clear;
    private LinearLayout floatingbar;
    private TextView items,itemsprice,viewcart;
    private ProgressBar progressBar;
    private GroceriesAdatper productsAdapter;
    private GroceriesAdatper productsAdapter1;
    private ArrayList<Grocery> products=new ArrayList<Grocery>();
    private ArrayList<Grocery> products1=new ArrayList<Grocery>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_product_details, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.VISIBLE);
        }

//        ImageView back = v.findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(getActivity()!=null) {
//                    getActivity().onBackPressed();
//                }
//            }
//        });

//        TextView category=v.findViewById(R.id.category);
        session = new Session(getContext());

//        if(session.getsub().equals("Vegetables")){
//            category.setText("Vegetables");
//        }
//        else if(session.getsub().equals("Mangoes")){
//            category.setText("Mangoes");
//        }
//        else if(session.getsub().equals("Pot")){
//            category.setText("Pot");
//        }
//        else if(session.getsub().equals("Leafy")){
//            category.setText("Leafy");
//        }
//        else if(session.getsub().equals("English")){
//            category.setText("English");
//        }
//        else if(session.getsub().equals("IndianRoot")){
//            category.setText("IndianRoot");
//        }
//        else if(session.getsub().equals("IndianVegetables")){
//            category.setText("IndianVegetables");
//        }
//        else if(session.getsub().equals("Fruits")){
//            category.setText("Fruits");
//        }
//        else if(session.getsub().equals("Imported")){
//            category.setText("Imported");
//        }
//        else if(session.getsub().equals("Essentials")){
//            category.setText("Essentials");
//        }


        mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);

        floatingbar=v.findViewById(R.id.floatingbar);
        items=v.findViewById(R.id.items);
        itemsprice=v.findViewById(R.id.itemsprice);
        search=v.findViewById(R.id.txtSearch);
        clear=v.findViewById(R.id.clear);
        mRecyclerView=v.findViewById(R.id.recyclerView);
        mRecyclerView1=v.findViewById(R.id.recyclerView1);
        clear.setVisibility(View.GONE);

        progressBar=v.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        mShimmerViewContainer.startShimmerAnimation();

        FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            floatingbar.setVisibility(View.VISIBLE);
                            items.setText(session.getcartitem()+"Items");
                            itemsprice.setText("\u20b9"+session.getcarttotal());
                        }
                        else{
                            floatingbar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        TextView viewcart=v.findViewById(R.id.viewcart);

        viewcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(sessions.getusername()).child("Cart");
//                ref.child("Quantity").setValue(sessions.getcartitem());
//                ref.child("Total").setValue(sessions.getcarttotal());
//                ref.child("TotalItems").setValue(sessions.getitemname("INAME"));

                if(getActivity()!=null) {
                    Fragment fragment = new Cart();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mFirebaseDatabase.getReference().child("Seller");

        products.clear();
        productsAdapter = new GroceriesAdatper(products);

        products1.clear();
        productsAdapter1 = new GroceriesAdatper(products1);

//        FirebaseDatabase.getInstance().getReference().child("Seller")
//                .orderByChild("SellerType").equalTo(session.getsub())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            for(DataSnapshot v: dataSnapshot.getChildren()){
//                                products.add(new Grocery(
//                                        v.child("Name").getValue().toString(),
//                                        v.child("KName").getValue().toString(),
//                                        v.child("Desc").getValue().toString(),
//                                        v.child("PushId").getValue().toString(),
//                                        v.child("Image").getValue().toString(),
//                                        v.child("Units").getValue().toString(),
//                                        v.child("Category").getValue().toString(),
//                                        v.child("CategoryName").getValue().toString(),
//
//                                        v.child("W1").getValue().toString(),
//                                        v.child("W2").getValue().toString(),
//                                        v.child("W3").getValue().toString(),
//                                        v.child("W4").getValue().toString(),
//                                        v.child("W5").getValue().toString(),
//                                        v.child("W6").getValue().toString(),
//                                        v.child("W7").getValue().toString(),
//                                        v.child("W8").getValue().toString(),
//
//                                        Integer.parseInt(v.child("S1").getValue().toString()),
//                                        Integer.parseInt(v.child("S2").getValue().toString()),
//                                        Integer.parseInt(v.child("S3").getValue().toString()),
//                                        Integer.parseInt(v.child("S4").getValue().toString()),
//                                        Integer.parseInt(v.child("S5").getValue().toString()),
//                                        Integer.parseInt(v.child("S6").getValue().toString()),
//                                        Integer.parseInt(v.child("S7").getValue().toString()),
//                                        Integer.parseInt(v.child("S8").getValue().toString())));
//
//
//                            }
//
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                            mRecyclerView.setLayoutManager(mLayoutManager);
//                            productsAdapter = new GroceriesAdatper(products);
//                            mRecyclerView.setAdapter(productsAdapter);
//                            new Handler().postDelayed(new Runnable() {
//                                public void run() {
//                                    mShimmerViewContainer.stopShimmerAnimation();
//                                    mShimmerViewContainer.setVisibility(View.GONE);
//                                    mRecyclerView.setVisibility(View.VISIBLE);
//                                }
//                            }, 250);
//                        }
//                        else{
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                            mRecyclerView.setLayoutManager(mLayoutManager);
//                            productsAdapter = new GroceriesAdatper(products);
//                            mRecyclerView.setAdapter(productsAdapter);
//                            new Handler().postDelayed(new Runnable() {
//                                public void run() {
//                                    mShimmerViewContainer.stopShimmerAnimation();
//                                    mShimmerViewContainer.setVisibility(View.GONE);
//                                    mRecyclerView.setVisibility(View.VISIBLE);
//                                }
//                            }, 250);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                productsAdapter.getFilter().filter(charSequence);
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView1.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);

                if(charSequence.length()==0){
                    if(getActivity()!=null) {
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                    clear.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search.setText("");
                clear.setVisibility(View.GONE);
            }
        });

        return v;
    }

    @Override
    public  void onResume() {
        super.onResume();


        mShimmerViewContainer.startShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView1.setVisibility(View.GONE);


        products.clear();
        productsAdapter = new GroceriesAdatper(products);

        FirebaseDatabase.getInstance().getReference().child("Products")
//                .orderByChild("Category").equalTo(session.getsub())
                .orderByChild("Name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            products.clear();
                            for(DataSnapshot v: dataSnapshot.getChildren()){

                                if(v.exists()) {
                                    String a1="",a2="",a3="",a4="",a5="",a6="",a7="",a8="",a9="",a10="",a11="",a12="";
                                    double a13=0;
                                    Boolean s=true;

                                    if(v.child("Name").exists())
                                        a1=v.child("Name").getValue().toString();
                                    else
                                        continue;
                                    if(v.child("Desc").exists())
                                        a2=v.child("Desc").getValue().toString();
                                    if(v.child("PushId").exists())
                                        a3=v.child("PushId").getValue().toString();
                                    if(v.child("Image").exists())
                                        a4=v.child("Image").getValue().toString();
                                    if(v.child("Units").exists())
                                        a5=v.child("Units").getValue().toString();
                                    if(v.child("Category").exists())
                                        a6=v.child("Category").getValue().toString();
                                    if(v.child("CategoryName").exists())
                                        a7=v.child("CategoryName").getValue().toString();
                                    if(v.child("Qty").exists())
                                        a8=v.child("Qty").getValue().toString();
                                    if(v.child("Price").exists())
                                        a9=v.child("Price").getValue().toString();
                                    if(v.child("Price1").exists())
                                        a10=v.child("Price1").getValue().toString();
                                    if(v.child("Price2").exists())
                                        a11=v.child("Price2").getValue().toString();
                                    if(v.child("Status").exists())
                                        a12=v.child("Status").getValue().toString();
                                    if(v.child("Stock").exists())
                                        a13=Double.parseDouble(v.child("Stock").getValue().toString());

                                    if(a13<=0){
                                        s=false;
                                    }

                                    if(a12.equals("InActive")){
                                        s=false;
                                    }

                                    if(s) {
                                        products.add(new Grocery(
                                                a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13
                                        ));
                                    }
                                    else{
                                        products1.add(new Grocery(
                                                a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13
                                        ));
                                    }
                                }


                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            productsAdapter = new GroceriesAdatper(products);
                            mRecyclerView.setAdapter(productsAdapter);

                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getContext());
                            mRecyclerView1.setLayoutManager(mLayoutManager1);
                            productsAdapter1 = new GroceriesAdatper(products1);
                            mRecyclerView1.setAdapter(productsAdapter1);

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    mShimmerViewContainer.stopShimmerAnimation();
                                    mShimmerViewContainer.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mRecyclerView1.setVisibility(View.VISIBLE);
                                }
                            }, 250);
                        }
                        else{
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            productsAdapter = new GroceriesAdatper(products);
                            mRecyclerView.setAdapter(productsAdapter);

                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getContext());
                            mRecyclerView1.setLayoutManager(mLayoutManager1);
                            productsAdapter1 = new GroceriesAdatper(products1);
                            mRecyclerView1.setAdapter(productsAdapter1);

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    mShimmerViewContainer.stopShimmerAnimation();
                                    mShimmerViewContainer.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mRecyclerView1.setVisibility(View.VISIBLE);
                                }
                            }, 250);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
