package com.picopwr.plop.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.picopwr.plop.model.User;

/**
 * Created by duy on 11/4/15.
 */
public class UserLocalStore {

    public static final String SHAREDPREF_NAME = "userDetails";
    SharedPreferences mUserSharedPref;

    public UserLocalStore(Context context) {
        mUserSharedPref = context.getSharedPreferences(SHAREDPREF_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor editor = mUserSharedPref.edit();
        // the following can be daisy-chained
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());
        editor.commit();
    }

    public User getUser() {
        String name = mUserSharedPref.getString("name", "");
        String email = mUserSharedPref.getString("email", "");
        String password = mUserSharedPref.getString("password", "");
        return new User(name, email, password);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = mUserSharedPref.edit();
        editor.putBoolean("loggedin", loggedIn);
        editor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor editor = mUserSharedPref.edit();
        editor.clear().commit();
    }

    public boolean isUserLoggedIn() {
        return mUserSharedPref.getBoolean("loggedin", false);
    }

}
