package hu.bme.aut.exhibitionexplorer.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
                .placeholder(R.mipmap.ic_launcher).into(ivExhibitionImage);

        FirebaseDatabase.getInstance().getReference("museums").child(String.valueOf(exhibition.getInMuseum()))
                .child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvLocation.setText(getString(R.string.parameter_location_text, dataSnapshot.getValue(String.class)));
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

    public interface OnSelectExhibitionListener {
        public void onSelectExhibition(Exhibition exhibition);
    }
}
