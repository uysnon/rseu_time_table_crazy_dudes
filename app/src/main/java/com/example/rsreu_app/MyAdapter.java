package com.example.rsreu_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rsreu_app.model.MyItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
   private List<MyItem> myItemList = new ArrayList<>();
   DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
   OnButtonItemClickListener buttonItemClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
     class MyViewHolder extends RecyclerView.ViewHolder {

        OnButtonItemClickListener buttonItemClickListener;
        // each data item is just a string in this case
        private TextView title;
        private TextView url;
        private TextView summary;
        private TextView date;
        private TextView author;
        private ImageView image;
        private Button button;

        public MyViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.itemTitle);
            url = v.findViewById(R.id.itemUrl);
            summary = v.findViewById(R.id.itemSummary);
            date = v.findViewById(R.id.itemDate);
            author = v.findViewById(R.id.itemAuthor);
            image = v.findViewById(R.id.itemImage);
            button = v.findViewById(R.id.fullItem);
        }

        public Button getButton() {
            return button;
        }

        public void bind(MyItem myItem){
            title.setText(myItem.getTitle());
            url.setText(myItem.getUrl());
            summary.setText(myItem.getSummary());
            String creationDateFormatted = getFormattedDate(myItem.getDate());
            date.setText(creationDateFormatted);
            author.setText(myItem.getAuthor());
            image.setImageBitmap(dbBitmapUtility.getImage(myItem.getImg()));
        }



        private String getFormattedDate(String rawDate){

            SimpleDateFormat formatFrom = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",Locale.US);
            try {
                java.util.Date tmpDate = formatFrom.parse(rawDate);
                SimpleDateFormat formatTo = new SimpleDateFormat("dd/MMM/yyyy");
                return formatTo.format(tmpDate);
            }catch (Exception e){
                return "error";
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
  /*  public MyAdapter(List<MyItem> mItemList) {
        myItemList = mItemList;
    }*/

    public void setButtonItemClickListener(OnButtonItemClickListener buttonItemClickListener){
        this.buttonItemClickListener = buttonItemClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_view,parent,false);
         return new MyAdapter.MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(buttonItemClickListener != null){
            holder.getButton().setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    buttonItemClickListener.onButtonIsClick(v,position);
                }
            });
        }


        holder.bind(myItemList.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myItemList.size();
    }


    public void setItems(Collection<MyItem> items){
         myItemList.addAll(items);
         notifyDataSetChanged();
    }

    public void clearItems() {
        myItemList.clear();
        notifyDataSetChanged();
    }
}
