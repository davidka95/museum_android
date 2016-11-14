package hu.bme.aut.exhibitionexplorer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.fragment.FavoriteFragment;
import hu.bme.aut.exhibitionexplorer.interfaces.FavoriteTouchHelperAdapter;

/**
 * Created by Zay on 2016.11.14..
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> implements FavoriteTouchHelperAdapter {
    private ArrayList<Artifact> favorites;
    private Context context;

    public FavoriteAdapter(Context context) {
        favorites = new ArrayList<>();
        this.context = context;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        final Artifact artifact = favorites.get(position);
        try {
            Picasso.with(context).load(artifact.getImageURL()).fit().centerCrop()
                    .placeholder(R.drawable.loading_animation).into(holder.iconImageView);
        } catch (Exception e) {
            Picasso.with(context).load(R.drawable.no_image_found).into(holder.iconImageView);
        }

        holder.nameTextView.setText(artifact.getName());
        //TODO itemclick
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public void update(List<Artifact> shoppingItems) {
        favorites.clear();
        favorites.addAll(shoppingItems);
        notifyDataSetChanged();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            this.iconImageView = ((ImageView) itemView.findViewById(R.id.FavoriteIconImageView));
            this.nameTextView = ((TextView) itemView.findViewById(R.id.FavoriteNameTextView));
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(favorites, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(favorites, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        favorites.get(position).delete();
        favorites.remove(position);
        notifyItemRemoved(position);
    }
}
