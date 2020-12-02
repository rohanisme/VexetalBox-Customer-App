package vexetal.box.vexetalbox.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;

public class ProfileDetails extends Fragment {


    private EditText name,number,email;
    private Button update;

    private Session session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_profile_details, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.GONE);
        }

        ImageView back = v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null)
                getActivity().onBackPressed();
            }
        });

        name = v.findViewById(R.id.name);
        number = v.findViewById(R.id.number);
        email = v.findViewById(R.id.email);
        update = v.findViewById(R.id.update);
        session = new Session(getActivity());


        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(session.getusername())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            name.setText(dataSnapshot.child("Name").getValue().toString());
                            number.setText(dataSnapshot.child("MobileNumber").getValue().toString());
                            if(dataSnapshot.child("Email").exists())
                            email.setText(dataSnapshot.child("Email").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(name.getText().toString())){
                    name.setError("Enter Name");
                    name.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Enter Emailid");
                    email.requestFocus();
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Name").setValue(name.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Email").setValue(email.getText().toString());

                session.setname(name.getText().toString());
                session.setemail(email.getText().toString());

                if(getContext()!=null) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Succesfully Updated!")
                            .show();
                    if(getActivity()!=null)
                        getActivity().onBackPressed();
                }
            }
        });


        return v;
    }
}
