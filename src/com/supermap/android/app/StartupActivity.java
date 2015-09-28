package com.supermap.android.app;

import com.supermap.android.configuration.DefaultDataConfiguration;
import com.supermap.data.Environment;
import com.supermap.navigation.demo.DataManager;
import com.supermap.navigation.demo.MainActivity;
import com.supermap.navigation.demo.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

/**
 * 启动界面
 * @author 康伟波
 *
 */
public class StartupActivity extends Activity {
	
	private final String LicPath = DefaultDataConfiguration.LicensePath;
	
	private MyApplication mApp = null;
	
	boolean mIsDataExists = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Environment.setLicensePath(LicPath);
//		Environment.setOpenGLMode(true);
		Environment.initialization(this);
		
		setContentView(R.layout.activity_startup);
		mApp = (MyApplication) getApplication();
		mApp.registerActivity(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		// Initialize NaviData and License when they are contained in the package
		initData();                     

	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
	}
	
	/**
	 * 初始化数据
	 */
	public void initData()
    {
		final ProgressDialog dialog = new ProgressDialog(this);
		
		final DefaultDataConfiguration configuration = new DefaultDataConfiguration();
		if (!configuration.checkData()) {		
	    	dialog.setCancelable(false);
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.setMessage("正在初始化数据。。。");
	    	dialog.show();
	    	mIsDataExists = false;
		} else {
			mIsDataExists = true;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				
				if (!mIsDataExists) {				
					configuration.configMapData();
				}			
				
				configuration.checkLicense();
				
				DataManager.getInstance(StartupActivity.this).openWorkspace();
				dialog.dismiss();

				// Start MainActivity after initialize data
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Intent intent = new Intent(StartupActivity.this,
								MainActivity.class);
						StartupActivity.this.startActivity(intent);

					}
				});
			};

		}).start();
	    	
	}
}
