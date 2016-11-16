package hu.bme.aut.exhibitionexplorer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import hu.bme.aut.exhibitionexplorer.interfaces.OnArtifactItemClickListener;

/**
 * Created by Zay on 2016.11.14..
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> implements FavoriteTouchHelperAdapter {
    private ArrayList<Artifact> favorites;
    private Context context;
    OnArtifactItemClickListener itemClickListener;

    public FavoriteAdapter(Context context, OnArtifactItemClickListener listener) {
        favorites = new ArrayList<>();
        this.context = context;
        itemClickListener = listener;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onArtifactItemClick(artifact);
            }
        });
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
    public void onItemDismiss(final int position, final RecyclerView recyclerView) {
        final Artifact tmp = favorites.get(position);
        favorites.get(position).delete();
        favorites.remove(position);
        notifyItemRemoved(position);

        Snackbar addedSnackbar = Snackbar.make(recyclerView,
                "Artifact deleted from favorites", Snackbar.LENGTH_LONG);
        addedSnackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        Snackbar undoSnackbar = Snackbar.make(recyclerView, "Restored to favorites", Snackbar.LENGTH_SHORT);
                        undoSnackbar.show();
                        favorites.add(position, tmp);
                        favorites.get(position).save();
                        notifyItemInserted(position);
                        break;
                    default:
                        break;
                }
            }
        });
        addedSnackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        })
                .setActionTextColor(Color.YELLOW);
        addedSnackbar.show();
    }
}
