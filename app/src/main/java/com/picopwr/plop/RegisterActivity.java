package com.picopwr.plop;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.picopwr.plop.db.DatabaseHelper;
import com.picopwr.plop.model.User;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity"; // For logcat
    private String url = "http://cssgate.insttech.washington.edu/~dhuynh88/Android/addUser.php";

    DatabaseHelper mDBHelper = new DatabaseHelper(this);


    private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditConfirmPassword;
    private Button mRegisterButton;
    private TextView mTextLink;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditConfirmPassword = (EditText) findViewById(R.id.edit_confirm_password);

        // Register button:
        mRegisterButton = (Button) findViewById(R.id.button_register);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        // Login link redirect
        mTextLink = (TextView) findViewById(R.id.link_login);
        mTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel the registration screen and go to login activity
                Log.d(TAG, "Cancelling registration. Returning to login page");
                finish();
            }
        });

    }


    public void register() {
        Log.d(TAG, "Starting registration");

        // Progress dialog:
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating new account...");

        // Upon successful validation,
        if (validate()) {
            progressDialog.show();
            // !: DRY this up; it's in validate also.
            String name = mEditName.getText().toString();
            String email = mEditEmail.getText().toString();
            String password = mEditPassword.getText().toString();

            // Create user from
            User user = new User(name, email, password);

            url += "?email=" + email + "&password=" + password;
            new AddUserWebTask().execute(url);
        }
    }

    public void onRegistrationSuccess() {
        progressDialog.dismiss();
        Toast.makeText(this, "User account created!", Toast.LENGTH_SHORT).show();
        mRegisterButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onRegistrationFailed() {
        progressDialog.dismiss();
        Toast.makeText(this, "There was an error in registration", Toast.LENGTH_SHORT).show();
        mRegisterButton.setEnabled(true);
    }

    public boolean validate() {

        Log.d(TAG, "Starting validations...");
        String name = mEditName.getText().toString();
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();
        String confirmPassword = mEditConfirmPassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mEditName.setError("Name should be at least 3 characters long.");
            Log.d(TAG, "Error in name");
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditEmail.setError("invalid email address");
            Log.d(TAG, "Error in email");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            mEditConfirmPassword.setError("Passwords do not match!");
            Log.d(TAG, "Error in password");
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mEditPassword.setError("Password must be between 4 to 10 alphanumeric characters");
            Log.d(TAG, "Error in confirming password");
            return false;
        }

        Log.d(TAG, "Successfully validated");
        return true;
    }


    private class AddUserWebTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "AddUserWebTask";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                Log.d(TAG, "The string is: " + contentAsString);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                Log.d(TAG, "Something happened" + e.getMessage());
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return null;
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("result");
                if (status.equalsIgnoreCase("success")) {
                    onRegistrationSuccess();
                } else {
                    String reason = jsonObject.getString("error");
                    Log.d(TAG, "Failed registration: " + reason);
                    onRegistrationFailed();
                }
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception " + e.getMessage());
                onRegistrationFailed();
            }
        }
    }

}
