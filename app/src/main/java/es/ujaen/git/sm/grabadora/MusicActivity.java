package es.ujaen.git.sm.grabadora;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MusicActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION_READEXTERNAL = 1;

    private ImageView mPlayRaw = null;
    private ImageView mPlayExternal = null;

    private MediaPlayer mMPlayer = null;
    private View mMainView = null;
    private int audioSessionId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainView = findViewById(R.id.content_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlayRaw = (ImageView) findViewById(R.id.main_play);

        mPlayExternal = (ImageView) findViewById(R.id.main_play2);


        mPlayRaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();

            }
        });
        mPlayExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (podemosLeer()) {
                    checkPermissions();
                } else {
                    Snackbar.make(mMainView, R.string.externalstorage_error,
                            Snackbar.LENGTH_LONG).show();
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(mMainView, R.string.externalstorage_error,
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MusicActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MusicActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar message = Snackbar.make(mMainView, R.string.permission_readexternal,
                        Snackbar.LENGTH_LONG);
                message.show();
                ActivityCompat.requestPermissions(MusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_READEXTERNAL);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            playMusicExternal();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_READEXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    playMusic();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission..
                    Snackbar.make(mMainView, R.string.permission_readexternal_denied,
                            Snackbar.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void playMusic() {
        if (mMPlayer != null)
            if (mMPlayer.isPlaying()) {
                mMPlayer.pause();
                mPlayRaw.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, android.R.drawable.ic_media_play));
            } else {

                mMPlayer.start();
                mPlayRaw.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, android.R.drawable.ic_media_pause));
            }
    }

    protected void playMusicExternal() {
        File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        String path = music.getPath() + "/" + "invierno.mp3";

        if (mMPlayer != null) {


            if (mMPlayer.isPlaying()) {
                mMPlayer.pause();
                mPlayExternal.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, android.R.drawable.ic_media_play));
            } else {

                mMPlayer.start();
                mPlayExternal.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, android.R.drawable.ic_media_pause));
            }

        } else {
            try {
                mMPlayer = new MediaPlayer();
                mMPlayer.setDataSource(path);
                mMPlayer.prepare();
                mMPlayer.start();
                mPlayExternal.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, android.R.drawable.ic_media_pause));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMPlayer = MediaPlayer.create(MusicActivity.this, R.raw.barry);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMPlayer.isPlaying())
            mMPlayer.stop();
        mMPlayer.release();

    }

    public boolean podemosLeer() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        } else {
            return false;
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
