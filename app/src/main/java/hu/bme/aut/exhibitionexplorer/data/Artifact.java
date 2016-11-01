package hu.bme.aut.exhibitionexplorer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

/**
 * Created by Adam on 2016. 11. 01..
 */

public class Artifact implements Parcelable {

    @Exclude
    private String UuID;

    @PropertyName("beaconID")
    private long beaconID;

    @PropertyName("description")
    private String description;

    @PropertyName("imageURL")
    private String imageURL;

    @PropertyName("inExhibition")
    private long inExhibition;

    @PropertyName("name")
    private String name;

    @PropertyName("qrURL")
    private String qrURL;

    @PropertyName("quiz")
    private String quiz;

    public Artifact() {
    }

    @Exclude
    public String getUuID() {
        return UuID;
    }

    @Exclude
    public void setUuID(String uuID) {
        UuID = uuID;
    }

    public long getBeaconID() {
        return beaconID;
    }

    public void setBeaconID(long beaconID) {
        this.beaconID = beaconID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getInExhibition() {
        return inExhibition;
    }

    public void setInExhibition(long inExhibition) {
        this.inExhibition = inExhibition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQrURL() {
        return qrURL;
    }

    public void setQrURL(String qrURL) {
        this.qrURL = qrURL;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    protected Artifact(Parcel in) {
        UuID = in.readString();
        beaconID = in.readLong();
        description = in.readString();
        imageURL = in.readString();
        inExhibition = in.readLong();
        name = in.readString();
        qrURL = in.readString();
        quiz = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UuID);
        dest.writeLong(beaconID);
        dest.writeString(description);
        dest.writeString(imageURL);
        dest.writeLong(inExhibition);
        dest.writeString(name);
        dest.writeString(qrURL);
        dest.writeString(quiz);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Artifact> CREATOR = new Parcelable.Creator<Artifact>() {
        @Override
        public Artifact createFromParcel(Parcel in) {
            return new Artifact(in);
        }

        @Override
        public Artifact[] newArray(int size) {
            return new Artifact[size];
        }
    };
}
