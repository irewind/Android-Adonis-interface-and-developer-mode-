package com.irewind.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IRPeopleAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private int layout;
    private Context mContext;
    private DateFormat dateFormat;

    public IRPeopleAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        layout = resource;

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
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.picture = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            convertView.setTag(holder);
        } else {
            holder = (UserHolder) convertView.getTag();
        }

        User user = getItem(position);

        if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
            Picasso.with(mContext).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(holder.picture);
        } else {
            holder.picture.setImageResource(R.drawable.img_default_picture);
        }

        holder.name.setText(user.getDisplayName());

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date parsedDate = fromUser.parse(user.getLastLoginDate());
            if (parsedDate.getTime() > 0) {
                holder.date.setText(DateUtils.getRelativeTimeSpanString(parsedDate.getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
            } else {
                holder.date.setText("");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.date.setText("");
        }


      //  holder.date.setText(user.getLastLoginDate());


        return convertView;
    }

    private class UserHolder {
        TextView name, date;
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
