package com.irewind.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.activities.IRCommentActivity;
import com.irewind.sdk.model.Comment;
import com.irewind.ui.views.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class IRCommentsAdapter extends ArrayAdapter<Comment> {

    private List<Comment> comments;
    private int layout;
    private Context mContext;
    private ImageLoader imageLoader;


    public IRCommentsAdapter(Context context, int resource, ImageLoader imageLoader) {
        super(context, resource);
        mContext = context;
        layout = resource;
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public int getCount() {
        return comments != null ? comments.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommentHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, null);
            holder = new CommentHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.reply = (TextView) convertView.findViewById(R.id.reply);
            holder.comments = (TextView) convertView.findViewById(R.id.description);
            holder.addComment = (TextView) convertView.findViewById(R.id.addComment);
            holder.delete = (ImageButton) convertView.findViewById(R.id.delete);
            holder.pictureTop = (RoundedImageView) convertView.findViewById(R.id.profileImageTop);
            holder.picture = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            holder.topRow = (LinearLayout) convertView.findViewById(R.id.firstRow);
            holder.otherRow = (RelativeLayout) convertView.findViewById(R.id.anyLayout);

            convertView.setTag(holder);
        } else {
            holder = (CommentHolder) convertView.getTag();
        }

        Comment comment = getItem(position);

        if (position == 0) {
            holder.topRow.setVisibility(View.VISIBLE);
            holder.otherRow.setVisibility(View.GONE);

            holder.addComment.setOnClickListener(new CustomOnClickAddCommentListener());
        } else {
            holder.topRow.setVisibility(View.GONE);
            holder.otherRow.setVisibility(View.VISIBLE);
            holder.reply.setOnClickListener(new CustomOnClickReplayListener());
            holder.delete.setOnClickListener(new CustomOnClickReplayListener());
        }

        return convertView;
    }

    private class CommentHolder {
        TextView username, date, comments, reply, addComment;
        ImageButton delete;
        RoundedImageView pictureTop, picture;
        LinearLayout topRow;
        RelativeLayout otherRow;
    }

    private class CustomOnClickAddCommentListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent addComment = new Intent(mContext, IRCommentActivity.class);
            mContext.startActivity(addComment);
        }
    }

    private class CustomOnClickReplayListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO reply
        }
    }

    private class CustomOnClickDeleteListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO Delete comment
        }
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void appendComments(List<Comment> comments) {
        if (this.comments == null) {
            this.comments = new ArrayList<Comment>(comments);
        } else {
            this.comments.addAll(comments);
        }
        notifyDataSetChanged();
    }
}
