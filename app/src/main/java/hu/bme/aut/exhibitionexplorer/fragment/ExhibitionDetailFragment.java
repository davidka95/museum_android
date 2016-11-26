package hu.bme.aut.exhibitionexplorer.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;

/**
 * Created by Adam on 2016. 10. 29..
 */

public class ExhibitionDetailFragment extends Fragment {

    public static final String TAG = "ExhibitionDetailFragment";

    private Exhibition exhibition;

    private OnSelectExhibitionListener listener;

    private FloatingActionButton fabSelect;

    private KenBurnsView ivExhibitionImage;

    private TextView tvExhibitionTitle;
    private TextView tvDate;
    private TextView tvLocation;
    private TextView tvIBeacons;
    private TextView tvQrCode;
    private TextView tvQuiz;
    private TextView tvDescription;

    private TextView tvShowMap;

    private MapView mapView;
    private GoogleMap map;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof OnSelectExhibitionListener) {
            listener = (OnSelectExhibitionListener) activity;
        } else {
            throw new RuntimeException("Activity must implement OnSelectExhibitionListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exhibition_detail, container, false);

        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        exhibition = getArguments().getParcelable(Exhibition.KEY_EXHIBITION_PARCELABLE);
        if (exhibition != null) {
            initView(rootView);
        }

        return rootView;
    }

    private void initView(View rootView) {
        fabSelect = (FloatingActionButton) rootView.findViewById(R.id.fabSelect);
        fabSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectExhibition(exhibition);
            }
        });

        tvShowMap = (TextView) rootView.findViewById(R.id.tvShowMap);
        tvShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapView.getVisibility() != View.VISIBLE){
                    tvShowMap.setVisibility(View.GONE);
                    mapView.setVisibility(View.VISIBLE);
                }
            }
        });
        ivExhibitionImage = (KenBurnsView) rootView.findViewById(R.id.kbvExhibitionImage);
        tvExhibitionTitle = (TextView) rootView.findViewById(R.id.tvExhibitionTitle);
        tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        tvLocation = (TextView) rootView.findViewById(R.id.tvLocation);
        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);
        tvIBeacons = (TextView) rootView.findViewById(R.id.tvIBeacons);
        tvQrCode = (TextView) rootView.findViewById(R.id.tvQrCode);
        tvQuiz = (TextView) rootView.findViewById(R.id.tvQuiz);


        loadDataToViews();
    }

    private void loadDataToViews() {
        Picasso.with(getContext()).load(exhibition.getImageURL()).fit().centerCrop()
                .placeholder(R.drawable.loading_animation).into(ivExhibitionImage);

        FirebaseDatabase.getInstance().getReference("museums").child(String.valueOf(exhibition.getInMuseum()))
                .child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String address = dataSnapshot.getValue(String.class);
                tvLocation.setText(getString(R.string.parameter_location_text, address));

                setMapMarker(address);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvExhibitionTitle.setText(exhibition.getName());
        tvDate.setText(getString(R.string.parameter_open_duration_text, exhibition.getOpenDuration()));

        tvDescription.setText(exhibition.getDescription());
        tvIBeacons.setText(getString(R.string.parameter_ibeacons_text, String.valueOf(exhibition.isBeacon())));
        tvQrCode.setText(getString(R.string.parameter_qr_code_text, String.valueOf(exhibition.isQr())));
        tvQuiz.setText(getString(R.string.parameter_quiz_text, String.valueOf(exhibition.isQuiz())));

    }

    private void setMapMarker(final String address) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                Geocoder coder = new Geocoder(getActivity());
                List<Address> addresses = new ArrayList<Address>();
                try {
                    addresses = coder.getFromLocationName(address, 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size()!=0) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions = markerOptions.position(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()))
                            .title(exhibition.getName()).snippet(address);
                    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View info = getActivity().getLayoutInflater().inflate(R.layout.map_marker, null);
                            TextView tvMarkerTitle = (TextView) info.findViewById(R.id.tvMarkerTitle);
                            tvMarkerTitle.setText(marker.getTitle());

                            TextView tvMarkerAddress = (TextView) info.findViewById(R.id.tvMarkerAddress);
                            tvMarkerAddress.setText(marker.getSnippet());

                            ImageView ivMarkerImage = (ImageView) info.findViewById(R.id.ivMarkerImage);
                            Picasso.with(getContext()).load(exhibition.getImageURL()).into(ivMarkerImage);

                            return info;
                        }
                    });
                    Marker marker = map.addMarker(markerOptions);
                    marker.showInfoWindow();

                    MapsInitializer.initialize(getActivity());

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15);
                    map.animateCamera(cameraUpdate);
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    public interface OnSelectExhibitionListener {
        public void onSelectExhibition(Exhibition exhibition);
    }
}
