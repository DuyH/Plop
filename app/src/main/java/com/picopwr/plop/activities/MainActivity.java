package com.picopwr.plop.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.picopwr.plop.R;
import com.picopwr.plop.db.LogDBHelper;
import com.picopwr.plop.utility.UserLocalStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity"; // For logcat


    private Button mCreateLogButton;
    private Button mViewLogsButton;
    private Button mLogoutButton;
    private Button mShareLogsButton;
    private TextView mTextFact;
    String randomFact;
    UserLocalStore mUserLocalStore;
    private SoundPool mSoundPool;
    private ImageView mPlopLogo;
    private boolean mSoundsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserLocalStore = new UserLocalStore(this);


        // Setup SoundPool according to SDK version
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "USING NEW SOUND POOL");
            createNewSoundPool();
        } else {
            Log.d(TAG, "USING OLD SOUND POOL");
            createOldSoundPool();
        }

        // Load all sound effects (context, id from res/raw, priority - currently has no effect so use 1)
        mSoundPool.load(this, R.raw.fart01, 1);
        mSoundPool.load(this, R.raw.fart02, 1);
        mSoundPool.load(this, R.raw.fart03, 1);
        mSoundPool.load(this, R.raw.fart04, 1);
        mSoundPool.load(this, R.raw.fart05, 1);
        mSoundPool.load(this, R.raw.fart06, 1);
        mSoundPool.load(this, R.raw.fart07, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSoundsLoaded = true;
            }
        });

        // Create the music player for the sfx
        // final MediaPlayer mp = new MediaPlayer(); // Too resource intensive so use SoundPool

        // Make the Plop logo have sfx
        mPlopLogo = (ImageView) findViewById(R.id.mainLogo);
        mPlopLogo.setSoundEffectsEnabled(false);
        mPlopLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRandomSound();
            }
            private void playRandomSound() {
                Log.d(TAG, "PLAYING RANDOM SOUND");
                int sound = (new Random().nextInt(7) + 1);
                mSoundPool.play(sound, 1, 1, 1, 0, 1);
            }

        });


        // Random fact:
        mTextFact = (TextView) findViewById(R.id.text_fact);
        final String[] facts = getResources().getStringArray(R.array.random_facts);
        randomFact = facts[new Random().nextInt(facts.length)];
        mTextFact.setText(randomFact);
        mTextFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextFact.setText(facts[new Random().nextInt(facts.length)]);
            }
        });


        // CREATE NEW LOG BUTTON:
        mCreateLogButton = (Button) findViewById(R.id.button_log);
        mCreateLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LogActivity.class);
                startActivity(i);
            }
        });

        // VIEW PAST LOGS BUTTON:
        mViewLogsButton = (Button) findViewById(R.id.button_view);
        mViewLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ViewActivity.class);
                startActivity(i);
            }
        });

        // Send Logs:
        // http://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application

        // Get email
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString("email", null);


        LogDBHelper logDBHelper = new LogDBHelper(this);
        final List<ArrayList<String>> entries = logDBHelper.getLogEntries(email);
        mShareLogsButton = (Button) findViewById(R.id.button_send);
        mShareLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, "Here are your logs!");
                i.putExtra(Intent.EXTRA_TEXT, entries.toString());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // LOGOUT BUTTON:
        mLogoutButton = (Button) findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Clear out current user from shared pref:
                mUserLocalStore.clearUserData();
                mUserLocalStore.setUserLoggedIn(false);

                // Update shared pref to reflect user not logged in:
                SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean(getString(R.string.LOGGEDIN), false);
                editor.putString("email", "");
                editor.commit();

                // Redirect to login page:
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });



    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Perhaps make sure user is logged in?
        // if authenticated?
    }

}
