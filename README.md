### 腾讯云通信 Demo

[![Build Status](https://travis-ci.org/Ydcool/TencentIMSample.svg?branch=master)](https://travis-ci.org/Ydcool/TencentIMSample)

源代码来自 [官网](http://www.qcloud.com/product/im.html)，本项目是官网的 Android Studio 移植版本。( 官网 Eclipse 版本槽点太多! 😒 )  

可直接运行使用的 [Demo Apk 下载](https://github.com/Ydcool/TencentIMSample/raw/master/demo/demo-release-unsigned.apk)

-----------------------------------------------------------------------------------------------------------
#### 使用说明

1 . 由于离线推送通知依赖于特定的service，所以集成的时候必须在 `AndroidManifest.xml` 的 `<application> </application>` 中添加如下配置：

```xml
          <!-- 【必须】消息收发service -->
         <service
            android:name="com.tencent.qalsdk.service.QalService"
            android:exported="true"
            android:persistent="true"
            android:process=":QALSERVICE" >
        </service>  
		
        <!-- 【必须】 离线消息广播接收器 -->
         <receiver
            android:name="com.tencent.qalsdk.QALBroadcastReceiver">
            <intent-filter>
                <action android:name="com.tencent.qalsdk.broadcast.qal" />
            </intent-filter>
        </receiver>
        
        <!-- 【必须】 qal service广播接收,自启动 -->
        <receiver 
             android:name="com.tencent.qalsdk.service.QALServiceReceiver" 
             android:enabled="true" 
             android:exported="false" 
             android:process=":QALSERVICE" >
             <intent-filter android:priority="0x7fffffff">
                <action android:name="android.intent.action.BOOT_COMPLETED" />        	
                <action android:name="android.intent.action.USER_PRESENT" />               
                <action android:name="android.bluetooth.adapter.action.STATE_CHANGED" />
                <action android:name="android.intent.action.ACTION_POWER_CONNECTED" />
                <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED" />
            </intent-filter>
        </receiver>
```

2 . 如果需要离线推送通知，需要在 `application` 的 `onCreate` 中调用 `TIMManager` 中的 `setOfflinePushListener` 接口注册离线推送通知回调（具体参数可以参考相应的 javadoc ）。如下：
   注意：`onCreate()` 这里只是初始化离线处理必须的逻辑。app 正常启动初始化逻辑建议放在 `activity` 中。
   
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "app oncreate");
        if(MsfSdkUtils.isMainProcess(this)) {
            Log.d("MyApplication", "main process");
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    Log.e("MyApplication", "recv offline push");
                    notification.doNotify(getApplicationContext(), R.drawable.ic_launcher);
                }
            });
        }
    }
}
```


[参考文档](http://www.qcloud.com/doc/product/269/%E6%A6%82%E8%BF%B0%EF%BC%88Android%20SDK%EF%BC%89)

#### Jar 包说明

| jar 包 | 描述 |
|----|----|
| beacon_android_v1.9.9.jar | 灯塔上报jar包 |
| bugly_1.2.8_imsdk_release.jar | crash上报jar包 |
| imsdk.jar | SDK的jar包 |
| mobilepb.jar | protobuffer处理相关jar包 |
| qalsdk.jar | SDK网络层jar包 |
| tls_sdk.jar | 帐号系统jar包 |
| wup-1.0.0-SNAPSHOT.jar | 无线统一协议jar包 |

#### 更新日志

See [Change Log](CHANGELOG.md)