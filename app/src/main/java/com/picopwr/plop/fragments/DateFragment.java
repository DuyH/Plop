package com.picopwr.plop.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.picopwr.plop.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateFragment extends Fragment {


    private String mTitle;
    private int mPage;

    public static DateFragment newInstance(int page, String title) {
        DateFragment dateFragment= new DateFragment();
        Bundle args = new Bundle();
        args.putInt("position", page);
        args.putString("title", title);
        dateFragment.setArguments(args);
        return dateFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("position", 1);
        mTitle = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Do the real work of this fragment.

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_date, container, false);
        TextView textDate = (TextView) v.findViewById(R.id.textDate);
        textDate.setText("Date is " + (month + 1) + "/" + day + "/" + year + "This is page " + mPage);

        // Return view
        return v;
    }

}
