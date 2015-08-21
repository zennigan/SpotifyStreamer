package app.com.zennigan.android.spotifystreamer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    public static String PLAYER_ARTIST = "artist";
    public static String PLAYER_TRACK_LIST = "trackList";
    public static String PLAYER_POSITION = "position";

    public static String PLAYER_CURRENT_DURATION = "currDuration";

    private ArrayList<TrackParcel> mTrackParcelList;
    private int mPosition;
    private String mArtist;
    private TextView mArtistView;
    private TextView mAlbumView;
    private ImageView mImageView;
    private TextView mTitleView;
    private String mUrl;

    private MediaPlayer mMediaPlayer;
    private ProgressDialog mProgressDialog;
    private SeekBar mSeekBar;
    private ImageButton mPlayButton;
    private TextView mCurrentDurationLabel;
    private TextView mMaxDurationLabel;

    private boolean mIsPlaying;
    private Handler mHandler = new Handler();
    private Utilities mUtils;

    private int mPrevDuration = -1;

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int currentDuration = mMediaPlayer.getCurrentPosition();
            mCurrentDurationLabel.setText("" + mUtils.milliSecondsToTimer(currentDuration));

            mSeekBar.setProgress(currentDuration);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        Bundle arguments  = getArguments();
        if(arguments !=null){
            mTrackParcelList = arguments.getParcelableArrayList(PLAYER_TRACK_LIST);
            mPosition = arguments.getInt(PLAYER_POSITION);
            mArtist = arguments.getString(PLAYER_ARTIST);
        }

        if(savedInstanceState != null) {
            // read the person list from the saved state
            mPosition = savedInstanceState.getInt(PLAYER_POSITION);
        }

        String album = mTrackParcelList.get(mPosition).getAlbum();
        String title = mTrackParcelList.get(mPosition).getTitle();
        String image = mTrackParcelList.get(mPosition).getPlayerImage();
        mUrl = mTrackParcelList.get(mPosition).getPreviewUrl();

        mArtistView = (TextView) rootView.findViewById(R.id.player_artist);
        mAlbumView = (TextView) rootView.findViewById(R.id.player_album);
        mImageView = (ImageView) rootView.findViewById(R.id.player_image);
        mTitleView = (TextView) rootView.findViewById(R.id.player_title);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.player_play);
        mCurrentDurationLabel = (TextView) rootView.findViewById(R.id.current_duration);
        mMaxDurationLabel = (TextView) rootView.findViewById(R.id.max_duration);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.player_seek);

        mArtistView.setText(mArtist);
        mAlbumView.setText(album);
        mTitleView.setText(title);
        if(image != null) {
            Picasso.with(getActivity()).load(image).into(mImageView);
        }

        ImageButton playButton = (ImageButton) rootView.findViewById(R.id.player_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaySelected(mUrl);
            }
        });

        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.player_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = getNextPosition();
                String artist = getActivity().getIntent().getStringExtra(PLAYER_ARTIST);
                String album = mTrackParcelList.get(mPosition).getAlbum();
                String title = mTrackParcelList.get(mPosition).getTitle();
                String image = mTrackParcelList.get(mPosition).getPlayerImage();
                mUrl = mTrackParcelList.get(mPosition).getPreviewUrl();

                mArtistView.setText(artist);
                mAlbumView.setText(album);
                mTitleView.setText(title);
                if(image != null) {
                    Picasso.with(getActivity()).load(image).into(mImageView);
                }

                if(mIsPlaying) {
                    onPlaySelected(mUrl);
                }
            }
        });

        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.player_prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = getPreviousPosition();
                String artist = getActivity().getIntent().getStringExtra(PLAYER_ARTIST);
                String album = mTrackParcelList.get(mPosition).getAlbum();
                String title = mTrackParcelList.get(mPosition).getTitle();
                String image = mTrackParcelList.get(mPosition).getPlayerImage();
                mUrl = mTrackParcelList.get(mPosition).getPreviewUrl();

                mArtistView.setText(artist);
                mAlbumView.setText(album);
                mTitleView.setText(title);
                if(image != null) {
                    Picasso.with(getActivity()).load(image).into(mImageView);
                }

                if(mIsPlaying) {
                    onPlaySelected(mUrl);
                }
            }
        });

        //Check if need to play
        if(savedInstanceState != null && savedInstanceState.containsKey(PLAYER_CURRENT_DURATION)) {
            // read the person list from the saved state
            mPrevDuration = savedInstanceState.getInt(PLAYER_CURRENT_DURATION);
            onPlaySelected(mUrl);
        }else {
            mPrevDuration = -1;
            onPlaySelected(mUrl);
        }

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(PLAYER_POSITION,mPosition);

        if(mIsPlaying) {
            int currentDuration = mMediaPlayer.getCurrentPosition();
            outState.putInt(PLAYER_CURRENT_DURATION,currentDuration);
        }
        super.onSaveInstanceState(outState);
    }

    private int getNextPosition(){
        return (mPosition + 1)% mTrackParcelList.size();
    }

    private int getPreviousPosition(){
        return  ((mPosition+mTrackParcelList.size())-1)%mTrackParcelList.size();
    }

    public void onPlaySelected(String url) {

        if(mMediaPlayer!=null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }


        mSeekBar.setOnSeekBarChangeListener(this);
        mUtils = new Utilities();

        try {
//            mProgressDialog = new ProgressDialog(getActivity());
//            mProgressDialog.setMessage(getString(R.string.buffer_text));
//            mProgressDialog.show();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnCompletionListener(this);

            mPlayButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mIsPlaying) {
                        mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                        pause();
                    } else {
                        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                        play();
                    }
                    mIsPlaying = !mIsPlaying;
                }
            });
        }
        catch(Exception e)
        {
            Log.e("StreamAudioDemo", e.getMessage());
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i("StreamAudioDemo", "prepare finished");
        //mProgressDialog.dismiss();

        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        mIsPlaying = true;

        if(mPrevDuration!=-1){
            mMediaPlayer.seekTo(mPrevDuration);

            // update timer progress again
            updateProgressBar();
        }
        mMediaPlayer.start();

        // set Progress bar values
        mSeekBar.setProgress(0);
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mCurrentDurationLabel.setText("" + mUtils.milliSecondsToTimer(mMediaPlayer.getCurrentPosition()));
        mMaxDurationLabel.setText("" + mUtils.milliSecondsToTimer(mMediaPlayer.getDuration()));

        // Updating progress bar
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        mProgressDialog.dismiss();
        mIsPlaying = false;
        mPlayButton.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
//        mProgressDialog.dismiss();
        return false;
    }

    private void pause() {
        mMediaPlayer.pause();
    }

    private void play(){
        mMediaPlayer.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mUpdateTimeTask);
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mMediaPlayer.getDuration();
        int currentPosition = seekBar.getProgress();

        // forward or backward to certain seconds
        mMediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /* Update timer on SeekBar
    * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
}
