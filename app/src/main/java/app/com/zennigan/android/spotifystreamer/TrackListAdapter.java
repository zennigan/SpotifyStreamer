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

/**
 * Created by Zennigan on 7/5/2015.
 */
public class TrackListAdapter extends ArrayAdapter<TrackParcel> {

    private final Activity context;
    private final static int THUMBNAIL_LOC = 0;
    private List<TrackParcel> trackList;

    public TrackListAdapter(Activity context, List<TrackParcel> trackList){
        super(context, R.layout.track_list, trackList);
        this.trackList = trackList;
        this.context = context;
    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.track_list, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.track_icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.track_name);
        TextView txtDesc = (TextView) rowView.findViewById(R.id.track_desc);
        txtTitle.setText(trackList.get(position).getAlbum());
        txtDesc.setText(trackList.get(position).getTitle());

        if(trackList.get(position).getImage() != null) {
            Picasso.with(context).load(trackList.get(position).getImage()).into(imageView);
        }else{
            imageView.setImageResource(R.drawable.ic_launcher);
        }


        return rowView;
    };

    @Override
    public void addAll(Collection<? extends TrackParcel> collection) {
        super.addAll(collection);
        this.trackList.clear();
        this.trackList.addAll(collection);
    }
}
