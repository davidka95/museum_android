package hu.bme.aut.exhibitionexplorer.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Exhibition;
import hu.bme.aut.exhibitionexplorer.fragment.ExhibitionFragment;

/**
 * Created by Zay on 2016.10.23..
 */

public class ExhibitionAdapter extends RecyclerView.Adapter<ExhibitionAdapter.ExhibitionViewHolder> {
    private ArrayList<Exhibition> exhibitions;
    private Context context;

    ExhibitionFragment.OnItemClickListener onItemClickListener;

    public ExhibitionAdapter(Context context, ExhibitionFragment.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        exhibitions = new ArrayList<>();
    }

    public ExhibitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExhibitionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exhibition_row_item, parent, false));
    }

    public void onBindViewHolder(ExhibitionViewHolder holder, int position) {
        final Exhibition exhibition = exhibitions.get(position);
        Picasso.with(context).load(exhibition.getImageURL()).fit().centerCrop()
                .placeholder(R.mipmap.ic_launcher).into(holder.iconImageView);
        holder.nameTextView.setText(exhibition.getName());
        holder.descriptionTextView.setText(exhibition.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(exhibition);
            }
        });
    }

    private int getDemoIcons(int position) {
        @DrawableRes int ret;
        switch (position) {
            case 0:
                ret = R.drawable.exhibition_demo_icon_textura;
                break;
            case 1:
                ret = R.drawable.exhibition_demo_icon_worldpress;
                break;
            case 2:
                ret = R.drawable.exhibition_demo_icon_vadnyugat;
                break;
            default:
                ret = R.drawable.exhibition_demo_icon_textura;
                break;
        }
        return ret;
    }


    public int getItemCount() {
        return exhibitions.size();
    }

    public void addExhibition(Exhibition exhibition) {
        exhibitions.add(exhibition);
    }


    public class ExhibitionViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;
        ImageView iconImageView;
        TextView nameTextView;

        public ExhibitionViewHolder(View itemView) {
            super(itemView);
            this.iconImageView = ((ImageView) itemView.findViewById(R.id.ExhibitionIconImageView));
            this.nameTextView = ((TextView) itemView.findViewById(R.id.ExhibitionNameTextView));
            this.descriptionTextView = ((TextView) itemView.findViewById(R.id.ExhibitionDescriptionTextView));
        }
    }
}
