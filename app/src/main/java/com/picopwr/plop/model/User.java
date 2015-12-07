/**
 * Duy Huynh
 * TCSS 450 - Fall 2015
 * Plop! - LogEntry Your Logs
 * User.java
 */

package com.picopwr.plop.model;

/**
 * Represents a user in Plop!
 * Created by duy on 10/26/15.
 */
public class User {


    // Name of the user
    String mName;
    // Email of the user (used as unique identifier)
    String mEmail;
    // User's password for logging in to app
    String mPassword;

    /**
     * A User of Plop!
     *
     * @param name     User's name
     * @param email    User's email
     * @param password User's password
     */
    public User(String name, String email, String password) {
        mName = name;
        mEmail = email;
        mPassword = password;
    }

    /**
     * User constructor
     *
     * @param email    User's email
     * @param password User's password
     */
    public User(String email, String password) {
        mName = "";
        mEmail = email;
        mPassword = password;
    }

    /**
     * Get user's name.
     *
     * @return user's name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Set user's name.
     *
     * @param name user's name.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Get user's email address.
     *
     * @return user's email address.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Set user's email address.
     *
     * @param email user's email address.
     */
    public void setEmail(String email) {
        mEmail = email;
    }

    /**
     * Get user's password.
     *
     * @return user's password.
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * Set user's password.
     *
     * @param password user's password
     */
    public void setPassword(String password) {
        mPassword = password;
    }
}
