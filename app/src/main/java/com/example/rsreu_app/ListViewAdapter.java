package com.example.rsreu_app;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rsreu_app.model.MyItem;
import com.example.rsreu_app.model.MySetting;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<MySetting> {

    private Context context;
    private TextView textView;
    private  ImageView imageView;

    private List<MySetting> settings;



    public ListViewAdapter(Context context, List<MySetting> settings) {
        super(context, R.layout.settings_item_view);
        this.context = context;
        this.settings = settings;
    }

    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View settingsItemView = layoutInflater.inflate(R.layout.settings_item_view,parent,false);
        textView = settingsItemView.findViewById(R.id.text_settings);
        imageView = settingsItemView.findViewById(R.id.image_settings);
        textView.setText(settings.get(position).getTextSettings());
        imageView.setImageBitmap(settings.get(position).getImageSettings());

        return settingsItemView;

    }




    public void setSettings(MySetting mySetting){
        settings.add(mySetting);

        Log.d("Setting", mySetting.getTextSettings());

    }

    public void bind(MySetting mySetting){
        textView.setText(mySetting.getTextSettings());
        imageView.setImageBitmap(mySetting.getImageSettings());

        Log.d("myLogs",mySetting.getTextSettings());
    }



}
