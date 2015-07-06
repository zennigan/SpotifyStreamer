package app.com.zennigan.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 * Sample SearchView code from: http://sampleprogramz.com/android/searchview.php
 */
public class MainActivityFragment extends Fragment {

    private static final String KEY_ARTIST_LIST = "artistList";
    private SearchView mArtistSearchView;
    private ListView mArtistListView;
    private ArtistListAdapter mArtistAdapter;
    private ArrayList<ArtistParcel> mArtistParcelList;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final LinearLayout artistLayout = (LinearLayout) rootView.findViewById(R.id.artist_layout);
        artistLayout.requestFocus();

        mArtistSearchView = (SearchView) rootView.findViewById(R.id.searchView_artist);

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) mArtistSearchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        mArtistListView = (ListView) rootView.findViewById(R.id.listView_artist);

        if(savedInstanceState != null) {
            // read the person list from the saved state
            mArtistParcelList = savedInstanceState.getParcelableArrayList(KEY_ARTIST_LIST);
        } else {
            // load the person list
            mArtistParcelList = new ArrayList<ArtistParcel>();
        }
        mArtistAdapter=new ArtistListAdapter(getActivity(), mArtistParcelList);
        mArtistListView.setAdapter(mArtistAdapter);

        mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View forecastListView, int i, long l) {
                ArtistParcel artist = (ArtistParcel) adapterView.getItemAtPosition(i);
                artistLayout.requestFocus();

                Intent trackIntent = new Intent(forecastListView.getContext(), TopTracksActivity.class);
                trackIntent.putExtra(Intent.EXTRA_TEXT, artist.getId());
                trackIntent.putExtra(Intent.EXTRA_TITLE, artist.getName());
                trackIntent.setType(HTTP.PLAIN_TEXT_TYPE);
                startActivity(trackIntent);
            }
        });

        mArtistSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        mArtistSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mArtistSearchView.clearFocus();
                new ArtistListTask().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ARTIST_LIST, mArtistParcelList);
        super.onSaveInstanceState(outState);
    }

    public class ArtistListTask extends AsyncTask<String, Void, List<ArtistParcel>> {

        private final String LOG_TAG = ArtistListTask.class.getSimpleName();

        @Override
        protected void onPostExecute(List<ArtistParcel> result) {
            if(result!=null && result.size()>0){
                mArtistParcelList = new ArrayList<>(result);
                mArtistAdapter.clear();
                mArtistAdapter.addAll(result);
            }else if(result!=null && result.size()==0){
                Toast.makeText(getActivity(), getString(R.string.no_result_label), Toast.LENGTH_SHORT).show();
            }else if(result==null){
                Toast.makeText(getActivity(), getString(R.string.no_connection_label), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected List<ArtistParcel> doInBackground(String... params) {
            List<ArtistParcel> artistParcelList = null;
            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager results = spotify.searchArtists(params[0]);

                if(results!=null && results.artists!=null) {
                    List<Artist> artistList = results.artists.items;

                    if (artistList != null) {
                        artistParcelList = new ArrayList<ArtistParcel>();

                        for (Artist artist : artistList) {
                            String image = null;
                            if (artist.images != null && artist.images.size() > 0) {
                                image = artist.images.get(artist.images.size()-1).url;
                            }
                            ArtistParcel artistParcel = new ArtistParcel(artist.name, image, artist.id);
                            artistParcelList.add(artistParcel);
                        }
                    }
                }
            } catch (RetrofitError retrofitError){
                Log.e(LOG_TAG+"Connection Error: ",retrofitError.getMessage());
            }

            return artistParcelList;
        }
    }
}
