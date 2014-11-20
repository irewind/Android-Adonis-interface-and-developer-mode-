package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.models.RelatedItem;
import com.irewind.sdk.model.Video;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tzmst on 5/7/14.
 */
public class IRRelatedAdapter extends ArrayAdapter<Video> {

    private List<Video> videos;
    private int layout;
    private Context mContext;
    private ImageLoader imageLoader;

    public IRRelatedAdapter(Context context, int resource, ImageLoader imageLoader) {
        super(context, resource);
        mContext = context;
        layout = resource;
        this.imageLoader = imageLoader;
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
            imageLoader.displayImage(video.getThumbnail(), holder.picture);
        }
        else {
            holder.picture.setImageResource(R.drawable.ic_launcher);
        }

        holder.title.setText(video.getTitle() != null ? video.getTitle() : "");

        holder.username.setText(video.getAuthorName() != null ? video.getAuthorName() : "");

        holder.views.setText("" + video.getViews());
        holder.likes.setText("" + video.getLikes());

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
        }
        else {
            this.videos.addAll(videos);
        }
        notifyDataSetChanged();
    }
}
