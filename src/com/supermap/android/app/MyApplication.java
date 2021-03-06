package com.supermap.android.app;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Application;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.supermap.android.filemanager.MyAssetManager;
import com.supermap.android.filemanager.MySharedPreferences;


public class MyApplication extends Application {
	public static String DATAPATH = "";
	public static String SDCARD   = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	
	private static MyApplication sInstance = null;

	private boolean isLongPressEnable  = true;
	private boolean isSingleTapEnable  = true;
	
	private ArrayList<Activity> mActivities = new ArrayList<Activity>();
	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		DATAPATH = this.getFilesDir().getAbsolutePath() + "/";
		sInstance = this;
		
		// Initialization
		MySharedPreferences.init(this);
		MyAssetManager.init(this);
	
	}
	
	/**
	 * 获取当前应用对象
	 * @return
	 */
	public static MyApplication getInstance()
	{
		return sInstance;
	}
	
	/**
	 * Toast显示信息
	 * @param info
	 */
	public   void showInfo(String info)
	{
		Toast toast = Toast.makeText(sInstance, info, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
		
	}
	
	/**
	 * Toast显示错误信息
	 * @param err
	 */
	public void showError (String err) 
	{
		Toast toast = Toast.makeText(sInstance, "Error: " + err, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
		Log.e(this.getClass().getName(), err);
	}
	
	/**
	 * 获取显示尺寸值
	 * @param dp
	 * @return
	 */
	public static int dp2px (int dp) 
	{
		int density = (int) (dp*sInstance.getResources().getDisplayMetrics().density);
		
		return density;
	}
	
	/**
	 * 注册Activity
	 * @param act
	 */
	public void registerActivity(Activity act){
		mActivities.add(act);
	}
	
	/**
	 * 退出应用
	 */
	public void exit(){
	
		for(Activity act:mActivities){
			act.finish();
		}
		Process.killProcess(Process.myPid());
	}
	
	public boolean isLongPressEnable() {
		return isLongPressEnable;
	}
	
	public boolean isSingleTapEnable() {
		return isSingleTapEnable;
	}
	
	public void setLongPressEnable(boolean isLongPressEnable) {
		this.isLongPressEnable = isLongPressEnable;
	}
	
	public void setSingleTapEnable(boolean isSingleTapEnable) {
		this.isSingleTapEnable = isSingleTapEnable;
	}
}

