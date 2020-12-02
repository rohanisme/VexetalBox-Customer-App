package vexetal.box.vexetalbox.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import vexetal.box.vexetalbox.Activities.Login;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;

import static android.app.Activity.RESULT_OK;


public class Profile extends Fragment {


    private CircleImageView pp;
    private TextView name,number;
    private LinearLayout l1,l2,l3,l4,l5,l6,l20,l23;
    private Session session;

    private Uri imageUri;
    private Uri imageHoldUri = null;
    private static final int REQUEST_CAMERA = 3;
    private static final int REQUEST_CODE = 5;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA_ACCESS_PERMISSION = 5674;
    private static final int SELECT_FILE = 2;
    private final int RESULT_CROP = 400;
    private StorageReference mstorageReference;
    private ProgressBar progressBar2;

    public Profile() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_profile, container, false);

        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.VISIBLE);
        }


        if(getActivity()!=null) {
            ImageView i1, i4, i3;
            i1 = getActivity().findViewById(R.id.i1);
            i4 = getActivity().findViewById(R.id.i4);
            i3 = getActivity().findViewById(R.id.i3);

            i1.setImageResource(R.drawable.b1);
            i3.setImageResource(R.drawable.b3);
            i4.setImageResource(R.drawable.hb4);
        }

        mstorageReference= FirebaseStorage.getInstance().getReference();


        name=v.findViewById(R.id.name);
        number=v.findViewById(R.id.number);
        pp=v.findViewById(R.id.pp);
        l1=v.findViewById(R.id.l1);
        l2=v.findViewById(R.id.l2);
        l3=v.findViewById(R.id.l3);
        l4=v.findViewById(R.id.l4);
        l5=v.findViewById(R.id.l5);
        l6=v.findViewById(R.id.l6);
        l20=v.findViewById(R.id.l20);
        l23=v.findViewById(R.id.l23);
        session=new Session(getActivity());

        if(!TextUtils.isEmpty(session.getpp())) {
            Glide.with(getActivity())
                    .load(session.getpp())
                    .into(pp);
        }

        name.setText(session.getname());
        number.setText(session.getnumber());


        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new ProfileDetails();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        l23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new SavedAddress();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new OrderHistory();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new Coins();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new Feedback();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new AboutUs();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        l20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    Fragment fragment = new TransactionHistory();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });

        l6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setusername("");
                session.settoken("");
                session.setname("");
                session.setpp("");
                session.setpassword("");
                session.setextras("");
                session.setnumber("");
                session.setpincode("");
                session.setsub("");
                session.setrange("");
                session.settoken("");
                session.setcart("");
                session.setdaaddress("");
                session.setdadist("");
                session.setdaf("");
                session.setdal("");
                session.setdaloc("");
                session.setdaname("");
                session.setcityname("");
                session.setcitypushid("");
                session.setdaloc("");
                session.setdaname("");


                FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Cart").setValue(null);
                session.setcartitem("0");
                session.setcarttotal("0");
                session.setcartrtotal("0");
                session.setcartname("");
                session.setchefcity("");
                session.setcheflocality("");
                session.setchefloc("");
                ArrayList<String> iname=new ArrayList<String>();
                iname.clear();
                session.setitemname(iname,"INAME");


                startActivity(new Intent(getContext(),
                        Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                if(getActivity()!=null)
                    getActivity().finish();
            }
        });


        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = { "Choose from Library",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("Add Photo!");

                //SET ITEMS AND THERE LISTENERS
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Take Photo")) {
                            if(getContext()!=null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                            REQUEST_CAMERA_ACCESS_PERMISSION);
                                } else {
                                    cameraIntent();
                                }
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



        return v;
    }


    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "MyPicture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
        if(getActivity()!=null)
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
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
                session = new Session(getActivity());
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                String path = "Users/" + session.getusername() + ".jpg";
                StorageReference riversRef = mstorageReference.child(path);
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
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

                                storageRef.child("Users/" + session.getusername() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        u[0] = uri.toString();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername());
                                        ref.child("PP").setValue(u[0]);

                                        session.setpp(u[0]);

//                                                ImageView Hpp=(ImageView)getActivity().findViewById(R.id.Hpp);
                                        if(getContext()!=null)
                                            Glide.with(getContext()).load(u[0]).into(pp);
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
                                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "File Path Null", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {


            imageHoldUri = imageUri;

            Toast.makeText(getContext(),""+imageHoldUri,Toast.LENGTH_LONG).show();

            if (imageHoldUri != null) {
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                String path = "Users/" + session.getusername() + ".jpg";
                StorageReference riversRef = mstorageReference.child(path);
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
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

                                storageRef.child("Users/" + session.getusername() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        u[0] = uri.toString();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername());
                                        ref.child("PP").setValue(u[0]);

                                        session.setpp(u[0]);

//                                                ImageView Hpp=(ImageView)getActivity().findViewById(R.id.Hpp);
                                        if(getContext()!=null)
                                            Glide.with(getContext()).load(u[0]).into(pp);
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
                                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
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



    @Override
    public void onResume(){
        super.onResume();
    }
}
