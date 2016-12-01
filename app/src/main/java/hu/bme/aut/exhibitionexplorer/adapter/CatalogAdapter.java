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

import hu.bme.aut.exhibitionexplorer.PicassoCache;
import hu.bme.aut.exhibitionexplorer.R;
import hu.bme.aut.exhibitionexplorer.data.Artifact;
import hu.bme.aut.exhibitionexplorer.fragment.CatalogFragment;
import hu.bme.aut.exhibitionexplorer.interfaces.OnArtifactItemClickListener;

/**
 * Created by Zay on 2016.11.05..
 */
public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {
    private ArrayList<Artifact> artifacts;
    private Context context;
    private OnArtifactItemClickListener itemClickListener;

    public CatalogAdapter(Context context, OnArtifactItemClickListener listener) {
        artifacts = new ArrayList<>();
        this.context = context;
        itemClickListener = listener;
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CatalogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CatalogViewHolder holder, int position) {
        final Artifact artifact = artifacts.get(position);
        PicassoCache.makeImageRequest(context, holder.iconImageView, artifact.getImageURL());

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
        return artifacts.size();
    }

    public void addArtifact(Artifact artifact) {
        artifacts.add(artifact);
    }

    public class CatalogViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;

        public CatalogViewHolder(View itemView) {
            super(itemView);
            this.iconImageView = ((ImageView) itemView.findViewById(R.id.CatalogIconImageView));
            this.nameTextView = ((TextView) itemView.findViewById(R.id.CatalogNameTextView));
        }
    }
}
