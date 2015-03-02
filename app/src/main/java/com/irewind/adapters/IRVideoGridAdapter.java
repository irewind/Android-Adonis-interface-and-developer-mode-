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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IRVideoGridAdapter extends BaseAdapter {

    private List<Video> videos;
    private Context mContext;
    private int mResourceid;

    public IRVideoGridAdapter(Context context, int resourceId) {
        this.mContext = context;
        this.mResourceid = resourceId;
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

            holder.picture = (ImageView) v.findViewById(R.id.movieImage);
            holder.username = (TextView) v.findViewById(R.id.username);
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.title = (EllipsingTextView) v.findViewById(R.id.titleGrid);
            holder.likes = (TextView) v.findViewById(R.id.likes);
            holder.views = (TextView) v.findViewById(R.id.views);

            v.setTag(holder);
        } else {
            holder = (GalleryHolder) v.getTag();
        }

        Video video = videos.get(position);

        if (video.getThumbnail() != null && video.getThumbnail().length() > 0) {
            Picasso.with(mContext).load(video.getThumbnail()).placeholder(R.drawable.ic_placeholder).into(holder.picture);
        }
        else {
            holder.picture.setImageResource(R.drawable.ic_placeholder);
        }

        holder.title.setText(video.getTitle() != null ? video.getTitle() : "");

        holder.username.setText(video.getAuthorName() != null ? video.getAuthorName() : "");

        holder.date.setText(DateUtils.getRelativeTimeSpanString(video.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
        holder.likes.setText(video.getLikes().intValue() - video.getDislikes().intValue() + "");
        holder.views.setText(video.getViews().intValue() + "");

        return v;
    }

    class GalleryHolder {
        ImageView picture;
        TextView username, date;
        EllipsingTextView title;
        TextView likes, views;
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
