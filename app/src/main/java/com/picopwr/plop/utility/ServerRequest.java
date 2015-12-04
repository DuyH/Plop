package com.picopwr.plop.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.picopwr.plop.model.User;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by duy on 11/4/15.
 */
public class ServerRequest {

    ProgressDialog mProgressDialog;
    public static final int TIMEOUT = 30000;
    public static final String SERVER_URL = "http://cssgate.insttech.washington.edu/~dhuynh88/Android/users.php";
    private String url = "http://cssgate.insttech.washington.edu/~dhuynh88/Android/addUser.php";


    public ServerRequest(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Connecting...");

    }

    public void storeUserDataInBackground(User user, GetUserCallback callback) {
        mProgressDialog.show();
        new StoreUserDataAsyncTask(user, callback).execute();
    }

    public void fetchUserData(User user, GetUserCallback callback) {
        mProgressDialog.show();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "AddUserWebTask";
        User mUser;
        GetUserCallback mCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback callback) {
            mUser = user;
            mCallback = callback;
        }


        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream inputStream = null;
            int maxContent = 500;

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
                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(inputStream, maxContent);
                Log.d(TAG, "The string is: " + contentAsString);
                return contentAsString;

            } catch (Exception e) {
                Log.d(TAG, "Something happened" + e.getMessage());
            }

            // Make sure to close inputstream once finished
            finally {
                if (inputStream != null) {
                    inputStream.close();
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
                    Log.d(TAG, "User successfully inserted");
//                    Toast.makeText(context, "User successfully inserted",
//                            Toast.LENGTH_SHORT)
//                            .show();
                } else {
                    String reason = jsonObject.getString("error");
                    Log.d(TAG, "User failed to be inserted");
//                    Toast.makeText(context, "Failed :" + reason,
//                            Toast.LENGTH_SHORT)
//                            .show();
                }

//                context.getFragmentManager().popBackStackImmediate();
            } catch (Exception e) {
                Log.d(TAG, "Parsing JSON Exception " + e.getMessage());
            }
        }
    }
}
