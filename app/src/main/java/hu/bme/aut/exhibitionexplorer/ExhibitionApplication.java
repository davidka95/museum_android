package hu.bme.aut.exhibitionexplorer;

import com.google.firebase.database.FirebaseDatabase;
import com.orm.SugarApp;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class ExhibitionApplication extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}