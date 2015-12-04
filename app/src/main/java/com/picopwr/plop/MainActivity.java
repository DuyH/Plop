package com.picopwr.plop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.picopwr.plop.model.User;
import com.picopwr.plop.utility.UserLocalStore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // For logcat

    private Button mLogoutButton;
    private TextView mUserText;
    UserLocalStore mUserLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserLocalStore = new UserLocalStore(this);

        User user = mUserLocalStore.getUser();
        mUserText = (TextView) findViewById(R.id.text_name);
        mUserText.setText(user.getName());


        mLogoutButton = (Button) findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Clear out current user from shared pref:
                mUserLocalStore.clearUserData();
                mUserLocalStore.setUserLoggedIn(false);

                // Redirect to login page:
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        //Perhaps make sure user is logged in?
        // if authenticated?
    }

}
