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
//        View rowView=inflater.inflate(R.layout.track_list, null, true);

//        ImageView imageView = (ImageView) rowView.findViewById(R.id.track_icon);
//        TextView txtTitle = (TextView) rowView.findViewById(R.id.track_name);
//        TextView txtDesc = (TextView) rowView.findViewById(R.id.track_desc);

        ViewHolder holder;

        if(view == null){
            View rowView=inflater.inflate(R.layout.track_list, null, true);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);
            view = rowView;
        }else{
            holder = (ViewHolder) view.getTag();
        }


        holder.txtTitle.setText(trackList.get(position).getAlbum());
        holder.txtDesc.setText(trackList.get(position).getTitle());

        if(trackList.get(position).getImage() != null) {
            Picasso.with(context).load(trackList.get(position).getImage()).into(holder.imageView);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_launcher);
        }


        return view;
    };

    @Override
    public void addAll(Collection<? extends TrackParcel> collection) {
        super.addAll(collection);
        this.trackList.clear();
        this.trackList.addAll(collection);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView txtTitle;
        public final TextView txtDesc;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.track_icon);
            txtTitle = (TextView) view.findViewById(R.id.track_name);
            txtDesc = (TextView) view.findViewById(R.id.track_desc);
        }
    }
}
