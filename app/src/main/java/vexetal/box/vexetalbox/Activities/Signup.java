package vexetal.box.vexetalbox.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;

public class Signup extends AppCompatActivity {

    TextView login,number;
    EditText name,email,refer,gst;
    ImageView upload,upload1,uploadsuccess,uploadsuccess1;
    Session session;
    int selection=-1;
    String uniqueid = "VB",path1="",path2="";
    Uri imageUri;
    Uri imageHoldUri = null;
    private static final int REQUEST_CAMERA = 3;
    private static final int REQUEST_CODE = 5;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA_ACCESS_PERMISSION = 5674;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final int SELECT_FILE = 2;
    private final int RESULT_CROP = 400;
    private StorageReference mstorageReference;
    long id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        login=findViewById(R.id.login);
        number=findViewById(R.id.number);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        refer = findViewById(R.id.refer);
        gst = findViewById(R.id.gst);
        upload = findViewById(R.id.upload);
        upload1 = findViewById(R.id.upload1);
        uploadsuccess = findViewById(R.id.uploadsuccess);
        uploadsuccess1 = findViewById(R.id.uploadsuccess1);
        session=new Session(this);
        uploadsuccess.setVisibility(View.GONE);
        uploadsuccess1.setVisibility(View.GONE);
        mstorageReference= FirebaseStorage.getInstance().getReference();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Take Photo",  "Choose from Library",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this, AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("Add Photo!");
                //SET ITEMS AND THERE LISTENERS
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        selection=0;
                        if (items[item].equals("Take Photo")) {
                                cameraIntent();
                        } else if (items[item].equals("Choose from Library")) {
                            galleryIntent();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        upload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Take Photo", "Choose from Library",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this, AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("Add Photo!");
                //SET ITEMS AND THERE LISTENERS
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        selection=1;
                        if (items[item].equals("Take Photo")) {
                            if (items[item].equals("Take Photo")) {
                                  cameraIntent();
                            }
                        } else if (items[item].equals("Choose from Library")) {
                            galleryIntent();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        String pattern = "yyyy-MM-dd";
        final String dateInString = new SimpleDateFormat(pattern).format(new Date());

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "+919900005605";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login.setEnabled(false);

                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("Enter Full Name");
                    name.requestFocus();
                    login.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(path2)) {
                    Toast.makeText(getApplicationContext(),"Upload Shop Image",Toast.LENGTH_LONG).show();
                    login.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(refer.getText().toString())) {
                    refer.setError("Enter Referral Code");
                    refer.requestFocus();
                    login.setEnabled(true);
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("SalesAgents").child(refer.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("UniqueId");
                                    dref.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                            double value = 0;
                                            if (currentData.getValue() != null) {
                                                value = Long.parseLong(currentData.getValue().toString()) + 1;
                                                id = Long.parseLong(currentData.getValue().toString()) + 1;
                                            }
                                            currentData.setValue(value);


                                            return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {


                                            if (id > 0 && id < 10) {
                                                uniqueid += "000000000" + id;
                                            } else if (id >= 10 && id < 100) {
                                                uniqueid += "00000000" + id;
                                            } else if (id >= 100 && id < 1000) {
                                                uniqueid += "0000000" + id;
                                            } else if (id >= 1000 && id < 10000) {
                                                uniqueid += "000000" + id;
                                            } else if (id >= 10000 && id < 100000) {
                                                uniqueid += "00000" + id;
                                            } else if (id >= 100000 && id < 1000000) {
                                                uniqueid += "0000" + id;
                                            } else if (id >= 1000000 && id < 10000000) {
                                                uniqueid += "000" + id;
                                            } else if (id >= 10000000 && id < 100000000) {
                                                uniqueid += "00" + id;
                                            } else if (id >= 100000000 && id < 1000000000) {
                                                uniqueid += "0" + id;
                                            } else {
                                                uniqueid += id;
                                            }


                                            DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(uniqueid);
                                            mref.child("Name").setValue(name.getText().toString());
                                            mref.child("MobileNumber").setValue(user.getPhoneNumber().substring(3));
                                            mref.child("UserId").setValue(uniqueid);
                                            mref.child("Email").setValue(email.getText().toString());
                                            mref.child("Referral").setValue(refer.getText().toString());
                                            mref.child("Status").setValue("Active");
                                            mref.child("Role").setValue("User");
                                            mref.child("Coins").setValue(0);
                                            mref.child("Gst").setValue(gst.getText().toString());
                                            mref.child("GstImage").setValue(path1);
                                            mref.child("ShopImage").setValue(path2);
                                            mref.child("JoiningDate").setValue(dateInString);

                                            session.setusername(uniqueid);
                                            session.setnumber(user.getPhoneNumber().substring(3));
                                            session.setname(name.getText().toString());
                                            session.setreferral(refer.getText().toString());

                                            FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart").setValue(null);
                                            session.setcartitem("0");
                                            session.setcarttotal("0");
                                            session.setcartrtotal("0");
                                            session.setcartname("");
                                            session.setloc("");
                                            session.setslab("1");
                                            session.setcityname("");
                                            session.setcitypushid("");
                                            session.setdaloc("");
                                            session.setdaname("");
                                            session.setchefcity("");
                                            session.setcheflocality("");
                                            session.setchefloc("");
                                            ArrayList<String> iname=new ArrayList<String>();
                                            iname.clear();
                                            ArrayList<String> iprice=new ArrayList<String>();
                                            iprice.clear();
                                            session.setitemname(iname,"INAME");
                                            session.setitemprice(iprice,"IPRICE");



                                            Intent intent = new Intent(Signup.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();

                                        }
                                    });
                                }
                                else{
                                    refer.setError("Enter Valid Referral Code");
                                    refer.requestFocus();
                                    login.setEnabled(true);
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



            }
        });

    }

    private void cameraIntent() {
        requestMultiplePermissions();
    }


    private void galleryIntent() {

        //CHOOSE IMAGE FROM GALLERY
//        Log.d("gola", "entered here");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //SAVE URI FROM GALLERY
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            imageHoldUri = data.getData();

            if (imageHoldUri != null) {
                session = new Session(Signup.this);
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                String path = "Users/" + c + ".jpg";
                StorageReference riversRef = mstorageReference.child(path);
                final ProgressDialog progressDialog = new ProgressDialog(Signup.this);
                progressDialog.setTitle("Updating....!");
                progressDialog.show();
                progressDialog.setCancelable(false);
                riversRef.putFile(imageHoldUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                session.setpp("");
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();


                                final String[] u = new String[1];

                                storageRef.child("Users/" + c + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        u[0] = uri.toString();
                                        if(selection==0) {
                                            path1 = u[0];
                                            uploadsuccess.setVisibility(View.VISIBLE);
                                        }
                                        else if(selection==1){
                                            path2 = u[0];
                                            uploadsuccess1.setVisibility(View.VISIBLE);
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });


                                progressDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(Signup.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage((int) progress + "%Uploaded");
                            }
                        });

            } else {
                Toast.makeText(Signup.this, "File Path Null", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {


            imageHoldUri = imageUri;

            Toast.makeText(Signup.this,""+imageHoldUri,Toast.LENGTH_LONG).show();

            if (imageHoldUri != null) {
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                String path = "Users/" + c + ".jpg";
                StorageReference riversRef = mstorageReference.child(path);
                final ProgressDialog progressDialog = new ProgressDialog(Signup.this);
                progressDialog.setTitle("Updating....!");
                progressDialog.show();
                progressDialog.setCancelable(false);
                riversRef.putFile(imageHoldUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();


                                final String[] u = new String[1];

                                storageRef.child("Users/" + c + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        u[0] = uri.toString();
                                        if(selection==0) {
                                            path1 = u[0];
                                            uploadsuccess.setVisibility(View.VISIBLE);
                                        }
                                        else if(selection==1){
                                            path2 = u[0];
                                            uploadsuccess1.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });


                                progressDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(Signup.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage((int) progress + "%Uploaded");
                            }
                        });
            }
        }
    }


    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {  // check if all permissions are granted

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, CAMERA_REQUEST_CODE);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) { // check for permanent denial of any permission
                            // show alert dialog navigating to Settings
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(Signup.this, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    @Override
    public void onResume(){
        super.onResume();
    }


}
