package com.example.rsreu_app;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rsreu_app.model.MySetting;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    ListView listView;
    public List<MySetting> settings = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        String[] textSettings = {
                getActivity().getString(R.string.about_developers)
        };
        int imageSettings[] = {R.drawable.ic_man_in_a_gear_blue};

        listView = view.findViewById(R.id.list);

        MySAdapter mySAdapter = new MySAdapter(getContext(),textSettings,imageSettings);

        listView.setAdapter(mySAdapter);


        /*MySetting setting = new MySetting("О разработчике",  BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.aboutdevelopers));
        settings.add(setting);



        ListViewAdapter listViewAdapter = new ListViewAdapter(getContext(), settings);
        listView.setAdapter(listViewAdapter);*/




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
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
        String myTextSettings[];
        int  myImageSettings[];

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
            View row = layoutInflater.inflate(R.layout.settings_item_view,parent,false);

            ImageView imageView = row.findViewById(R.id.image_settings);
            TextView textView = row.findViewById(R.id.text_settings);
            textView.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.pt_sans_bold));
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorDarkBlue));

            imageView.setImageDrawable(getActivity().getResources().getDrawable(myImageSettings[position]));
            textView.setText(myTextSettings[position]);



            return row;
        }

    }




}
