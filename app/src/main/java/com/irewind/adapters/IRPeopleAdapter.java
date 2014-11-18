package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.models.PeopleItem;
import com.irewind.ui.views.RoundedImageView;

import java.util.List;

/**
 * Created by tzmst on 5/7/14.
 */
public class IRPeopleAdapter extends ArrayAdapter<PeopleItem> {

    private List<PeopleItem> dataList;
    private int layout;
    private Context mContext;

    public IRPeopleAdapter(Context context, int resource, List<PeopleItem> data) {
        super(context, resource);
        mContext = context;
        layout = resource;
        dataList = data;
    }

    @Override
    public PeopleItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AccountItemHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, null);
            holder = new AccountItemHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.videos = (TextView) convertView.findViewById(R.id.videos);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.picture = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            convertView.setTag(holder);
        } else {
            holder = (AccountItemHolder) convertView.getTag();
        }

        PeopleItem info = getItem(position);

        return convertView;
    }

    private class AccountItemHolder {
        TextView name, videos, date;
        RoundedImageView picture;
    }
}
