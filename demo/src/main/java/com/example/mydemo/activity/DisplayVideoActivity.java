package com.example.mydemo.activity;

import java.io.File;
import java.io.FileInputStream;

import  com.example.mydemo.R;
import com.example.mydemo.adapter.ChatMsgListAdapter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;


 public class DisplayVideoActivity  extends MyBaseActivity {
	private final static String TAG = DisplayVideoActivity.class.getSimpleName();
	private SurfaceView surfaceView;
	 private MediaPlayer mediaPlayer;
	 private String filename;
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_video);		
		initView();	
	}
	
	public void initView() {	
		
		 filename = getIntent().getStringExtra("filePath");
		Log.d(TAG,"init ivew:" + filename);		
		surfaceView = (SurfaceView) findViewById(R.id.sf_video);
	//	 surfaceView.getHolder().setFixedSize(640, 480);//设置分辨率
	     DisplayMetrics dm = new DisplayMetrics();getWindowManager().getDefaultDisplay().getMetrics(dm);
	        int width = dm.widthPixels;
	        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(width, width*4/3));
		 surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        surfaceView.getHolder().addCallback(new SurceCallBack());
	        mediaPlayer=new MediaPlayer();
	        
	        surfaceView.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
		            if(mediaPlayer.isPlaying()){
		                mediaPlayer.stop();
		            }
		            finish();
				}
	        });
		
	}
	 private final class SurceCallBack implements SurfaceHolder.Callback{
	        /**
	         * 画面修改
	         */
	        @Override
	        public void surfaceChanged(SurfaceHolder holder, int format, int width,
	                int height) {
	            // TODO Auto-generated method stub
	            
	        }

	        /**
	         * 画面创建
	         */
	        @Override
	        public void surfaceCreated(SurfaceHolder holder) {
	            if(filename!=null){
	                try {
	                    File file=new File(filename);
	                    mediaPlayer.reset();//重置为初始状态
	                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音乐流的类型	                    
	                    mediaPlayer.setDisplay(surfaceView.getHolder());//设置video影片以surfaceviewholder播放
	                    mediaPlayer.setDataSource(file.getAbsolutePath());//设置路径
	                    mediaPlayer.prepare();//缓冲
	                    mediaPlayer.start();//播放
	                } catch (Exception e) {
	                    Log.e(TAG, e.toString());
	                    e.printStackTrace();
	                }
	            }
	            
	        }

	        /**
	         * 画面销毁
	         */
	        @Override
	        public void surfaceDestroyed(SurfaceHolder holder) {
	            if(mediaPlayer.isPlaying()){
	                mediaPlayer.stop();
	            }
	        }
	    }
}
