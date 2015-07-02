/**
 * Created by Jesus on 6/1/2015.
 */
package com.yate.phoneapp;

/**
 * Created by Jesus on 6/1/2015.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder>{

    private static final int HEADER_TYPE = 0;
    private static final int ROW_TYPE = 1;

    //Accessed by methods to add strings to views
    private List<String> rows;

    public DrawerAdapter(List<String> rows){
        //settings rows we're taking in to the rows that we have up top
        this.rows = rows;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            return new ViewHolder(view, viewType);//calling code in static class
        }else if (viewType == ROW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            return new ViewHolder(view, viewType);//calling code in static class
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Set different images or text
        if (holder.viewType == ROW_TYPE){
            String rowText = rows.get(position - 1);     //Not counting the header
            holder.textView.setText(rowText);

            //Specific images for specific actions
            switch(rowText){
                case "Home":holder.imageView.setImageResource(R.drawable.ic_home);
                    break;
                case "Favorites": holder.imageView.setImageResource(R.drawable.ic_favorite);
                    break;
                case "Save": holder.imageView.setImageResource(R.drawable.ic_save);
                    break;
                case "Map": holder.imageView.setImageResource(R.drawable.ic_map);
                    break;
                case "Search": holder.imageView.setImageResource(R.drawable.ic_action_search);
                    break;
                case "About": holder.imageView.setImageResource(R.drawable.ic_about);
                    break;

            }
        }
    }

    @Override
    public int getItemCount() {
        return rows.size() + 1; //Again, not counting the header
    }

    //When onCreateViewHolder gets called, it will know that the position exists and it will get the correct type just based on the position
    @Override
    public int getItemViewType(int position){
        if(position == 0)
            return HEADER_TYPE;
        return ROW_TYPE;
    }

    //each viewHolder is eigther a header or item type(row item)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //hold the type of ViewHolder
        protected int viewType;

        @InjectView(R.id.drawer_row_icon) ImageView imageView;
        @InjectView(R.id.drawer_row_text) TextView textView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            this.viewType = viewType;

            if (viewType==ROW_TYPE) {
                ButterKnife.inject(this, itemView);
            }
        }

    }
}
