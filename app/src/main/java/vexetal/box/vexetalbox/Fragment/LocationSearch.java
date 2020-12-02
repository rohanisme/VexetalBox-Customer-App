package vexetal.box.vexetalbox.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.internal.LatLngAdapter;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Models.Address.Address;
import vexetal.box.vexetalbox.Models.Address.ViewHolder;
import vexetal.box.vexetalbox.Models.PlacePredictionAdapter;
import vexetal.box.vexetalbox.R;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG;

public class LocationSearch extends Fragment {


    private EditText txtSearch;
    private RecyclerView mRecyclerView,past;
    private ImageView clear;
    private Handler handler = new Handler();
    private PlacePredictionAdapter adapter = new PlacePredictionAdapter();
    private Gson gson = new GsonBuilder().registerTypeAdapter(LatLng.class, new LatLngAdapter())
            .create();

    private RequestQueue queue;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;

    private ViewAnimator viewAnimator;
    private ProgressBar progressBar;

    private TextView map,address;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mref;
    private Session sessions;



    public LocationSearch() {
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
        View v= inflater.inflate(R.layout.fragment_location_search, container, false);


        ImageView back=v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity()!=null) {
                    LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
                    bottomnavigation.setVisibility(View.VISIBLE);
                    getActivity().onBackPressed();
                }
            }
        });


        if(getActivity()!=null) {
            LinearLayout bottomnavigation = (getActivity()).findViewById(R.id.bottomnavigation);
            bottomnavigation.setVisibility(View.GONE);
        }

//        buildGoogleApiClient();

        if (!Places.isInitialized()&&getContext()!=null) {
            Places.initialize(getContext(), getString(R.string.google_maps_api));
        }

        txtSearch=v.findViewById(R.id.txtSearch);
        mRecyclerView=v.findViewById(R.id.recyclerView);
        clear=v.findViewById(R.id.clear);
        if(getContext()!=null)
            placesClient = Places.createClient(getContext());

        progressBar = v.findViewById(R.id.progress_bar);
        viewAnimator = v.findViewById(R.id.view_animator);

        map = v.findViewById(R.id.map);
        past = v.findViewById(R.id.past);
        address = v.findViewById(R.id.address);

        address.setVisibility(View.GONE);

        past.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        map.setVisibility(View.VISIBLE);

        viewAnimator.setVisibility(View.GONE);
        clear.setVisibility(View.GONE);

        sessions= new Session(getActivity());




        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getActivity()!=null) {
                    LocationManager locationManager = (LocationManager)
                            getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        displayLocationSettingsRequest(getActivity());
                        return;
                    }
                }

                if(getActivity()!=null) {
                    Fragment fragment = new LocationDetection();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Bundle bundle=new Bundle();
                    bundle.putString("type","location");
                    fragment.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                }
            }
        });


        placesClient = com.google.android.libraries.places.api.Places.createClient(getContext());
        queue = Volley.newRequestQueue(getContext());

        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        past.setLayoutManager(layoutManager1);
        past.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager1.getOrientation()));


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));


//        adapter.setPlaceClickListener();
//        LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mRecyclerView.setAdapter(mAutoCompleteAdapter);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                sessionToken = AutocompleteSessionToken.newInstance();
            }

            @Override
            public void onTextChanged(final CharSequence s, int i, int i1, int i2) {

                past.setVisibility(View.GONE);
                address.setVisibility(View.GONE);
                map.setVisibility(View.GONE);

                viewAnimator.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);


                progressBar.setIndeterminate(true);
                // Cancel any previous place prediction requests
                handler.removeCallbacksAndMessages(null);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPlacePredictions(s.toString());
                    }
                }, 300);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSearch.setText("");

                past.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                map.setVisibility(View.VISIBLE);

                viewAnimator.setVisibility(View.GONE);
                clear.setVisibility(View.GONE);

            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mref = mFirebaseDatabase.getReference().child("Users").child(sessions.getusername()).child("Address");

        return v;
    }

    private void getPlacePredictions(String query) {


//        final LocationBias bias = RectangularBounds.newInstance(
//            new LatLng(13.590194, 77.535981), // NE lat, lng
//            new LatLng(13.622520, 77.503923) // SW lat, lng
//        );

        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        final FindAutocompletePredictionsRequest newRequest = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(sessionToken)
//            .setLocationBias(bias)
//                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setQuery(query)
                .setCountries("IN")
                .build();

        // Perform autocomplete predictions request
        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                adapter.setPredictions(predictions);

                progressBar.setIndeterminate(false);
                viewAnimator.setDisplayedChild(predictions.isEmpty() ? 0 : 1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getLocalizedMessage());
                }
            }
        });



    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Address, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Address, ViewHolder>(
                        Address.class,
                        R.layout.address_row1,
                        ViewHolder.class,
                        mref
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Address address, int position) {
                        viewHolder.setDetails(getContext(),address.Address,address.Coord,address.Flat,address.Name,address.PushId);

                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View mView, int position) {

                                TextView name,address,flat,coord,pushid,cityname;
                                name=mView.findViewById(R.id.name);
                                address=mView.findViewById(R.id.address);
                                coord=mView.findViewById(R.id.coord);
                                flat=mView.findViewById(R.id.flat);

                                sessions.setloc(coord.getText().toString());
                                sessions.setaddress(address.getText().toString());
                                sessions.setdaname(name.getText().toString());
                                sessions.sethouseno(flat.getText().toString());
                                sessions.setlocationbacground("no");


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

                                                            sessions.setslab("1");

                                                            if(getActivity()!=null) {
                                                                Fragment fragment = new Home();
                                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                fragmentManager.beginTransaction()
                                                                        .addToBackStack(null)
                                                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                                            }

                                                        }
                                                        else   if (Math.round(results[0] / 1000) <= Double.parseDouble(slab2)) {

                                                            sessions.setslab("2");

                                                            if(getActivity()!=null) {
                                                                Fragment fragment = new Home();
                                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                fragmentManager.beginTransaction()
                                                                        .addToBackStack(null)
                                                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                                                            }

                                                        }
                                                        else   if (Math.round(results[0] / 1000) <= Double.parseDouble(slab3)) {

                                                            sessions.setslab("3");

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

                            @Override
                            public void onItemLongClick(View v, int position) {

                            }
                        });
                        return viewHolder;
                    }

                    @Override
                    protected void onDataChanged() {
                        super.onDataChanged();
                        if(getItemCount()>0) {
                            address.setVisibility(View.VISIBLE);
                        }
                    }
                };

        past.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();

        txtSearch.setText("");

        past.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        map.setVisibility(View.VISIBLE);

        viewAnimator.setVisibility(View.GONE);
        clear.setVisibility(View.GONE);

    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Fragment fragment = new LocationDetection();
                        if(getActivity()!=null) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "location");
                            fragment.setArguments(bundle);
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), 0x1);
                            fragment = new LocationDetection();
                            if(getActivity()!=null) {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                Bundle bundle = new Bundle();
                                bundle.putString("type", "location");
                                fragment.setArguments(bundle);
                                fragmentManager.beginTransaction()
                                        .addToBackStack(null)
                                        .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                            }
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 0x1:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Fragment fragment = new LocationDetection();
                        if(getActivity()!=null) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "location");
                            fragment.setArguments(bundle);
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.frame_container, fragment).commitAllowingStateLoss();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("TAG", "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }


}