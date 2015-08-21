package app.com.zennigan.android.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.protocol.HTTP;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback{

    private static final String TOP_TRACK_FRAGMENT_TAG = "TT_TAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.track_list_container) != null) {
            mTwoPane = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.track_list_container, new TopTracksActivityFragment(), TOP_TRACK_FRAGMENT_TAG)
//                        .commit();
//            }
        } else {
            mTwoPane = false;

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id, String artist) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString(TopTracksActivityFragment.TRACK_ID,id);
            args.putString(TopTracksActivityFragment.TRACK_ARTIST, artist);
            args.putBoolean(TopTracksActivityFragment.TRACK_TWO_PANE, mTwoPane);

            TopTracksActivityFragment fragment = new TopTracksActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_list_container,fragment, TOP_TRACK_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent trackIntent = new Intent(this, TopTracksActivity.class);
            trackIntent.putExtra(TopTracksActivityFragment.TRACK_ID,id);
            trackIntent.putExtra(TopTracksActivityFragment.TRACK_ARTIST, artist);
            startActivity(trackIntent);
        }
    }
}
