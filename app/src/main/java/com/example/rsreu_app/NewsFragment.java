package com.example.rsreu_app;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rsreu_app.model.MyItem;

import java.util.Arrays;
import java.util.Collection;

public class NewsFragment extends Fragment {


    DatabaseHelper myDB;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initRecyclerView(view);
        loadItems();

        return view;

    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         mAdapter = new MyAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    private void loadItems() {
        Collection<MyItem> items = getItems();
        mAdapter.setItems(items);
    }

    private  Collection<MyItem> getItems(){

        myDB = new DatabaseHelper(getContext());
        int newsCount = myDB.getNewsCount();

        int i = 0;

        MyItem[] array = new MyItem[newsCount];
            Cursor c;
            try{
                c = myDB.getAllNews();
               if(c.moveToFirst()) {
                   while(!c.isAfterLast()) {
                       array[i] = new MyItem("","","","","",null);
                       Log.d("SecretLogs", c.getString(c.getColumnIndex("url")));
                       array[i].setUrl(c.getString(c.getColumnIndex("url")));
                       Log.d("SecretLogs", c.getString(c.getColumnIndex("title")));
                       array[i].setTitle(c.getString(c.getColumnIndex("title")));
                       Log.d("SecretLogs", c.getString(c.getColumnIndex("summary")));
                       array[i].setSummary(c.getString(c.getColumnIndex("summary")));
                       Log.d("SecretLogs", c.getString(c.getColumnIndex("date")));
                       array[i].setDate(c.getString(c.getColumnIndex("date")));
                       Log.d("SecretLogs", c.getString(c.getColumnIndex("author")));
                       array[i].setAuthor(c.getString(c.getColumnIndex("author")));
                       array[i].setImg(c.getBlob(c.getColumnIndex("image")));
                       i++;
                       c.moveToNext();
                   }
               }

               c.close();

            }catch (CursorIndexOutOfBoundsException e) {
                Log.e("LogError", "Error");
            }


        return Arrays.asList(array);
    }
}
