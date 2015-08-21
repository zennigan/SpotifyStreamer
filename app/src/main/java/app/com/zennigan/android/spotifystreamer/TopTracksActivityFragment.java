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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

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

    private static final String PLAYER_FRAGMENT_TAG = "PLAYER_TAG";

    private static final String KEY_TRACK_LIST = "trackList";
    public static final String TRACK_ID = "id";
    public static final String TRACK_ARTIST = "artist";
    public static final String TRACK_TWO_PANE = "twoPane";

    private ListView mTrackListView;
    private TrackListAdapter mTrackAdapter;
    private ArrayList<TrackParcel> mTrackParcelList;
    private String mArtistName;
    private String mArtistId;
    private boolean mTwoPane;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
//        String artistId = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
//        mArtistName = getActivity().getIntent().getStringExtra(Intent.EXTRA_TITLE);


        Bundle arguments  = getArguments();
        if(arguments !=null){
            mArtistName = arguments.getString(TopTracksActivityFragment.TRACK_ARTIST);
            mArtistId = arguments.getString(TopTracksActivityFragment.TRACK_ID);

            if(arguments.containsKey(TRACK_TWO_PANE)){
                mTwoPane = arguments.getBoolean(TRACK_TWO_PANE);
            }
        }

        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(mArtistName);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String countryCode = preferences.getString(getString(R.string.pref_country_key), getString(R.string.pref_country_default));

        mTrackListView = (ListView) rootView.findViewById(R.id.listView_track);

        if(savedInstanceState != null) {
            // read the person list from the saved state
            mTrackParcelList = savedInstanceState.getParcelableArrayList(KEY_TRACK_LIST);
        } else {
            // load the person list
            mTrackParcelList = new ArrayList<TrackParcel>();
            new TrackListTask().execute(mArtistId,countryCode);
        }

        mTrackAdapter = new TrackListAdapter(getActivity(), mTrackParcelList);
        mTrackListView.setAdapter(mTrackAdapter);

        mTrackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mTwoPane){
                    Bundle args = new Bundle();
                    args.putString(PlayerActivityFragment.PLAYER_ARTIST, mArtistName);
                    args.putParcelableArrayList(PlayerActivityFragment.PLAYER_TRACK_LIST, mTrackParcelList);
                    args.putInt(PlayerActivityFragment.PLAYER_POSITION, position);

                    PlayerActivityFragment fragment = new PlayerActivityFragment();
                    fragment.setArguments(args);
                    fragment.show(getActivity().getSupportFragmentManager(),PLAYER_FRAGMENT_TAG);
                }else{
                    Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);

                    playerIntent.putExtra(PlayerActivityFragment.PLAYER_ARTIST, mArtistName);
                    playerIntent.putParcelableArrayListExtra(PlayerActivityFragment.PLAYER_TRACK_LIST, mTrackParcelList);
                    playerIntent.putExtra(PlayerActivityFragment.PLAYER_POSITION, position);
                    startActivity(playerIntent);
                }
            }
        });

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
                            if(track!=null) {
                                String image = null;
                                String playerImage = null;
                                if (track.album.images != null && track.album.images.size() > 0) {
                                    image = track.album.images.get(track.album.images.size() - 1).url;
                                }
                                if (track.album.images != null && track.album.images.size() > 2) {
                                    playerImage = track.album.images.get(track.album.images.size() - 2).url;
                                } else if (track.album.images != null && track.album.images.size() == 1) {
                                    playerImage = track.album.images.get(0).url;
                                }
                                TrackParcel trackParcel = new TrackParcel(track.album.name, image, track.name, playerImage, track.preview_url);
                                trackParcelList.add(trackParcel);
                            }
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
