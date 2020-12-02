package vexetal.box.vexetalbox.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Users;
import vexetal.box.vexetalbox.R;

public class Login extends AppCompatActivity {


    TextView login,resend,textView3;
    EditText mobilenumber,otp;
    LinearLayout l2;

    Users users;
    FirebaseUser user;

    ProgressBar progressBar;
    String status="";

    private static final String TAG = "PhoneAuthActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=findViewById(R.id.login);
        resend=findViewById(R.id.resend);
        mobilenumber=findViewById(R.id.mobilenumber);
        otp=findViewById(R.id.otp);
        textView3=findViewById(R.id.textView3);
        l2=findViewById(R.id.l2);
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        textView3.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        resend.setVisibility(View.GONE);

        session = new Session(this);


        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                Log.d(TAG, "token:" + token);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode("+91"+mobilenumber.getText().toString(), mResendToken);
                Toast.makeText(Login.this,"Otp Sent",Toast.LENGTH_SHORT).show();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(mobilenumber.getText().toString())){
                    mobilenumber.setError("Enter Mobile Number");
                    mobilenumber.requestFocus();

                    return;
                }

                if(mobilenumber.getText().toString().length()!=10){
                    mobilenumber.setError("Enter Proper Mobile Number");
                    mobilenumber.requestFocus();
                    return;
                }



                if(status.equals(""))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .orderByChild("MobileNumber").equalTo(mobilenumber.getText().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        textView3.setVisibility(View.VISIBLE);
                                        l2.setVisibility(View.VISIBLE);
                                        resend.setVisibility(View.VISIBLE);
                                        startPhoneNumberVerification("+91" + mobilenumber.getText().toString());
                                        status = "notregistered";
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else {
                                        String userid = "";
                                        for (DataSnapshot v : dataSnapshot.getChildren()) {
                                            userid = v.child("UserId").getValue().toString();
                                            break;
                                        }
                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(userid)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        users = dataSnapshot.getValue(Users.class);
                                                        textView3.setVisibility(View.VISIBLE);
                                                        l2.setVisibility(View.VISIBLE);
                                                        resend.setVisibility(View.VISIBLE);
                                                        startPhoneNumberVerification("+91" + mobilenumber.getText().toString());
                                                        progressBar.setVisibility(View.GONE);
                                                        status = "registered";
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                else
                    {
                    progressBar.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(otp.getText().toString())) {
                            otp.setError("Enter OTP");
                            otp.requestFocus();
                            return;
                        }

                        verifyPhoneNumberWithCode(mVerificationId, otp.getText().toString());
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification("+91"+mobilenumber.getText().toString());
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            // [END verify_with_code]
            signInWithPhoneAuthCredential(credential);
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(TextUtils.isEmpty(mobilenumber.getText().toString())){
                                Toast.makeText(Login.this,"Technical Error.Error Code #1200. Try after sometime or contact admin",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            user = task.getResult().getUser();


                            if(user!=null) {
                                if(status.equals("registered")){

                                    Intent intent = new Intent(Login.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    session = new Session(Login.this);
                                    session.setusername(users.UserId);
                                    session.setnumber(users.MobileNumber);
                                    session.setname(users.Name);

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart").setValue(null);
                                    session.setcartitem("0");
                                    session.setcarttotal("0");
                                    session.setcartrtotal("0");
                                    session.setcartname("");
                                    session.setloc("");
                                    session.setcityname("");
                                    session.setcitypushid("");
                                    session.setdaloc("");
                                    session.setdaname("");
                                    session.setchefcity("");
                                    session.setcheflocality("");
                                    session.setchefloc("");
                                    session.setslab("1");
                                    ArrayList<String> iname=new ArrayList<String>();
                                    iname.clear();
                                    ArrayList<String> iprice=new ArrayList<String>();
                                    iprice.clear();
                                    session.setitemname(iname,"INAME");
                                    session.setitemprice(iprice,"IPRICE");


                                    startActivity(intent);
                                    finish();

                                }
                                else{
                                    Intent intent = new Intent(Login.this, Signup.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    session = new Session(Login.this);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                otp.setError("Invalid code.");
                            }
                        }
                    }
                });
    }
    private boolean validatePhoneNumber() {
        String phoneNumber = mobilenumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mobilenumber.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

}
