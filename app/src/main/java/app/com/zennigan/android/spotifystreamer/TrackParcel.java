package app.com.zennigan.android.spotifystreamer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zennigan on 7/5/2015.
 */
public class TrackParcel implements Parcelable {

    // parcel keys
    private static final String KEY_ALBUM = "album";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TITLE = "title";

    private String album;
    private String image;
    private String title;

    public TrackParcel() {

    }

    public TrackParcel(String album, String image, String title) {
        this.album = album;
        this.image = image;
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        bundle.putString(KEY_ALBUM, album);
        bundle.putString(KEY_IMAGE, image);
        bundle.putString(KEY_TITLE, title);

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
            return new ArtistParcel(bundle.getString(KEY_ALBUM),
                    bundle.getString(KEY_IMAGE),bundle.getString(KEY_TITLE) );
        }

        @Override
        public ArtistParcel[] newArray(int size) {
            return new ArtistParcel[size];
        }

    };
}
