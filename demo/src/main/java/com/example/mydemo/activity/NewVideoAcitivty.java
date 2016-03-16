package com.example.mydemo.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.mydemo.R;
import com.example.mydemo.utils.Constant;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewVideoAcitivty extends MyBaseActivity implements OnErrorListener, OnInfoListener {
    private SurfaceView mSurfaceview;
    private MediaRecorder mMediaRecorder;
    private SurfaceHolder mSurfaceHolder;
    private File mRecVedioPath;
    private File mVedioFile;
    private TextView timer;

    protected Camera camera;
    protected boolean isPreview;
    
	private Button btnStart;
	private Button btnStop;
	private Button btnCancel;
	

    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private boolean isRecording = false; 
    private String mOutVideoFile = null;
    private String mOutPicFile = null;
    private int recordSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_new_video);

        timer = (TextView) findViewById(R.id.tv_timer);

        mSurfaceview = (SurfaceView) this.findViewById(R.id.sv_video_view);
        timer.setVisibility(View.GONE);
		btnStop = (Button) findViewById(R.id.btn_video_stop);
		btnStart = (Button) findViewById(R.id.btn_video_start);
		btnCancel = (Button) findViewById(R.id.btn_video_cancel);
		btnCancel.setOnClickListener(listener);
		btnStart.setOnClickListener(listener);
		btnStop.setOnClickListener(listener);

        mRecVedioPath = new File(Constant.VEDIO_DIR);
        if (!mRecVedioPath.exists()) {
            mRecVedioPath.mkdirs();
        }

        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    if (isPreview) {
                        camera.stopPreview();
                        isPreview = false;
                    }
                    camera.release();
                    camera = null; 
                }
                mSurfaceview = null;
                mSurfaceHolder = null;
                mMediaRecorder = null;
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();      
                    Size caSize = getCameraSize(camera.getParameters());
                    if(null!=caSize){
                    	   DisplayMetrics dm = new DisplayMetrics();getWindowManager().getDefaultDisplay().getMetrics(dm);
                         int width = dm.widthPixels;
                         mSurfaceview.setLayoutParams(new RelativeLayout.LayoutParams(width, width*caSize.width/caSize.height));
                    }
             //       parameters.setPreviewFrameRate(5); 
              //      parameters.setPictureFormat(PixelFormat.JPEG);             
                    parameters.set("jpeg-quality", 80);
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(holder);
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                    isPreview = true;
                } catch (Exception e) {
                    if (camera!=null) {
    	                camera.stopPreview();
    	                camera.release();
    	                camera = null;
                    }
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(),"无法打开摄像头，请在权限设置中授权", Toast.LENGTH_LONG).show();
                    finish();
                }
                mSurfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int width, int height) {
                mSurfaceHolder = holder;
              
            }            
     
        });        

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

   }

    
    private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_video_stop:
				stopRecord();
				btnStart.setEnabled(true);				
				break;
			case R.id.btn_video_start:
				recordCamera();
				btnStart.setEnabled(false);
				break;
			case R.id.btn_video_cancel:
				releaseRecord();
				
				break;
			}
		}
	};   
    
	private Size getCameraSize(Camera.Parameters parameters){
 	List<Size> sizes = parameters.getSupportedPreviewSizes(); 	
 		int width = 0;
 		int minWidth=300;
 		Size retSize = null;
	     for (Size size : sizes) {
	    	 Log.d(TAG,"size:"+size.width +":"+size.height);
	    	 if((size.width>minWidth && size.width< width) || width==0){
	    		 width = size.width;
	    		 retSize = size;
	    	 }
	    	
	      }
	     return retSize;
	}
	
	private void recordCamera(){
		if(!isRecording){
		    try {
            if (camera!=null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
            second = 0;
            minute = 0;
            hour = 0;
            recordSeconds=0;
            if (mMediaRecorder == null){
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setOnInfoListener(this);
                mMediaRecorder.setOnErrorListener(this);
            }
            else{
                mMediaRecorder.reset();
            }            
            camera = Camera.open();
            if (camera != null) {        
            	 camera.setDisplayOrientation(90);
            	 camera.unlock();
            	 mMediaRecorder.setCamera(camera);                  
            }
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
            camcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
            mMediaRecorder.setProfile(camcorderProfile);            

//            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
            mMediaRecorder.setVideoEncodingBitRate(1024*816);
            mMediaRecorder.setOrientationHint(90);
            try {
                mVedioFile = File.createTempFile("Vedio", ".mp4",
                        mRecVedioPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaRecorder.setOutputFile(mVedioFile.getAbsolutePath());
        
                mMediaRecorder.prepare();
                timer.setVisibility(View.VISIBLE);
                handler.postDelayed(task, 1000);
                mMediaRecorder.start();
            } catch (Exception e) {
                Log.e(TAG,"media open error:"  + e.getMessage());
                releaseRecord();
                return;
            }        
            Toast.makeText(getBaseContext(), "开始录制视频!",Toast.LENGTH_SHORT).show();
            isRecording = true;    
      
            recordHandler.postDelayed(r, 10*1000);
		}else{
			Log.d(TAG,"is recording");
		}
	}
	
	private Handler recordHandler = new Handler();
    Runnable r=new Runnable()
    {
    	public void run()
    	{
    		stopRecord();
    	}
    };
    
	private void releaseRecord(){
		try{
	        if (mMediaRecorder != null) {
	            mMediaRecorder.setOnErrorListener(null);
	            mMediaRecorder.setPreviewDisplay(null);
	            mMediaRecorder.stop();
	            mMediaRecorder.release();
	            mMediaRecorder = null;
	        }
	    	handler.removeCallbacks(task);	
			minute=0;
			second=0;
			recordSeconds=0;
			isRecording = false;	    
		}catch(Exception e){
			e.printStackTrace();          
		}
	  if (camera!=null) {
          camera.stopPreview();
          camera.release();
          camera = null;
      }
		finish();
	}
	private void stopRecord(){
		isRecording = false;
        try {
             mMediaRecorder.stop();
            timer.setText(format(hour) + ":" + format(minute) + ":"
                    + format(second));
            mMediaRecorder.release();
            mMediaRecorder = null;

         	String nowTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        	mOutVideoFile = Constant.VEDIO_DIR + nowTime  + ".mp4";
        	mOutPicFile = Constant.VEDIO_DIR + nowTime + ".jpg";
            File out = new File( mOutVideoFile);
            if (mVedioFile.exists())
            	mVedioFile.renameTo(out);
            
            Bitmap bitmap = createThumb();
            if(bitmap==null){
            	Log.e(TAG,"to bitmap error");
            	return;
            }

            Log.d(TAG,bitmap.getHeight() + ":" + bitmap.getWidth() +":" +  recordSeconds);
            saveBitmap(bitmap,new File(mOutPicFile));
            Intent intent = new Intent();
            intent.putExtra("videoFileName", mOutVideoFile);
            intent.putExtra("picFileName", mOutPicFile);
            intent.putExtra("duration", recordSeconds);
            intent.putExtra("videoType", "mp4");
            intent.putExtra("picType", "jpg");
            intent.putExtra("picHeight", bitmap.getHeight());
            intent.putExtra("picWidth", bitmap.getWidth());
            setResult(ChatNewActivity.RESULT_TAKE_VEDIO_OK, intent);
            Toast.makeText(getBaseContext(), "录制完成!",Toast.LENGTH_SHORT).show();
			
        } catch (Exception e) {
            e.printStackTrace();
            releaseRecord();
        }

        if (camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        finish();
	}

	 private void saveBitmap(Bitmap mBitmap,File file)  {
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        if(fOut==null){
        	Log.e(TAG,"file open fail");
        	return;
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
	}

    private Bitmap createThumb(){
    	File videoFile = new File( mOutVideoFile);
    	if(videoFile.exists()){
    		MediaMetadataRetriever mmr = new MediaMetadataRetriever();  
    		mmr.setDataSource(mOutVideoFile);
    		
    		Bitmap bitmap = mmr.getFrameAtTime(1000);
    		 bitmap = ThumbnailUtils.extractThumbnail(bitmap, 240, 320,
    	                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    		mmr.release();
    		return bitmap;      		
    	}
    	return null;
    }

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            if (isRecording) {
                handler.postDelayed(this, 1000);
                second++;
                recordSeconds++;
                if (second >= 60) {
                    minute++;
                    second = second % 60;
                }
                if (minute >= 60) {
                    hour++;
                    minute = minute % 60;
                }
                timer.setText(format(hour) + ":" + format(minute) + ":"+ format(second));
            }
        }
    };

       public String format(int i) {
        String s = i + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    @Override
    public void onBackPressed() {
    	releaseRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onBackPressed();
    }

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		Log.e(TAG,"mediaRecord error:" + what +":" + extra);
        if (camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
		if(what==268435556){
			Toast.makeText(getBaseContext(), "录音时间太短!", Toast.LENGTH_SHORT).show();
            second = 0;
            minute = 0;
            hour = 0;
            recordSeconds=0;
		}
		
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		Log.e(TAG,"mediaRecord onInfo:" + what +":" + extra);
	}
}