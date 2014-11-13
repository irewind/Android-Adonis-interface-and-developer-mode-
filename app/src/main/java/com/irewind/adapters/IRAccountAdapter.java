package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.irewind.R;

import java.util.List;

/**
 * Created by tzmst on 5/7/14.
 */
public class IRAccountAdapter extends ArrayAdapter<String> {

    private List<String> dataList;
    private int layout;
    private Context mContext;

    public IRAccountAdapter(Context context, int resource, List<String> data) {
        super(context, resource);
        mContext = context;
        layout = resource;
        dataList = data;
    }

    @Override
    public String getItem(int position) {
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

            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (AccountItemHolder) convertView.getTag();
        }

        String info = getItem(position);


        holder.title.setText(info);

        return convertView;
    }

    private class AccountItemHolder {
        TextView title;
    }
}
