package hu.bme.aut.exhibitionexplorer;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Adam on 2016. 11. 30..
 */

public class PicassoCache {

    public static void makeImageRequest(final Context context, final ImageView imageView, final String imageUrl) {

        final int defaultImageResId = R.drawable.no_image_found;
        Picasso.with(context)
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.v("Picasso", "fetch image success in first time.");
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Log.v("Picasso", "Could not fetch image in first time...");
                        Picasso.with(context).load(imageUrl).networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).error(defaultImageResId)
                                .into(imageView, new Callback() {

                                    @Override
                                    public void onSuccess() {
                                        Log.v("Picasso", "fetch image success in try again.");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image again...");
                                    }

                                });
                    }
                });

    }
}
