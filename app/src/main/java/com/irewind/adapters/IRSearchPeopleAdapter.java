package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class IRSearchPeopleAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private int layout;
    private Context mContext;
    private DateFormat dateFormat;

    public IRSearchPeopleAdapter(Context context, int resource) {
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
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.picture = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            holder.select = (Button) convertView.findViewById(R.id.select);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.select.setSoundEffectsEnabled(false);
            convertView.setTag(holder);
        } else {
            holder = (UserHolder) convertView.getTag();
        }

        User user = getItem(position);

        holder.select.setOnClickListener(new CustomSelectPeopleClickListener(position, holder.checkBox));

        return convertView;
    }

    private class UserHolder {
        TextView name, location;
        RoundedImageView picture;
        CheckBox checkBox;
        Button select;
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

    private class CustomSelectPeopleClickListener implements View.OnClickListener {

        private int position;
        private CheckBox checkBox;

        public CustomSelectPeopleClickListener(int position, CheckBox checkBox) {
            this.position = position;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View v) {
            if (checkBox.isChecked()){
                checkBox.setChecked(false);
                //TODO remove people to selected people using position
            } else {
                checkBox.setChecked(true);
//                users.get(position);
                //TODO add people to selected people using position transmited
            }
        }
    }
}
