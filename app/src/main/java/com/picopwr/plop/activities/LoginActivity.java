package com.picopwr.plop.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.picopwr.plop.R;
import com.picopwr.plop.db.DatabaseHelper;
import com.picopwr.plop.model.User;
import com.picopwr.plop.utility.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

/**
 * Login activity handles user login. It is the first screen user will see.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity"; // For logcat
    private static final int REQUEST_CODE_REGISTER = 0; // activities, see BNR pg 102

    private DatabaseHelper mDBHelper = new DatabaseHelper(this);

    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mLoginButton;
    private ImageView mPlopLogo;

    // Sound effects:
    private SoundPool mSoundPool;
    private boolean mSoundsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        mPlopLogo = (ImageView) findViewById(R.id.plop_logo);
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


        // Initialize the email and password edit text fields:
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditPassword = (EditText) findViewById(R.id.edit_password);

        // Make login button attempt login with user credentials
        mLoginButton = (Button) findViewById(R.id.button_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Hide the keyboard when user clicks on login button
                // NOTE TO SELF: PUT IN UTILITY CLASS!
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // First validate credentials
                validateCredentials();
            }
        });

        // Register link onclick:
        TextView mTextRegister = (TextView) findViewById(R.id.link_signup);
        mTextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // From BNR, pg97
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(i, REQUEST_CODE_REGISTER);
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

    // This is called when being passed back to this activity from another
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // If passed back from the register activity,
        if (requestCode == REQUEST_CODE_REGISTER) {
            // and with successful registration...
            if (resultCode == RESULT_OK) {

                // Login with the data and finish this activity?
                Toast.makeText(this, "User account created!", Toast.LENGTH_SHORT).show();

                // Consider making pretty splash welcome screen pages for first time users
                // Intent i = new Intent(LoginActivity.this, WelcomeSplash.class);

                // Should take user to main screen, loggedin
                // [ Bundle the data from RegisterActivty when it returned ]

                // Update shared pref to reflect user logged in:
                SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean(getString(R.string.LOGGEDIN), false);
                editor.commit();

                // But for now, just create a new intent to the main activity
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        }
    }

    // Disable back-button on login screen (don't know why)
    // http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void validateCredentials() {

        Log.d(TAG, "Attempting to login");

        // Disable login button while validating and attempting login
        mLoginButton.setEnabled(false);

        // Stringing credentials
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();

        // Validate credentials:
        if (!validateEmail(email) || !validatePassword(password)) {
            onLoginFail();
            return; // Immediately exit validation
        }

        // Authentic user credentials:
        authenticate(email, password);
    }

    // Pass credentials along, attempt a login (web version)
    public void authenticate(User user) {
        new LoginAsyncTask().execute(user.getEmail(), user.getPassword());
    }

    // Authenticate using password in local database
    public void authenticate(String email, String password) {
        String dbPassword = mDBHelper.searchPass(email);
        Log.d(TAG, dbPassword);
        if (password.equals(dbPassword)) {

            // Store the user's email address in shared preferences
            SharedPreferences sharedPrefs = getSharedPreferences("com.picopwr.plop.PREFS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("email", email);
            editor.commit();
            onLoginSuccess();
        } else {
            onLoginFail();
        }

    }


    public void onLoginSuccess() {
        Log.d(TAG, "Successful login");
        mLoginButton.setEnabled(true); // Reset login button

        // Update shared pref to reflect user logged in:
        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(getString(R.string.LOGGEDIN), true);
        editor.commit();

        // Navigate to the Main Activity screen:
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Upon failed login, display a Toast and reset the login button
     */
    public void onLoginFail() {
        Log.d(TAG, "Login failed");
        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        mLoginButton.setEnabled(true); // Reset login button
    }

    // Consider putting in utility class!
    public boolean validateEmail(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditEmail.setError("enter a valid email address");
            return false;
        } else {
            return true;
        }
    }

    // Consider putting in utility class!
    public boolean validatePassword(String password) {
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mEditPassword.setError("password must be between 4 to 10 alphanumeric characters");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Private AsyncTask that handles web service connection for login authentication
     */
    private class LoginAsyncTask extends AsyncTask<String, String, JSONObject> {

        private static final String URL = "http://cssgate.insttech.washington.edu/~dhuynh88/Android/login.php";

        // Response tags derived from login.php
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        private ProgressDialog mProgressDialog;
        private JSONParser mJSONParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Authenticating...");
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String email = strings[0];
            String password = strings[1];

            try {
                HashMap<String, String> dataToSend = new HashMap();
                dataToSend.put("email", email);
                dataToSend.put("password", password);
                Log.d(TAG, "Authenticating with " + email + " and password " + password);
                JSONObject jsonObject = mJSONParser.makeHttpRequest(URL, "POST", dataToSend);

                if (jsonObject != null) {
                    Log.d("JSON Result", jsonObject.toString());

                    return jsonObject;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            int success = 0;
            String message = "";

            // Turn off progress dialog if running
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            // Retrieve json response from within the jsonobject
            if (jsonObject != null) {
                try {
                    success = jsonObject.getInt(TAG_SUCCESS);
                    message = jsonObject.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error in retrieving json response tags");
                }
            }

            // Check response
            if (success == 1) {
                onLoginSuccess();
                Log.d(TAG, "Successfully logged in");
            } else {
                onLoginFail();
                Log.d(TAG, "Failed to log in");
            }
        }
    }


}
