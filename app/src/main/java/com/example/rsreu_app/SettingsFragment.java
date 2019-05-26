package com.example.rsreu_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SettingsFragment extends Fragment {

    ListView listView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        String[] textSettings = { getActivity().getString(R.string.contacts),getActivity().getString(R.string.about_application), getActivity().getString(R.string.about_developers)};
        int imageSettings[] = { R.drawable.ic_contacts_blue_24dp ,R.drawable.ic_phonelink_setup_blue_24dp, R.drawable.ic_man_in_a_gear_blue};

        listView = view.findViewById(R.id.list);

        MySAdapter mySAdapter = new MySAdapter(getContext(),textSettings,imageSettings);

        listView.setAdapter(mySAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    mBuilder.setCancelable(true);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_contacts,null);
                    Button mButton = mView.findViewById(R.id.build_way);
                    Context context = getContext();
                    Intent intent = new Intent(getActivity(),MapsActivity.class);
                    mButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if(isGooglePlayServicesAvailable(context)) {
                                startActivity(intent);
                            } else{
                                Toast.makeText(context,"Google Play Services недоступны.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }


                if(position == 1){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    mBuilder.setCancelable(true);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_about_application,null);

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }


                if(position == 2){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    mBuilder.setCancelable(true);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_about_developers,null);

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
            }
        });

        return view;


    }

    class MySAdapter extends ArrayAdapter<String>{
        Context context;
        String[] myTextSettings;
        int[]  myImageSettings;

        public MySAdapter(Context context, String[] titles, int[] images) {
            super(context, R.layout.settings_item_view, R.id.text_settings, titles);
            this.context = context;
            this.myTextSettings = titles;
            this.myImageSettings = images;
        }

        @Nullable
        @Override
        public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View row = layoutInflater.inflate(R.layout.settings_item_view, parent,false);

            ImageView imageView = row.findViewById(R.id.image_settings);
            TextView textView = row.findViewById(R.id.text_settings);
            textView.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.pt_sans_bold));
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorDarkBlue));

            imageView.setImageDrawable(getActivity().getResources().getDrawable(myImageSettings[position]));
            textView.setText(myTextSettings[position]);



            return row;
        }

    }

    public boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }




}
