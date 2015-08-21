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
    private static final String KEY_PLAYER_IMAGE = "playerImage";
    private static final String KEY_PREVIEW_URL = "previewUrl";

    private String album;
    private String image;
    private String title;
    private String playerImage;
    private String previewUrl;

    public TrackParcel() {

    }

    public TrackParcel(String album, String image, String title, String playerImage,String previewUrl) {
        this.album = album;
        this.image = image;
        this.title = title;
        this.playerImage = playerImage;
        this.previewUrl = previewUrl;
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

    public String getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(String playerImage) {
        this.playerImage = playerImage;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
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
        bundle.putString(KEY_PLAYER_IMAGE, playerImage);
        bundle.putString(KEY_PREVIEW_URL, previewUrl);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<TrackParcel> CREATOR = new Creator<TrackParcel>() {

        @Override
        public TrackParcel createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new TrackParcel(bundle.getString(KEY_ALBUM),
                    bundle.getString(KEY_IMAGE),bundle.getString(KEY_TITLE),bundle.getString(KEY_PLAYER_IMAGE),bundle.getString(KEY_PREVIEW_URL) );
        }

        @Override
        public TrackParcel[] newArray(int size) {
            return new TrackParcel[size];
        }

    };
}
