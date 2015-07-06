package app.com.zennigan.android.spotifystreamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private static final String KEY_TRACK_LIST = "trackList";

    private ListView mTrackListView;
    private TrackListAdapter mTrackAdapter;
    private ArrayList<TrackParcel> mTrackParcelList;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        String artistId = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String artistName = getActivity().getIntent().getStringExtra(Intent.EXTRA_TITLE);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(artistName);

        mTrackListView = (ListView) rootView.findViewById(R.id.listView_track);

        if(savedInstanceState != null) {
            // read the person list from the saved state
            mTrackParcelList = savedInstanceState.getParcelableArrayList(KEY_TRACK_LIST);
        } else {
            // load the person list
            mTrackParcelList = new ArrayList<TrackParcel>();
        }

        mTrackAdapter = new TrackListAdapter(getActivity(), mTrackParcelList);
        mTrackListView.setAdapter(mTrackAdapter);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        String countryCode = preferences.getString(getString(R.string.pref_country_key), getString(R.string.pref_country_default));

        new TrackListTask().execute(artistId,countryCode);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRACK_LIST, mTrackParcelList);
        super.onSaveInstanceState(outState);
    }


    public class TrackListTask extends AsyncTask<String, Void, List<TrackParcel>> {
        private final String LOG_TAG = TrackListTask.class.getSimpleName();

        @Override
        protected void onPostExecute(List<TrackParcel> result) {
            if(result!=null && result.size() >0){
                mTrackParcelList = new ArrayList<TrackParcel>(result);
                mTrackAdapter.clear();
                mTrackAdapter.addAll(result);
            }else if(result!=null && result.size()==0){
                Toast.makeText(getActivity(), getString(R.string.no_track_label), Toast.LENGTH_SHORT).show();
            }else if(result==null ){
                Toast.makeText(getActivity(), getString(R.string.no_connection_label), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected List<TrackParcel> doInBackground(String... params) {
            List<TrackParcel> trackParcelList = null;

            try{
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                final String countryCode = params[1];

                Tracks tracks = spotify.getArtistTopTrack(params[0], new HashMap<String, Object>() {
                    {
                        put(getString(R.string.api_country_param), countryCode);
                    }
                });

                if(tracks!=null) {
                    List<Track> trackList = tracks.tracks;
                    if (trackList != null) {
                        trackParcelList = new ArrayList<TrackParcel>();
                        for (Track track : trackList) {
                            String image = null;
                            if (track.album.images != null && track.album.images.size() > 0) {
                                image = track.album.images.get(track.album.images.size() - 1).url;
                            }
                            TrackParcel trackParcel = new TrackParcel(track.album.name, image, track.name);
                            trackParcelList.add(trackParcel);
                        }
                    }
                }
            } catch (RetrofitError retrofitError){
                Log.e(LOG_TAG + "Connection Error: ", retrofitError.getMessage());
            }

            return trackParcelList;
        }
    }
}
