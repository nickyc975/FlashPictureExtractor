package com.nickyc975.fpextrcator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class GridViewAdapter extends BaseAdapter {
    Context context;
    List<GridItem> gridItems;

    GridViewAdapter(Context context, List<GridItem> gridItems) {
        this.context = context;
        this.gridItems = gridItems;
    }

    void setData(ArrayList<GridItem> gridItems) {
        this.gridItems = gridItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return gridItems.size();
    }

    @Override
    public Object getItem(int position) {
        return gridItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.grid_item_text);
            holder.imageView = convertView.findViewById(R.id.grid_item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GridItem item = gridItems.get(position);
        holder.textView.setText(item.getTimestamp());
        holder.imageView.setImageURI(item.getImageUri());
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
