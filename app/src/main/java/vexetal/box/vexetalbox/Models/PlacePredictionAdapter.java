package vexetal.box.vexetalbox.Models;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.internal.LatLngAdapter;

import java.util.ArrayList;
import java.util.List;

import vexetal.box.vexetalbox.Activities.MainActivity;
import vexetal.box.vexetalbox.Configuration.Session;
import vexetal.box.vexetalbox.Fragment.Location;
import vexetal.box.vexetalbox.R;


public class PlacePredictionAdapter extends RecyclerView.Adapter<PlacePredictionAdapter.PlacePredictionViewHolder> {

    private final List<AutocompletePrediction> predictions = new ArrayList<>();

    private Gson gson = new GsonBuilder().registerTypeAdapter(LatLng.class, new LatLngAdapter())
            .create();


    @NonNull
    @Override
    public PlacePredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PlacePredictionViewHolder(
                inflater.inflate(R.layout.search_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PlacePredictionViewHolder holder, int position) {
        final AutocompletePrediction prediction = predictions.get(position);
        holder.setPrediction(prediction);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   String pid=prediction.getPlaceId();

                Session session=new Session(holder.view);

                MainActivity mainActivity = (MainActivity) holder.view;

                    Fragment fragment = new Location();
                    Bundle bundle = new Bundle();
                    bundle.putString("placeid", pid);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frame_container, fragment).commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public void setPredictions(List<AutocompletePrediction> predictions) {
        this.predictions.clear();
        this.predictions.addAll(predictions);
        notifyDataSetChanged();
    }


    public static class PlacePredictionViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        public final Context view;
        private final TextView title;
        private final TextView address;
        private final LinearLayout linearLayout;

        public PlacePredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView.getContext();
            title = itemView.findViewById(R.id.address);
            address = itemView.findViewById(R.id.description);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }

        public void setPrediction(AutocompletePrediction prediction) {
            title.setText(prediction.getPrimaryText(null));
            address.setText(prediction.getSecondaryText(null));
        }

        @Override
        public void onClick(View v) {
        }

    }

}
