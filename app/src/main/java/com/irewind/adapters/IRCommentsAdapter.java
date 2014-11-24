package com.irewind.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.Comment;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IRCommentsAdapter extends ArrayAdapter<Comment> {
    public interface ActionListener {
        void addComment();
        void replyComment(Comment parentComment);
    }

    private List<Comment> comments;
    private int layout;
    private Context mContext;
    private ImageLoader imageLoader;
    private String profileImage;
    private ActionListener actionListener;

    public IRCommentsAdapter(Context context, int resource, ImageLoader imageLoader) {
        super(context, resource);
        mContext = context;
        layout = resource;
        this.imageLoader = imageLoader;
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public int getCount() {
        return (comments != null ? comments.size() : 0) + 1;
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
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.addComment = (TextView) convertView.findViewById(R.id.addComment);
//            holder.delete = (ImageButton) convertView.findViewById(R.id.delete);
            holder.pictureTop = (RoundedImageView) convertView.findViewById(R.id.profileImageTop);
            holder.picture = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            holder.addCommentRow = (LinearLayout) convertView.findViewById(R.id.addCommentRow);
            holder.commentRow = (RelativeLayout) convertView.findViewById(R.id.commentLayout);
            holder.childCommentRow = (RelativeLayout) convertView.findViewById(R.id.childCommentLayout);

            convertView.setTag(holder);
        } else {
            holder = (CommentHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.addCommentRow.setVisibility(View.VISIBLE);
            holder.commentRow.setVisibility(View.GONE);
            holder.childCommentRow.setVisibility(View.GONE);

            if (profileImage != null && profileImage.length() > 0) {
                imageLoader.displayImage(profileImage, holder.picture);
            } else {
                holder.picture.setImageResource(R.drawable.img_default_picture);
            }

            holder.addComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionListener != null) {
                        actionListener.addComment();
                    }
                }
            });

        } else {
            holder.addCommentRow.setVisibility(View.GONE);

            final Comment comment = getItem(position - 1);
            User user = comment.getUser();

            if (comment.isChildComment()) {
                holder.commentRow.setVisibility(View.GONE);
                holder.childCommentRow.setVisibility(View.VISIBLE);
            }
            else {
                holder.commentRow.setVisibility(View.VISIBLE);
                holder.childCommentRow.setVisibility(View.GONE);

                holder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionListener != null) {
                            actionListener.replyComment(comment);
                        }
                    }
                });

                holder.reply.setTag(comment);
            }

            holder.username.setText(comment.getUser().getDisplayName());

            if (user.getPicture() != null && user.getPicture().length() > 0) {
                imageLoader.displayImage(user.getPicture(), holder.picture);
            } else {
                holder.picture.setImageResource(R.drawable.img_default_picture);
            }

            holder.content.setText(comment.getContent());
            holder.date.setText(DateUtils.getRelativeTimeSpanString(comment.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
        }

        return convertView;
    }

    private class CommentHolder {
        TextView username, date, content, reply, addComment;
        //        ImageButton delete;
        RoundedImageView pictureTop, picture;
        LinearLayout addCommentRow;
        RelativeLayout commentRow;
        RelativeLayout childCommentRow;
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setComments(List<Comment> comments) {
        ArrayList<Comment> commentList = new ArrayList<Comment>();

        for (Comment comment : comments) {
            commentList.add(comment);
            List<Comment> childComments = comment.getChildren();
            if (childComments != null && childComments.size() > 0) {
                for (Comment childComment : childComments) {
                    childComment.setIsChildComment(true);
                    childComment.setParentCommentId(comment.getId());
                    commentList.add(childComment);
                }
            }
        }

        this.comments = commentList;

        notifyDataSetChanged();
    }

    public void appendComments(List<Comment> comments) {
        if (this.comments == null) {
            this.comments = new ArrayList<Comment>();
        }

        for (Comment comment : comments) {
            this.comments.add(comment);
            List<Comment> childComments = comment.getChildren();
            if (childComments != null && childComments.size() > 0) {
                for (Comment childComment : childComments) {
                    childComment.setIsChildComment(true);
                    childComment.setParentCommentId(comment.getId());
                    this.comments.add(childComment);
                }
            }
        }

        notifyDataSetChanged();
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
