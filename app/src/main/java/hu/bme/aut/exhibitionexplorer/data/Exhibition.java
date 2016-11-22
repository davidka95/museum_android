package hu.bme.aut.exhibitionexplorer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Adam on 2016. 10. 28..
 */

public class Exhibition implements Parcelable {
    @Exclude
    public static final String KEY_EXHIBITION_PARCELABLE = "KEY_EXHIBITION_PARCELABLE";

    @Exclude
    public static final String KEY_CHOOSED_EXHIBITION = "KEY_EXHIBITION_CHOOSED";

    @Exclude
    private String UuID;

    @Exclude
    public String getUuID() {
        return UuID;
    }

    @Exclude
    public void setUuID(String uuID) {
        UuID = uuID;
    }

    @PropertyName("artifactHere")
    private HashMap<String, Boolean> artifactsHere;

    @PropertyName("beacon")
    private boolean beacon;

    @PropertyName("beaconRegion")
    private String beaconRegion;

    @PropertyName("description")
    private String description;

    @PropertyName("imageURL")
    private String imageURL;

    @PropertyName("inMuseum")
    private String inMuseum;

    @PropertyName("name")
    private String name;

    @PropertyName("openDuration")
    private String openDuration;

    @PropertyName("qr")
    private boolean qr;

    @PropertyName("quiz")
    private boolean quiz;

    public Exhibition() {
    }

    public HashMap<String, Boolean> getArtifactsHere() {
        return artifactsHere;
    }

    public void setArtifactsHere(HashMap<String, Boolean> artifactsHere) {
        this.artifactsHere = artifactsHere;
    }

    public String getInMuseum() {
        return inMuseum;
    }

    public void setInMuseum(String inMuseum) {
        this.inMuseum = inMuseum;
    }

    public boolean isBeacon() {
        return beacon;
    }

    public void setBeacon(boolean beaconID) {
        this.beacon = beaconID;
    }

    public String getBeaconRegion() {
        return beaconRegion;
    }

    public void setBeaconRegion(String beaconRegion) {
        this.beaconRegion = beaconRegion;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenDuration() {
        return openDuration;
    }

    public void setOpenDuration(String openDuration) {
        this.openDuration = openDuration;
    }

    public boolean isQr() {
        return qr;
    }

    public void setQr(boolean qr) {
        this.qr = qr;
    }

    public boolean isQuiz() {
        return quiz;
    }

    public void setQuiz(boolean quiz) {
        this.quiz = quiz;
    }

    protected Exhibition(Parcel in) {
        UuID = in.readString();
        artifactsHere = (HashMap) in.readValue(HashMap.class.getClassLoader());
        beacon = in.readByte() != 0x00;
        beaconRegion = in.readString();
        description = in.readString();
        imageURL = in.readString();
        inMuseum = in.readString();
        name = in.readString();
        openDuration = in.readString();
        qr = in.readByte() != 0x00;
        quiz = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UuID);
        dest.writeValue(artifactsHere);
        dest.writeByte((byte) (beacon ? 0x01 : 0x00));
        dest.writeString(beaconRegion);
        dest.writeString(description);
        dest.writeString(imageURL);
        dest.writeString(inMuseum);
        dest.writeString(name);
        dest.writeString(openDuration);
        dest.writeByte((byte) (qr ? 0x01 : 0x00));
        dest.writeByte((byte) (quiz ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Exhibition> CREATOR = new Parcelable.Creator<Exhibition>() {
        @Override
        public Exhibition createFromParcel(Parcel in) {
            return new Exhibition(in);
        }

        @Override
        public Exhibition[] newArray(int size) {
            return new Exhibition[size];
        }
    };


}
