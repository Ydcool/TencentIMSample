package com.example.mydemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mydemo.R;
import com.example.mydemo.c2c.UserInfo;

import java.util.HashMap;
import java.util.List;


public class GroupMemberOperaAdapter extends BaseAdapter {

    private static final String TAG = GroupMemberOperaAdapter.class.getSimpleName();
    private List<UserInfo> listUser;
    private static HashMap<Integer, Boolean> isSelected;
    private LayoutInflater inflater;

    public GroupMemberOperaAdapter(Context context, List<UserInfo> list) {

        this.listUser = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listUser.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.create_group_contact_item, null);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.tv_username);
            holder.bselect = (CheckBox) convertView.findViewById(R.id.cb_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserInfo entity = listUser.get(position);
        holder.username.setText(entity.getDisplayUserName());
        if (getIsSelected() != null && getIsSelected().get(position) != null) {
            holder.bselect.setChecked(getIsSelected().get(position));
        }
        return convertView;
    }


    public static class ViewHolder {
        public TextView username;
        public CheckBox bselect;
        ImageView avatar;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        GroupMemberOperaAdapter.isSelected = isSelected;
    }

}


