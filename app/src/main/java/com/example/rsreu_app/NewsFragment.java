package com.example.rsreu_app;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NewsFragment extends Fragment {


    DatabaseHelper myDB;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    String URL = "url";

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
        MyItem[] ca = Arrays.copyOf(items.toArray(), items.toArray().length, MyItem[].class);
        List<MyItem> itemsList = new ArrayList<>(Arrays.asList(ca));


        mAdapter.setItems(items);

        mAdapter.setButtonItemClickListener(new OnButtonItemClickListener() {
            @Override
            public void onButtonIsClick(View button, int position) {
                Intent intent = new Intent(getActivity(),MyWebView.class);

                String link = itemsList.get(position).getUrl();
                intent.putExtra(URL, link);
                startActivity(intent);


            }
        });
    }

    private Collection<MyItem> getItems(){

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
                       array[i].setUrl(c.getString(c.getColumnIndex("url")));
                       array[i].setTitle(c.getString(c.getColumnIndex("title")));
                       array[i].setSummary(c.getString(c.getColumnIndex("summary")));
                       array[i].setDate(c.getString(c.getColumnIndex("date")));;
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

            myDB.close();


        return Arrays.asList(array);
    }
}
