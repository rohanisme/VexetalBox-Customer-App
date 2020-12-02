package vexetal.box.vexetalbox.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;


public class Home extends Fragment {

    public Home() {
        // Required empty public constructor
    }

    private EditText search;
    private CardView c1,c2,c3,c4,c5;
    private Session session;
    private boolean available;
    private String a="9:00 Am",b="9:00 Pm";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.VISIBLE);
            ImageView i1, i4, i3;
            i1 = getActivity().findViewById(R.id.i1);
            i4 = getActivity().findViewById(R.id.i4);
            i3 = getActivity().findViewById(R.id.i3);
            i1.setImageResource(R.drawable.hb1);
            i3.setImageResource(R.drawable.b3);
            i4.setImageResource(R.drawable.b4);
        }

        search=v.findViewById(R.id.search);

        InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert im != null;
        im.hideSoftInputFromWindow(search.getWindowToken(), 0);



        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount()-1; ++i) {
            fm.popBackStack();
        }


        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    System.exit(0);
                    return true;
                }
                return false;
            }
        } );




        session=new Session(getContext());

        if(!TextUtils.isEmpty(session.getextras())){
            if(session.getextras().equals("Orders"))
            {
                session.setextras("");
                if(getActivity()!=null) {
                    Fragment fragment = new OrderHistory();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
            if(session.getextras().equals("Cart"))
            {
                session.setextras("");
                if(getActivity()!=null) {
                    Fragment fragment = new Cart();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        }


        c1=v.findViewById(R.id.c1);
        c2=v.findViewById(R.id.c2);
        c3=v.findViewById(R.id.c3);
        c4=v.findViewById(R.id.c4);
        c5=v.findViewById(R.id.c5);


        try {

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            dateFormat.format(date);

            FirebaseDatabase.getInstance().getReference().child("Masters").child("Open")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                a = dataSnapshot.child("Open").getValue().toString();
                                b = dataSnapshot.child("Close").getValue().toString();


                                try {
                                    Date open = dateFormat.parse(a);
                                    Date close = dateFormat.parse(b);
                                    if ((dateFormat.parse(dateFormat.format(date)).after(open) && dateFormat.parse(dateFormat.format(date)).before(close))) {
                                        available = true;
                                    }
                                    else{
                                        available = false;
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                            else{
                                available = true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



        }
        catch (Exception e) {
            e.printStackTrace();
        }



        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(available) {
                    if (getActivity() != null) {
                        Fragment fragment = new ProductDetails();
                        session.setsub("Vegetables");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                    }
                }
                else{
                    if(getContext()!=null) {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Not Available")
                                .setContentText("We are open from "+a+"  to "+b+"  only")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                        sweetAlertDialog.setCancelable(false);
                        sweetAlertDialog.show();
                    }
                }
            }
        });


        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(available){
                if(getActivity()!=null) {
                    Fragment fragment = new ProductDetails();
                    session.setsub("English");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
                else{
                if(getContext()!=null) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Not Available")
                            .setContentText("We are open from "+a+"  to "+b+"  only")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();
                }
            }
            }
        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(available){
                if(getActivity()!=null) {
                    Fragment fragment = new ProductDetails();
                    session.setsub("Fruits");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
                }
                else{
                    if(getContext()!=null) {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Not Available")
                                .setContentText("We are open from "+a+"  to "+b+"  only")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                        sweetAlertDialog.setCancelable(false);
                        sweetAlertDialog.show();
                    }
                }
            }
        });

        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(available){
                   if(getActivity()!=null) {
                            Fragment fragment = new ProductDetails();
                            session.setsub("Essentials");
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                    }
                }
                else{
                    if(getContext()!=null) {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Not Available")
                                .setContentText("We are open from "+a+"  to "+b+"  only")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                        sweetAlertDialog.setCancelable(false);
                        sweetAlertDialog.show();
                    }
                }
            }
        });

        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(available) {
                    if (getActivity() != null) {
                        Fragment fragment = new ProductDetails();
                        session.setsub("Leafy");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                    }
                }
                else{
                    if(getContext()!=null) {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Not Available")
                                .setContentText("We are open from "+a+"  to "+b+"  only")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                        sweetAlertDialog.setCancelable(false);
                        sweetAlertDialog.show();
                    }
                }
            }
        });




        return v;
    }
}
