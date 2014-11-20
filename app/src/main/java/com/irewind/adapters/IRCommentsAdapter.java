package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.models.CommentItem;
import com.irewind.models.RelatedItem;
import com.irewind.ui.views.RoundedImageView;

import java.util.List;

/**
 * Created by tzmst on 5/7/14.
 */
public class IRCommentsAdapter extends ArrayAdapter<CommentItem>{

    private List<CommentItem> dataList;
    private int layout;
    private Context mContext;

    public IRCommentsAdapter(Context context, int resource, List<CommentItem> data) {
        super(context, resource);
        mContext = context;
        layout = resource;
        dataList = data;
    }

    @Override
    public CommentItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommentsItemHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, null);
            holder = new CommentsItemHolder();

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
            holder = (CommentsItemHolder) convertView.getTag();
        }

        CommentItem info = getItem(position);

        if (position == 0){
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

    private class CommentsItemHolder {
        TextView username, date, comments, reply, addComment;
        ImageButton delete;
        RoundedImageView pictureTop, picture;
        LinearLayout topRow;
        RelativeLayout otherRow;
    }

    private class CustomOnClickAddCommentListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //TODO add comments
        }
    }

    private class CustomOnClickReplayListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //TODO reply
        }
    }

    private class CustomOnClickDeleteListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //TODO Delete comment
        }
    }
}
