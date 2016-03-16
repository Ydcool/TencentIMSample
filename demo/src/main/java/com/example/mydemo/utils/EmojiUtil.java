package com.example.mydemo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.mydemo.R;


public class EmojiUtil {
	public HashMap<String,Integer> mEmojis = new HashMap<String, Integer>();
	public List<List<String>> mEmojiPageList = new ArrayList<List<String>>();
	public static final String EMOJI_DELETE_NAME = "EMOJI_DELETE_NAME";
	private int pageSize = 20;

	private static EmojiUtil mEmojiUtil;

	

	private EmojiUtil() {

	}

	public static EmojiUtil getInstace() {
		if (mEmojiUtil == null) {
			mEmojiUtil = new EmojiUtil();
		}
		return mEmojiUtil;
	}
	public void initData(){	
		mEmojis.put("[龇牙笑]", R.drawable.f_static_000);
		mEmojis.put("[顽皮]", R.drawable.f_static_001);
		mEmojis.put("[流汗]", R.drawable.f_static_002);
		mEmojis.put("[偷笑]", R.drawable.f_static_003);
		mEmojis.put("[拜拜]", R.drawable.f_static_004);
		mEmojis.put("[敲头]", R.drawable.f_static_005);
		mEmojis.put("[擦汗]", R.drawable.f_static_006);
		mEmojis.put("[我晕]", R.drawable.f_static_007);
		mEmojis.put("[鄙视]", R.drawable.f_static_008);
		mEmojis.put("[大哭]", R.drawable.f_static_009);
		mEmojis.put("[笑哭]", R.drawable.f_static_010);
		mEmojis.put("[嘘嘘]", R.drawable.f_static_011);
		mEmojis.put("[酷]", R.drawable.f_static_012);
		mEmojis.put("[狂笑]", R.drawable.f_static_013);
		mEmojis.put("[委屈]", R.drawable.f_static_014);
		mEmojis.put("[便便]", R.drawable.f_static_015);
		mEmojis.put("[可怜]", R.drawable.f_static_016);
		mEmojis.put("[砍人]", R.drawable.f_static_017);
		mEmojis.put("[可爱]", R.drawable.f_static_018);
		mEmojis.put("[色]", R.drawable.f_static_019);
		mEmojis.put("[害羞]", R.drawable.f_static_020);
		mEmojis.put("[得意]", R.drawable.f_static_021);
		mEmojis.put("[呕吐]", R.drawable.f_static_022);
		mEmojis.put("[微笑]", R.drawable.f_static_023);
		mEmojis.put("[抱抱]", R.drawable.f_static_024);
		mEmojis.put("[尴尬]", R.drawable.f_static_025);
		mEmojis.put("[惊恐]", R.drawable.f_static_026);
		mEmojis.put("[抽烟]", R.drawable.f_static_027);
		mEmojis.put("[坏笑]", R.drawable.f_static_028);
		mEmojis.put("[嘴唇]", R.drawable.f_static_029);
		mEmojis.put("[白眼]", R.drawable.f_static_030);
		mEmojis.put("[傲慢]", R.drawable.f_static_031);
		mEmojis.put("[奋斗]", R.drawable.f_static_032);
		mEmojis.put("[吃惊]", R.drawable.f_static_033);
		mEmojis.put("[疑问]", R.drawable.f_static_034);
		mEmojis.put("[睡觉]", R.drawable.f_static_035);
		mEmojis.put("[邪恶]", R.drawable.f_static_036);
		mEmojis.put("[哈哈]", R.drawable.f_static_037);
		mEmojis.put("[亲亲]", R.drawable.f_static_038);
		
		
		int pageCount = (int) Math.ceil(mEmojis.size() / 20 + 0.1);

		for (int iPage = 0; iPage < pageCount; iPage++) {
			int startIndex = iPage * pageSize;
			int endIndex = startIndex + pageSize;

			if (endIndex > mEmojis.size()) {
				endIndex = mEmojis.size();
			}
	
			List<String> list = new ArrayList<String>();
			int mapNum=0;
			for (Map.Entry<String,Integer> entry : mEmojis.entrySet()) {       
				if(mapNum>=startIndex){					
					list.add(entry.getKey());
			    	if(mapNum == (endIndex-1)){			
			    		break;
			    	}
				}
			    mapNum++;		    
			}    			
			if (list.size() < pageSize) {
				for (int i = list.size(); i < pageSize; i++) {
					String str = new String();
					list.add(str);
				}
			}
			if (list.size() == pageSize) {		
				list.add(EMOJI_DELETE_NAME);
			}
			mEmojiPageList.add(list);
		}	
		
	}
	
	public SpannableString addEmoji(Context context,String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		

		 
		int imageID = (Integer)mEmojis.get(text);			
		Drawable drawable = context.getResources().getDrawable(imageID);   
	    drawable.setBounds(0, 0, 45, 45);
		ImageSpan imageSpan = new ImageSpan(drawable);
		SpannableString spannable = new SpannableString(text);
		spannable.setSpan(imageSpan, 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}
	
	private void replaceImage(Context context,SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();	
			if (matcher.start() < start) {
				continue;
			}
			Integer imageID = mEmojis.get(key);
			if (imageID == null || imageID == 0) {
				continue;
			}

			ImageSpan imageSpan = new ImageSpan(context, imageID);
			int end = matcher.start() + key.length();	
			spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			if (end < spannableString.length()) {				
				replaceImage(context, spannableString, patten, end);
			}
			break;
		}		
	}
	
	public SpannableString getSpannableString(Context context, String str){
		SpannableString spannableString = new SpannableString(str);	
		String express = "\\[[^\\]]+\\]";		
		Pattern patten = Pattern.compile(express, Pattern.CASE_INSENSITIVE);
		try {
			replaceImage(context, spannableString, patten, 0);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;	
	}
		
}