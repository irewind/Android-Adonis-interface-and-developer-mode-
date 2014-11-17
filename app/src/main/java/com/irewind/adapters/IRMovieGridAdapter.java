package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.models.MovieGridItem;
import com.irewind.ui.views.EllipsingTextView;

import java.util.List;

public class IRMovieGridAdapter extends BaseAdapter {

    private List<MovieGridItem> dataList;
    private Context mContext;
    private int mResourceid;

    public IRMovieGridAdapter(Context context, int resourceId, List<MovieGridItem> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        this.mResourceid = resourceId;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public MovieGridItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        View v = convertView;
        GalleryHolder holder;
        if (v == null) {
            LayoutInflater li = LayoutInflater.from(mContext);

            v = li.inflate(mResourceid, null);
            holder = new GalleryHolder();

            holder.image = (ImageView) v.findViewById(R.id.movieImage);
            holder.username = (TextView) v.findViewById(R.id.username);
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.title = (EllipsingTextView) v.findViewById(R.id.titleGrid);

            v.setTag(holder);
        } else {
            holder = (GalleryHolder) v.getTag();
        }

        MovieGridItem info = dataList.get(position);

        holder.title.setText(info.getTitle());

        return v;
    }

    class GalleryHolder {
        ImageView image;
        TextView username, date;
        EllipsingTextView title;
    }
}