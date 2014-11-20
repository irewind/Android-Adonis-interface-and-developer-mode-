package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IRPeopleAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private int layout;
    private Context mContext;
    private ImageLoader imageLoader;
    private DateFormat dateFormat;

    public IRPeopleAdapter(Context context, int resource, ImageLoader imageLoader) {
        super(context, resource);
        mContext = context;
        layout = resource;
        this.imageLoader = imageLoader;

        dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public int getCount() {
        return users != null ? users.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, null);
            holder = new UserHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.videos = (TextView) convertView.findViewById(R.id.videos);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.picture = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            convertView.setTag(holder);
        } else {
            holder = (UserHolder) convertView.getTag();
        }

        User user = getItem(position);

        if (user.getPicture() != null && user.getPicture().length() > 0) {
            imageLoader.displayImage(user.getPicture(), holder.picture);
        } else {
            holder.picture.setImageResource(R.drawable.img_default_picture);
        }

        String name = "";
        if (user.getFirstname() != null && user.getFirstname().length() > 0) {
            name += user.getFirstname();
        }

        if (user.getLastname() != null && user.getLastname().length() > 0) {
            if (name.length() > 0) {
                name += " ";
            }
            name += user.getLastname();
        }

        if (name.length() == 0 && user.getFullname() != null && user.getFullname().length() > 0) {
            name = user.getFullname();
        }

        if (name.length() == 0 && user.getEmail() != null && user.getEmail().length() > 0) {
            name = user.getEmail();
        }

        holder.name.setText(name);

        if (user.getLastLoginDate() > 0) {
            Date date = new Date(user.getLastLoginDate());
            holder.date.setText(dateFormat.format(date));
        } else {
            holder.date.setText("");
        }

        return convertView;
    }

    private class UserHolder {
        TextView name, videos, date;
        RoundedImageView picture;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void appendUsers(List<User> users) {
        if (this.users == null) {
            this.users = new ArrayList<User>(users);
        }
        else {
            this.users.addAll(users);
        }
        notifyDataSetChanged();
    }
}
