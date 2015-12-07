package com.picopwr.plop.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.picopwr.plop.R;
import com.picopwr.plop.fragments.BristolFragment;
import com.picopwr.plop.fragments.DateFragment;
import com.picopwr.plop.fragments.SmartFragmentAdapter;

/**
 * This is for later implementation of the log activity. This will allow users to swipe between data input instead.
 */
public class SlidingLogActivity extends AppCompatActivity {


    // Sliding log adapter
    private SmartFragmentAdapter mSmartFragmentAdapter;

    public static class SlidingPageAdapter extends SmartFragmentAdapter {
        private static int NUM_ITEMS = 7;

        public SlidingPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return DateFragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return DateFragment.newInstance(1, "Page # 2");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return BristolFragment.newInstance();
                case 3: // Fragment # 1 - This will show SecondFragment
                    return DateFragment.newInstance(3, "Page # 4");
                case 4: // Fragment # 1 - This will show SecondFragment
                    return DateFragment.newInstance(4, "Page # 5");
                case 5: // Fragment # 1 - This will show SecondFragment
                    return DateFragment.newInstance(5, "Page # 6");
                case 6: // Fragment # 1 - This will show SecondFragment
                    return DateFragment.newInstance(6, "Page # 7");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            return "Page " + position;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_log);
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setClipToPadding(false); // For preview on other side
        vpPager.setPageMargin(12); // FOr pages on either side of current
        mSmartFragmentAdapter = new SlidingPageAdapter(getSupportFragmentManager());
        vpPager.setAdapter(mSmartFragmentAdapter);
    }


}
