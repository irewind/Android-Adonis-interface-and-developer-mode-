package com.irewind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.models.PeopleVideoItem;
import com.irewind.ui.views.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

public class IRSettingsExpandableAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<PeopleVideoItem> mChild;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private List<Boolean> checkedStates;
    private ExpandableListView expandableListView;

    private String[] GROUP = {"Public", "Private", "Some People"};

    public IRSettingsExpandableAdapter(Context context, List<PeopleVideoItem> child, SlidingUpPanelLayout slidingUpPanelLayout,
                                       ExpandableListView expandableListView, List<Boolean> checkedStates) {
        mContext = context;
        this.slidingUpPanelLayout = slidingUpPanelLayout;
        this.mChild = child;
        this.checkedStates = checkedStates;
        this.expandableListView = expandableListView;
    }

    @Override
    public PeopleVideoItem getChild(int groupPosition, int childPosition) {
        return childPosition < mChild.size() ? mChild.get(childPosition):new PeopleVideoItem();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupPosition == 2 ? mChild.size() + 1:0;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View view = convertView;
        ChildHolder holder;

        PeopleVideoItem device = getChild(groupPosition, childPosition);

        view = LayoutInflater.from(mContext).inflate(R.layout.expandable_list_row_child, null);
        holder = new ChildHolder();

        holder.childTextView = (TextView) view.findViewById(R.id.childTitle);
        holder.childImage = (RoundedImageView) view.findViewById(R.id.profileExpandablePicture);
        holder.addPeople = (Button) view.findViewById(R.id.childButton);

        if (groupPosition == 2 && childPosition == mChild.size()){
            holder.childTextView.setText(mContext.getString(R.string.add_another_underline));
            holder.childTextView.setTextColor(mContext.getResources().getColor(R.color.text_red));
            holder.addPeople.setOnClickListener(new CustomAddPeopleListener());
        } else {
            holder.childTextView.setTextColor(mContext.getResources().getColor(android.R.color.black));
            holder.childTextView.setText("Adonis");
            //TODO add info;
        }

        return view;
    }

    public List<Boolean> getCheckedStates(){
        return checkedStates;
    }

    private class ChildHolder {
        TextView childTextView;
        RoundedImageView childImage;
        Button addPeople;
    }

    private class CustomAddPeopleListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            slidingUpPanelLayout.setSlidingEnabled(true);
            slidingUpPanelLayout.expandPanel();
        }
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
        GroupHolder holder;
        view = LayoutInflater.from(mContext).inflate(R.layout.expandable_list_row_group, null);
        holder = new GroupHolder();

        String group = getGroup(groupPosition);

        holder.groupTextView = (TextView) view.findViewById(R.id.groupTitle);
        holder.groupCheckBox = (CheckBox) view.findViewById(R.id.groupCheckbox);
        holder.groupSelectButton = (Button) view.findViewById(R.id.groupSelect);

        holder.groupSelectButton.setOnClickListener(new CustomGroupClickListener(groupPosition));

        if (groupPosition == 2 && checkedStates.get(groupPosition)){
            expandableListView.expandGroup(groupPosition);
        } else if (groupPosition == 2 && !checkedStates.get(groupPosition)){
            expandableListView.collapseGroup(groupPosition);
        }

        holder.groupCheckBox.setChecked(checkedStates.get(groupPosition));
        holder.groupTextView.setText(group);

        return view;
    }

    private class GroupHolder {
        TextView groupTextView;
        CheckBox groupCheckBox;
        Button groupSelectButton;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class CustomGroupClickListener implements View.OnClickListener {

        int groupPosistion;

        public CustomGroupClickListener(int groupPosition) {
            this.groupPosistion = groupPosition;
        }

        @Override
        public void onClick(View v) {
            for (int i=0; i<3; i++){
                if (i == groupPosistion){
                    checkedStates.set(i, true);
                } else {
                    checkedStates.set(i, false);
                }
            }
            notifyDataSetChanged();
        }
    }
}