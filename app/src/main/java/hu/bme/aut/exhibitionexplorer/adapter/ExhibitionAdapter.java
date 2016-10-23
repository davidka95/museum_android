package hu.bme.aut.exhibitionexplorer.adapter;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.exhibitionexplorer.R;

/**
 * Created by Zay on 2016.10.23..
 */

public class ExhibitionAdapter extends RecyclerView.Adapter<ExhibitionAdapter.ExhibitionViewHolder> {
    List<String[]> ExhibitionDemos;

    public ExhibitionAdapter() {
        ExhibitionDemos = new ArrayList();
        initDemo();
    }

    private void initDemo() {
        this.ExhibitionDemos.add(new String[]{"Textúra kiállítás  a Nemzeti Galériában", "Megnyitó: 2016.10.18"});
        this.ExhibitionDemos.add(new String[]{"World Press Photo", "Now in Budapest"});
        this.ExhibitionDemos.add(new String[]{"Vadnyugat", "Az avantgárd Wrocław története"});
    }

    public ExhibitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExhibitionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exhibition_row_item, parent, false));
    }

    public void onBindViewHolder(ExhibitionViewHolder holder, int position) {
        holder.iconImageView.setImageResource(getDemoIcons(position));
        holder.nameTextView.setText(((String[]) this.ExhibitionDemos.get(position))[0]);
        holder.descriptionTextView.setText(((String[]) this.ExhibitionDemos.get(position))[1]);
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
        return this.ExhibitionDemos.size();
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
