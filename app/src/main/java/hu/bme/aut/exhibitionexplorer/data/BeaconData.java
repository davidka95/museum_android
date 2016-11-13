package hu.bme.aut.exhibitionexplorer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

/**
 * Created by Adam on 2016. 11. 13..
 */

public class BeaconData implements Parcelable {

    @Exclude
    private long minorID;

    @PropertyName("artifactId")
    private String artifactId;

    @PropertyName("beaconName")
    private String beaconName;

    @PropertyName("location")
    private String location;

    public BeaconData() {
    }

    @Exclude
    public long getMinorID() {
        return minorID;
    }

    @Exclude
    public void setMinorID(long minorID) {
        this.minorID = minorID;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    protected BeaconData(Parcel in) {
        minorID = in.readLong();
        artifactId = in.readString();
        beaconName = in.readString();
        location = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(minorID);
        dest.writeString(artifactId);
        dest.writeString(beaconName);
        dest.writeString(location);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BeaconData> CREATOR = new Parcelable.Creator<BeaconData>() {
        @Override
        public BeaconData createFromParcel(Parcel in) {
            return new BeaconData(in);
        }

        @Override
        public BeaconData[] newArray(int size) {
            return new BeaconData[size];
        }
    };
}
