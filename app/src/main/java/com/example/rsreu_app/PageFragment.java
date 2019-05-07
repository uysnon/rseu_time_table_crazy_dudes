package com.example.rsreu_app;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rsreu_app.model.Day;
import com.example.rsreu_app.model.Lesson;
import com.example.rsreu_app.model.Week;

import java.util.ArrayList;

public class PageFragment extends Fragment {

    private int pageNumber;



    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        TextView pageHeader = view.findViewById(R.id.textView);

        String header = "Фрагмент " + Integer.toString(pageNumber + 1);
        pageHeader.setText(header);

        return view;
    }


    private String getAnyColumnValueExample(Context context, int weekDay, int timeId, int weekBool, String columnYouWant) {
        DatabaseHelper myDB;
        myDB = new DatabaseHelper(context);

        Cursor c = myDB.getInfo(weekDay, timeId, weekBool);


        return c.getString(c.getColumnIndex("title"));  // columnYouWant вместо title
    }


}
