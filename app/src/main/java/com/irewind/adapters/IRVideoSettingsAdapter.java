package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IRVideoSettingsAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<User> people;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private List<Boolean> accessTypeStates;
    private ExpandableListView expandableListView;

    private String[] GROUP = {"Public", "Private", "Some People"};

    private OptionDelegate optionDelegate;

    public IRVideoSettingsAdapter(Context context, SlidingUpPanelLayout slidingUpPanelLayout,
                                  ExpandableListView expandableListView, List<Boolean> accessTypeStates, List<User> people) {
        mContext = context;
        this.slidingUpPanelLayout = slidingUpPanelLayout;
        this.people = people;
        this.accessTypeStates = accessTypeStates;
        this.expandableListView = expandableListView;
    }

    @Override
    public User getChild(int groupPosition, int childPosition) {
        return childPosition < people.size() ? people.get(childPosition) : null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupPosition == 2 ? people.size() + 1 : 0;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        UserHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expandable_list_row_child, null);
            holder = new UserHolder();
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.profileImage = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            holder.addPeople = (Button) convertView.findViewById(R.id.addButton);

            convertView.setTag(holder);
        } else {
            holder = (UserHolder) convertView.getTag();
        }

        if (groupPosition == 2 && childPosition == people.size()) {
            holder.username.setText(mContext.getString(R.string.add_another_underline));
            holder.username.setTextColor(mContext.getResources().getColor(R.color.text_red));
            holder.addPeople.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slidingUpPanelLayout.setSlidingEnabled(true);
                    slidingUpPanelLayout.expandPanel();
                }
            });
        } else {
            User user = getChild(groupPosition, childPosition);
            if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
                Picasso.with(mContext).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(holder.profileImage);
            } else {
                holder.profileImage.setImageResource(R.drawable.img_default_picture);
            }
            holder.username.setTextColor(mContext.getResources().getColor(android.R.color.black));
            holder.username.setText(user.getDisplayName());
        }

        return convertView;
    }

    public List<Boolean> getAccessTypeStates() {
        return accessTypeStates;
    }

    private class UserHolder {
        RoundedImageView profileImage;
        TextView username;
        Button addPeople;
    }

    @Override
    public String getGroup(int groupPosition) {
        return GROUP[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return GROUP.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        OptionHolder holder;
        view = LayoutInflater.from(mContext).inflate(R.layout.expandable_list_row_group, null);
        holder = new OptionHolder();

        String group = getGroup(groupPosition);

        holder.optionTitle = (TextView) view.findViewById(R.id.groupTitle);
        holder.optionCheckBox = (CheckBox) view.findViewById(R.id.groupCheckbox);
        holder.optionSelectButton = (Button) view.findViewById(R.id.groupSelect);

        holder.optionSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++) {
                    if (i == groupPosition) {
                        accessTypeStates.set(i, true);
                    } else {
                        accessTypeStates.set(i, false);
                    }
                }
                notifyDataSetChanged();

                if (optionDelegate != null) {
                    optionDelegate.setOption(groupPosition);
                }
            }
        });

        if (groupPosition == 2 && accessTypeStates.get(groupPosition)) {
            expandableListView.expandGroup(groupPosition);
        } else if (groupPosition == 2 && !accessTypeStates.get(groupPosition)) {
            expandableListView.collapseGroup(groupPosition);
        }

        holder.optionCheckBox.setChecked(accessTypeStates.get(groupPosition));
        holder.optionTitle.setText(group);

        return view;
    }

    private class OptionHolder {
        TextView optionTitle;
        CheckBox optionCheckBox;
        Button optionSelectButton;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public OptionDelegate getOptionDelegate() {
        return optionDelegate;
    }

    public void setOptionDelegate(OptionDelegate optionDelegate) {
        this.optionDelegate = optionDelegate;
    }

    public interface OptionDelegate {
        void setOption(int optionIndex);
    }
}
