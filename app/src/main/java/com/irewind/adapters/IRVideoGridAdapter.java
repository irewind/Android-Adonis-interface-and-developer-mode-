package com.irewind.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.EllipsingTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IRVideoGridAdapter extends BaseAdapter {

    private List<Video> videos;
    private Context mContext;
    private int mResourceid;
    private ImageLoader imageLoader;

    public IRVideoGridAdapter(Context context, int resourceId, ImageLoader imageLoader) {
        this.mContext = context;
        this.mResourceid = resourceId;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return videos != null ? videos.size() : 0;
    }

    @Override
    public Video getItem(int position) {
        return videos.get(position);
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

        Video video = videos.get(position);

        if (video.getThumbnail() != null && video.getThumbnail().length() > 0) {
            imageLoader.displayImage(video.getThumbnail(), holder.image);
        }
        else {
            holder.image.setImageResource(R.drawable.ic_launcher);
        }

        holder.title.setText(video.getTitle() != null ? video.getTitle() : "");

        holder.username.setText(video.getAuthorName() != null ? video.getAuthorName() : "");

        holder.date.setText(DateUtils.getRelativeTimeSpanString(video.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));

        return v;
    }

    class GalleryHolder {
        ImageView image;
        TextView username, date;
        EllipsingTextView title;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    public void appendVideos(List<Video> videos) {
        if (this.videos == null) {
            this.videos = new ArrayList<Video>(videos);
        }
        else {
            this.videos.addAll(videos);
        }
        notifyDataSetChanged();
    }
}
