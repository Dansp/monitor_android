package com.amenuo.monitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amenuo.monitor.R;
import com.amenuo.monitor.lib.SectionedBaseAdapter;
import com.amenuo.monitor.manager.LumpCategoryManager;
import com.amenuo.monitor.manager.LumpManager;
import com.amenuo.monitor.model.LumpCategoryModel;
import com.amenuo.monitor.model.LumpModel;
import com.amenuo.monitor.utils.PLog;

import java.util.List;

/**
 * Created by laps on 7/19/16.
 */
public class FollowAdapter extends SectionedBaseAdapter implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<LumpCategoryModel> lumpCategoryModels;

    public FollowAdapter(Context context) {
        this.mContext = context;
        this.lumpCategoryModels = LumpCategoryManager.getInstance().getLumpCategoryList();
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void notifyDataSetChanged() {
        lumpCategoryModels = LumpCategoryManager.getInstance().getLumpCategoryList();
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int section, int position) {
        return lumpCategoryModels.get(section).getLumpModels().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return ((LumpModel) getItem(section, position)).getId();
    }

    @Override
    public int getSectionCount() {
        return lumpCategoryModels.size();
    }

    @Override
    public int getCountForSection(int section) {
        return lumpCategoryModels.get(section).getLumpModels().size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {

        ImageView iconImageView = null;
        TextView nameTextView = null;
        TextView descTextView = null;
        Button followButton = null;
        View dividerView = null;
        View view = null;
        LumpViewHolder viewHolder = null;
        LumpModel lumpModel = (LumpModel) getItem(section, position);
        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.layout_follow_list_item, null);
            iconImageView = (ImageView) view.findViewById(R.id.follow_list_item_icon);
            nameTextView = (TextView) view.findViewById(R.id.follow_list_item_name);
            descTextView = (TextView) view.findViewById(R.id.follow_list_item_desc);
            followButton = (Button) view.findViewById(R.id.follow_list_item_follow);
            followButton.setOnClickListener(this);
            dividerView = view.findViewById(R.id.follow_list_item_divider);
            viewHolder = new LumpViewHolder();
            viewHolder.setIconImageView(iconImageView);
            viewHolder.setDescTextView(descTextView);
            viewHolder.setNameTextView(nameTextView);
            viewHolder.setFollowButton(followButton);
            viewHolder.setDividerView(dividerView);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (LumpViewHolder) view.getTag();
            iconImageView = viewHolder.getIconImageView();
            nameTextView = viewHolder.getNameTextView();
            descTextView = viewHolder.getDescTextView();
            followButton = viewHolder.getFollowButton();
            dividerView = viewHolder.getDividerView();
        }
//        viewHolder.setLumpModel(lumpModel);
        iconImageView.setImageResource(lumpModel.getIconResId());
        iconImageView.setBackgroundResource(lumpModel.getBgDrawableId());
        nameTextView.setText(lumpModel.getName());
        descTextView.setText(lumpModel.getDesc());
        followButton.setSelected(lumpModel.isFollowed());
        followButton.setTag(lumpModel);
        if (position == getCountForSection(section) - 1) {
            dividerView.setVisibility(View.GONE);
        } else {
            dividerView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.layout_follow_list_section, null);
        TextView nameTextView = (TextView) view.findViewById(R.id.follow_list_section_text);
        nameTextView.setText(lumpCategoryModels.get(section).getName());
        return view;
    }

    @Override
    public void onClick(View v) {
        PLog.e("follow adapter onClick :" + Thread.currentThread().getName());
        LumpModel lumpModel = (LumpModel) v.getTag();
        if (lumpModel != null) {
            Boolean isFollowed = lumpModel.isFollowed();
            lumpModel.setFollowed(!isFollowed);
            isFollowed = !isFollowed;
            if (isFollowed) {
                LumpManager.getInstance().addLump(lumpModel);
            } else {
                LumpManager.getInstance().removeLump(lumpModel.getName());
            }
            this.notifyDataSetChanged();
        }
    }

}
