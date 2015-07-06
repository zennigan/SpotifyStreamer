package app.com.zennigan.android.spotifystreamer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zennigan on 7/5/2015.
 * Based on info in: http://www.perfectapk.com/android-parcelable.html
 */
public class ArtistParcel implements Parcelable {

    // parcel keys
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_ID = "id";

    private String name;
    private String image;
    private String id;

    public ArtistParcel(){

    }

    public ArtistParcel(String name, String image, String id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        // insert the key value pairs to the bundle
        bundle.putString(KEY_NAME, name);
        bundle.putString(KEY_IMAGE, image);
        bundle.putString(KEY_ID, id);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<ArtistParcel> CREATOR = new Creator<ArtistParcel>() {

        @Override
        public ArtistParcel createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new ArtistParcel(bundle.getString(KEY_NAME),
                    bundle.getString(KEY_IMAGE),bundle.getString(KEY_ID) );
        }

        @Override
        public ArtistParcel[] newArray(int size) {
            return new ArtistParcel[size];
        }

    };
}
