package com.irewind.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IRRelatedAdapter extends ArrayAdapter<Video> {

    private List<Video> videos;
    private int layout;
    private Context mContext;

    public IRRelatedAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        layout = resource;
    }

    @Override
    public Video getItem(int position) {
        return videos.get(position);
    }

    @Override
    public int getCount() {
        return videos != null ? videos.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        VideoHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, null);
            holder = new VideoHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.likes = (TextView) convertView.findViewById(R.id.likes);
            holder.title = (TextView) convertView.findViewById(R.id.titleList);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.views = (TextView) convertView.findViewById(R.id.views);
            holder.picture = (ImageView) convertView.findViewById(R.id.movieImage);
            convertView.setTag(holder);
        } else {
            holder = (VideoHolder) convertView.getTag();
        }

        Video video = videos.get(position);

        if (video.getThumbnail() != null && video.getThumbnail().length() > 0) {
            Picasso.with(mContext).load(video.getThumbnail()).into(holder.picture);
        } else {
            holder.picture.setImageResource(R.drawable.ic_placeholder);
        }

        holder.title.setText(video.getTitle() != null ? video.getTitle() : "");

        holder.username.setText(video.getAuthorName() != null ? video.getAuthorName() : "");

        holder.views.setText("" + video.getViews());
        holder.likes.setText("" + video.getLikes());

        holder.date.setText(DateUtils.getRelativeTimeSpanString(video.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));

        return convertView;
    }

    private class VideoHolder {
        TextView title, username, date, likes, views;
        ImageView picture;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    public void appendVideos(List<Video> videos) {
        if (this.videos == null) {
            this.videos = new ArrayList<Video>(videos);
        } else {
            this.videos.addAll(videos);
        }
        notifyDataSetChanged();
    }
}
