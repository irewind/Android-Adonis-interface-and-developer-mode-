package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.models.PeopleItem;
import com.irewind.models.RelatedItem;
import com.irewind.ui.views.RoundedImageView;

import java.util.List;

/**
 * Created by tzmst on 5/7/14.
 */
public class IRRelatedAdapter extends ArrayAdapter<RelatedItem> {

    private List<RelatedItem> dataList;
    private int layout;
    private Context mContext;

    public IRRelatedAdapter(Context context, int resource, List<RelatedItem> data) {
        super(context, resource);
        mContext = context;
        layout = resource;
        dataList = data;
    }

    @Override
    public RelatedItem getItem(int position) {
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

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.likes = (TextView) convertView.findViewById(R.id.likes);
            holder.title = (TextView) convertView.findViewById(R.id.titleList);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.views = (TextView) convertView.findViewById(R.id.views);
            holder.picture = (ImageView) convertView.findViewById(R.id.profileImage);
            convertView.setTag(holder);
        } else {
            holder = (AccountItemHolder) convertView.getTag();
        }

        RelatedItem info = getItem(position);

        return convertView;
    }

    private class AccountItemHolder {
        TextView title, username, date, likes, views;
        ImageView picture;
    }
}
