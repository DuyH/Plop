package com.picopwr.plop.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.picopwr.plop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BristolFragment extends Fragment {

    private String mTitle;
    private int mPage;

    // Bristol images
    Integer[] imageIDs = {
            R.drawable.type1,
            R.drawable.type2,
            R.drawable.type3,
            R.drawable.type4,
            R.drawable.type5,
            R.drawable.type6,
            R.drawable.type7
    };


    public static BristolFragment newInstance() {
        BristolFragment bristolFragment = new BristolFragment();
        Bundle args = new Bundle();
//        args.putInt("position", page);
//        args.putString("title", title);
        bristolFragment.setArguments(args);
        return bristolFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("position", 3);
        mTitle = getArguments().getString("title");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bristol, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridBristol);
        gridView.setAdapter(new ImageAdapter(getActivity()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id)
            {
                Toast.makeText(getActivity(),
                        "pic" + (position + 1) + " selected",
                        Toast.LENGTH_SHORT).show();

                // Retrieve appropriate Bristol Stool type and its descrition
                String[] types = getResources().getStringArray(R.array.bristol_scale);
                String[] descriptions = getResources().getStringArray(R.array.bristol_descriptions);
                String bristolType = types[position + 1];
                String bristolDescription = descriptions[position + 1];

                // Now pass them into shared preferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("bristolType", bristolType);
                editor.putString("bristolDescription", bristolDescription);
                editor.commit();

                // Update text to display Bristol Type chosen
                TextView textBristolType = (TextView) getActivity().findViewById(R.id.textBristolType);
                textBristolType.setText(bristolType);

                // Update text to display Bristol Type Description
                TextView textBristolDescription = (TextView) getActivity().findViewById(R.id.textBristolDescription);
                textBristolDescription.setText(bristolDescription);

                // Return to previous activity maybe?
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.popBackStack();

            }
        });

        return v;

    }

    public class ImageAdapter extends BaseAdapter
    {
        private Context context;

        public ImageAdapter(Context c)
        {
            context = c;
        }

        //---returns the number of images---
        public int getCount() {
            return imageIDs.length;
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(imageIDs[position]);
            return imageView;
        }
    }
}

