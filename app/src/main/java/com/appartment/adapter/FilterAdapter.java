package com.appartment.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appartment.R;
import com.appartment.model.Filter;
import com.appartment.model.FilterHeader;
import com.appartment.model.FilterItem;

import java.util.ArrayList;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.SettingsViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private static MyClickListener myClickListener;
    private Activity activity;
    private Context context;
    private ArrayList<Filter> bambooSettingsList;

    public FilterAdapter(Activity mActivity, Context context, ArrayList<Filter> bambooSettingsList) {

        this.context = context;
        this.activity = mActivity;
        this.bambooSettingsList = bambooSettingsList;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        FilterAdapter.myClickListener = myClickListener;
    }

    @Override
    public int getItemCount() {
        int size = bambooSettingsList.size();
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        return bambooSettingsList.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < bambooSettingsList.size(); i++) {
            Filter settings = bambooSettingsList.get(i);
            if (settings.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_settings_header_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_settings_item, parent, false);
        }
        return new SettingsViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {

        final Filter settings = getItem(position);
        final View itemView = holder.itemView;

        holder.bind(getItem(position), position);

    }

    private void setSettingsItem(final SettingsViewHolder holder, FilterItem settingsItem, final int position) {

        holder.txtSettingsTitle.setText(settingsItem.getName());

        holder.checkBoxFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myClickListener.onItemClick(position, v);
            }
        });

        holder.layoutSettingItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBoxFilter.setChecked(!holder.checkBoxFilter.isChecked());

                myClickListener.onItemClick(position, v);
            }
        });
    }

    private void setSettingsHeaderItem(SettingsViewHolder holder, FilterHeader settingsHeader) {

        holder.txtSettingsHeaderTitle.setText(settingsHeader.getEventHeaderTitle());

    }

    public ArrayList<Filter> getAll() {
        return bambooSettingsList;
    }

    public void addAll(ArrayList<Filter> bambooSettingsList) {
        this.bambooSettingsList.addAll(bambooSettingsList);
        notifyDataSetChanged();
    }

    public void addItem(Filter Settings, int index) {
        bambooSettingsList.add(Settings);
        notifyItemInserted(index);
    }

    public void deleteAll() {
        bambooSettingsList.clear();
        notifyDataSetChanged();
    }

    public Filter getItem(int index) {
        return bambooSettingsList.get(index);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtSettingsHeaderTitle, txtSettingsTitle;
        CheckBox checkBoxFilter;
        LinearLayout layoutSettingItem;

        SettingsViewHolder(View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case VIEW_TYPE_HEADER:
                    txtSettingsHeaderTitle = (TextView) itemView.findViewById(R.id.settings_txt_header);
                    break;

                default:
                    layoutSettingItem = (LinearLayout) itemView.findViewById(R.id.filter_item);
                    txtSettingsTitle = (TextView) itemView.findViewById(R.id.settings_row_txt_alert);
                    checkBoxFilter = (CheckBox) itemView.findViewById(R.id.filter_check_box);
                    break;
            }
        }

        private void bind(Filter settings, int position) {
            if (settings.isHeader) {
                setSettingsHeaderItem(this, ((FilterHeader) settings.objItem));
            } else {
                setSettingsItem(this, ((FilterItem) settings.objItem), position);
            }
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getLayoutPosition(), v);
        }
    }

}
