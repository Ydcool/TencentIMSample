package com.example.mydemo.adapter;

import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.example.mydemo.R;
import com.example.mydemo.utils.EmojiUtil;


public class EmojiAdapter extends BaseAdapter {
	private List<String> data;
    private LayoutInflater inflater;

    private int size=0;

    public EmojiAdapter(Context context, List<String> list) {
        this.inflater=LayoutInflater.from(context);
        this.data=list;
        this.size=list.size();
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	String emoji=data.get(position);
        ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_face, null);
            viewHolder.iv_face=(ImageView)convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        if(emoji.equals(EmojiUtil.EMOJI_DELETE_NAME)) {
            viewHolder.iv_face.setImageResource(R.drawable.qb_tenpay_del);
        } else if(TextUtils.isEmpty(emoji)) {
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource(EmojiUtil.getInstace().mEmojis.get(emoji));
        }

        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }
}