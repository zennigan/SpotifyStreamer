package app.com.zennigan.android.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Zennigan on 7/3/2015.
 * Sample Custom ListView Adapter: http://www.androidinterview.com/android-custom-listview-with-image-and-text-using-arrayadapter/
 */
public class ArtistListAdapter extends ArrayAdapter<ArtistParcel> {

    private final Activity context;
    private List<ArtistParcel> artistList;

    public ArtistListAdapter(Activity context, List<ArtistParcel> artistList) {
        super(context, R.layout.artist_list, artistList);

        this.context=context;
        this.artistList=artistList;
    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.artist_list, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.artist_icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.artist_name);
        txtTitle.setText(artistList.get(position).getName());
        if(artistList.get(position).getImage()!=null) {
            Picasso.with(context).load(artistList.get(position).getImage()).into(imageView);
        }else{
            imageView.setImageResource(R.drawable.ic_launcher);
        }


        return rowView;
    };

    @Override
    public void addAll(Collection<? extends ArtistParcel> collection) {
        super.addAll(collection);
        this.artistList.clear();
        this.artistList.addAll(collection);
    }
}
