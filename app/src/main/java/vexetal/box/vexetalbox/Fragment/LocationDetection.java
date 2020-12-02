package vexetal.box.vexetalbox.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.R;

public class LocationDetection extends Fragment
        implements
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener {


    public LocationDetection() {
        // Required empty public constructor
    }

    private String pincode="";
    private GoogleMap mMap;
    private ImageView pin;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;



    private LocationRequest mLocationRequest;
    private int temp=0;

    private String city="",type="";

    private TextView location,coord;
    private EditText flat,name;
    private Button save;
    private Session session;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location_detection, container, false);


        if(getActivity()!=null) {
            Toolbar toolbar = (getActivity()).findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }


        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.GONE);
        }

        pin=v.findViewById(R.id.pin);
        session=new Session(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        if(getActivity()!=null) {
//            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            assert imm != null;
//            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//        }

        if(getArguments()!=null) {
            type = getArguments().getString("type");
        }

        ImageView back=v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null)
                    getActivity().onBackPressed();
            }
        });

        save=v.findViewById(R.id.save);
        location=v.findViewById(R.id.location);
        coord=v.findViewById(R.id.coord);
        flat=v.findViewById(R.id.flat);
        name=v.findViewById(R.id.name);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if(getContext()!=null) {
            Places.initialize(getContext(), "AIzaSyAo4tQpXz_kP166-72ugd5sc9b94l8LVzs");
            placesClient = Places.createClient(getContext());
        }
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();





        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(location.getText().toString().equals("LOCATING.....!"))
                {
                    Toast.makeText(getContext(),"Please Wait",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(flat.getText().toString())){
                    flat.setError("Enter Flat/House Number");
                    flat.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(name.getText().toString())){
                    name.setError("Enter Shop Name");
                    name.requestFocus();
                    return;
                }


                FirebaseDatabase.getInstance().getReference().child("Masters").child("Radius")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    String coordindates=dataSnapshot.child("Coord").getValue().toString();
                                    String slab1=dataSnapshot.child("Slab1").getValue().toString();
                                    String slab2=dataSnapshot.child("Slab2").getValue().toString();
                                    String slab3=dataSnapshot.child("Slab3").getValue().toString();

                                    float[] results = new float[1];
                                    String r[]=coordindates.split(",");
                                    String s[]=coord.getText().toString().split(",");

                                    try {
                                        Location.distanceBetween(Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(r[0]), Double.parseDouble(r[1]), results);
                                        if (Math.round(results[0] / 1000) <= Double.parseDouble(slab1)) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Address").push();
                                            ref.child("PushId").setValue(ref.getKey());
                                            ref.child("Address").setValue(location.getText().toString());
                                            ref.child("Flat").setValue(flat.getText().toString());
                                            ref.child("Name").setValue(name.getText().toString());
                                            ref.child("Coord").setValue(coord.getText().toString());
                                            ref.child("Pincode").setValue(pincode);

                                            session.setloc(coord.getText().toString());
                                            session.setaddress(location.getText().toString());
                                            session.setdaname(name.getText().toString());
                                            session.sethouseno(flat.getText().toString());
                                            session.setcityname(pincode);
                                            session.setlocationbacground("no");
                                            session.setslab("1");

                                            if(getActivity()!=null) {
                                                Fragment fragment = new Home();
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                fragmentManager.beginTransaction()
                                                        .addToBackStack(null)
                                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                            }

                                        }
                                        else   if (Math.round(results[0] / 1000) <= Double.parseDouble(slab2)) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Address").push();
                                            ref.child("PushId").setValue(ref.getKey());
                                            ref.child("Address").setValue(location.getText().toString());
                                            ref.child("Flat").setValue(flat.getText().toString());
                                            ref.child("Name").setValue(name.getText().toString());
                                            ref.child("Coord").setValue(coord.getText().toString());
                                            ref.child("Pincode").setValue(pincode);

                                            session.setloc(coord.getText().toString());
                                            session.setaddress(location.getText().toString());
                                            session.setdaname(name.getText().toString());
                                            session.sethouseno(flat.getText().toString());
                                            session.setcityname(pincode);
                                            session.setlocationbacground("no");
                                            session.setslab("2");

                                            if(getActivity()!=null) {
                                                Fragment fragment = new Home();
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                fragmentManager.beginTransaction()
                                                        .addToBackStack(null)
                                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                            }

                                        }
                                        else   if (Math.round(results[0] / 1000) <= Double.parseDouble(slab3)) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(session.getusername()).child("Address").push();
                                            ref.child("PushId").setValue(ref.getKey());
                                            ref.child("Address").setValue(location.getText().toString());
                                            ref.child("Flat").setValue(flat.getText().toString());
                                            ref.child("Name").setValue(name.getText().toString());
                                            ref.child("Coord").setValue(coord.getText().toString());
                                            ref.child("Pincode").setValue(pincode);

                                            session.setloc(coord.getText().toString());
                                            session.setaddress(location.getText().toString());
                                            session.setdaname(name.getText().toString());
                                            session.sethouseno(flat.getText().toString());
                                            session.setcityname(pincode);
                                            session.setlocationbacground("no");
                                            session.setslab("3");

                                            if(getActivity()!=null) {
                                                Fragment fragment = new Home();
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                fragmentManager.beginTransaction()
                                                        .addToBackStack(null)
                                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                            }

                                        }
                                        else{
                                            if(getContext()!=null) {
                                                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                                        .setTitleText("Not Serviceable!")
                                                        .setContentText("Currently we don't offer any services in the location selected")
                                                        .show();
                                            }
                                        }
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                                else{
                                    if(getContext()!=null) {
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Not Serviceable!")
                                                .setContentText("Currently we don't offer any services in the location selected")
                                                .show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
        });



        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        Initialize Google Play Services
        if(getContext()!=null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //Location Permission already granted
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
//
                } else {
                    //Request Location Permission
                    checkLocationPermission();
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker")
                .draggable(true));
    }

    protected synchronized void buildGoogleApiClient() {
        if(getContext()!=null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(getContext()!=null) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        if(temp==0) {
            mMap.clear();
            temp++;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            mMap.setMapType(mMap.MAP_TYPE_NORMAL);
        }


    }


    @Override
    public void onCameraMove() {

        mMap.clear();
        pin.setVisibility(View.VISIBLE);

    }


    @Override
    public void onCameraIdle() {

        pin.setVisibility(View.GONE);
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(mMap.getCameraPosition().target)
                .title("Marker"));


        String address="";
        String address1="";
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = gcd.getFromLocation(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            address=addresses.get(0).getAddressLine(0);
            city=addresses.get(0).getLocality();
            location.setText(address);
            pincode=addresses.get(0).getPostalCode();
            coord.setText(""+mMap.getCameraPosition().target.latitude+","+mMap.getCameraPosition().target.longitude);
        }

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if(getContext()!=null) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (getActivity() != null) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        new AlertDialog.Builder(getContext())
                                .setTitle("Location Permission Needed")
                                .setMessage("This app needs the Location permission, please accept to use location functionality")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                                        if (getActivity() != null) {
                                            ActivityCompat.requestPermissions(getActivity(),
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    MY_PERMISSIONS_REQUEST_LOCATION);
                                        }
                                    }
                                })
                                .create()
                                .show();


                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }
            }
        }
    }
}
