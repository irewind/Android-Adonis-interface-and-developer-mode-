package com.irewind.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.Comment;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.AutoResizeTextView;
import com.irewind.ui.views.RoundedImageView;
import com.squareup.picasso.Picasso;

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
    private String profileImage;
    private ActionListener actionListener;

    public IRCommentsAdapter(Context context, int resource) {
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
        return (comments != null ? comments.size() : 0) + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommentHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, null);
            holder = new CommentHolder();

            holder.addCommentHolder.rootViewGroup = (ViewGroup) convertView.findViewById(R.id.addCommentRowLayout);
            holder.addCommentHolder.picture = (RoundedImageView) holder.addCommentHolder.rootViewGroup.findViewById(R.id.profileImageAdd);
            holder.addCommentHolder.addCommentTextView = (TextView) holder.addCommentHolder.rootViewGroup.findViewById(R.id.addCommentTextView);

            holder.parentCommentHolder.rootViewGroup = (ViewGroup) convertView.findViewById(R.id.parentCommentLayout);
            holder.parentCommentHolder.picture = (RoundedImageView) holder.parentCommentHolder.rootViewGroup.findViewById(R.id.profileImage);
            holder.parentCommentHolder.username = (AutoResizeTextView) holder.parentCommentHolder.rootViewGroup.findViewById(R.id.username);
            holder.parentCommentHolder.date = (AutoResizeTextView) holder.parentCommentHolder.rootViewGroup.findViewById(R.id.date);
            holder.parentCommentHolder.content = (TextView) holder.parentCommentHolder.rootViewGroup.findViewById(R.id.content);
            holder.parentCommentHolder.reply = (AutoResizeTextView) holder.parentCommentHolder.rootViewGroup.findViewById(R.id.reply);

            holder.childCommentHolder.rootViewGroup = (ViewGroup) convertView.findViewById(R.id.childCommentLayout);
            holder.childCommentHolder.picture = (RoundedImageView) holder.childCommentHolder.rootViewGroup.findViewById(R.id.profileImageChild);
            holder.childCommentHolder.username = (AutoResizeTextView) holder.childCommentHolder.rootViewGroup.findViewById(R.id.usernameChild);
            holder.childCommentHolder.date = (AutoResizeTextView) holder.childCommentHolder.rootViewGroup.findViewById(R.id.dateChild);
            holder.childCommentHolder.content = (TextView) holder.childCommentHolder.rootViewGroup.findViewById(R.id.contentChild);

            convertView.setTag(holder);
        } else {
            holder = (CommentHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.addCommentHolder.rootViewGroup.setVisibility(View.VISIBLE);
            holder.parentCommentHolder.rootViewGroup.setVisibility(View.GONE);
            holder.childCommentHolder.rootViewGroup.setVisibility(View.GONE);

            if (profileImage != null && profileImage.trim().length() > 0) {
                Picasso.with(mContext).load(profileImage).placeholder(R.drawable.img_default_picture).into(holder.addCommentHolder.picture);
            } else {
                holder.addCommentHolder.picture.setImageResource(R.drawable.img_default_picture);
            }

            holder.addCommentHolder.addCommentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionListener != null) {
                        actionListener.addComment();
                    }
                }
            });

        } else {
            holder.addCommentHolder.rootViewGroup.setVisibility(View.GONE);

            final Comment comment = getItem(position - 1);
            User user = comment.getUser();

            if (comment.isChildComment()) {
                holder.parentCommentHolder.rootViewGroup.setVisibility(View.GONE);
                holder.childCommentHolder.rootViewGroup.setVisibility(View.VISIBLE);

                holder.childCommentHolder.username.setText(comment.getUser().getDisplayName());

                if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
                    Picasso.with(mContext).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(holder.childCommentHolder.picture);
                } else {
                    holder.childCommentHolder.picture.setImageResource(R.drawable.img_default_picture);
                }

                holder.childCommentHolder.content.setText(comment.getContent());
                holder.childCommentHolder.date.setText(DateUtils.getRelativeTimeSpanString(comment.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
            } else {
                holder.parentCommentHolder.rootViewGroup.setVisibility(View.VISIBLE);
                holder.childCommentHolder.rootViewGroup.setVisibility(View.GONE);

                holder.parentCommentHolder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionListener != null) {
                            actionListener.replyComment(comment);
                        }
                    }
                });

                holder.parentCommentHolder.username.setText(comment.getUser().getDisplayName());

                if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
                    Picasso.with(mContext).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(holder.parentCommentHolder.picture);
                } else {
                    holder.parentCommentHolder.picture.setImageResource(R.drawable.img_default_picture);
                }

                holder.parentCommentHolder.content.setText(comment.getContent());
                holder.parentCommentHolder.date.setText(DateUtils.getRelativeTimeSpanString(comment.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
            }
        }

        return convertView;
    }

    private class CommentHolder {
        AddCommentHolder addCommentHolder = new AddCommentHolder();
        ParentCommentHolder parentCommentHolder = new ParentCommentHolder();
        ChildCommentHolder childCommentHolder = new ChildCommentHolder();
    }

    private class AddCommentHolder {
        ViewGroup rootViewGroup;
        RoundedImageView picture;
        TextView addCommentTextView;
    }

    private class ParentCommentHolder {
        ViewGroup rootViewGroup;
        RoundedImageView picture;
        AutoResizeTextView username, date,reply;
        TextView content;
    }

    private class ChildCommentHolder {
        ViewGroup rootViewGroup;
        RoundedImageView picture;
        AutoResizeTextView username, date;
        TextView content;
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
