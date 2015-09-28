package com.supermap.navigation.demo;

import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class LocationTencent {
	
	TencentLocationManager mTencentLocationManager;
	TencentLocationRequest mTencentLocationRequest;
	
	private int m_reqGeoType = TencentLocationManager.COORDINATE_TYPE_WGS84;
	private int m_reqLevel   = TencentLocationRequest.REQUEST_LEVEL_GEO;

	private TencentLocation m_TencentLocation = null;
	
	private SmLocationListener mSmLocationListener = null;
	private boolean mIsStart = false;
	
	
	/**
	 * 构造函数
	 * @param context
	 */
	public LocationTencent(Context context) {
		
		mTencentLocationRequest = TencentLocationRequest.create();
		mTencentLocationRequest.setAllowCache(false);
		mTencentLocationRequest.setInterval(1000);
		mTencentLocationRequest.setRequestLevel(m_reqLevel);
		
		mTencentLocationManager = TencentLocationManager.getInstance(context.getApplicationContext());
		mTencentLocationManager.setCoordinateType(m_reqGeoType);
		mTencentLocationManager.setKey("QGSBZ-LTN2V-KRNPN-UPXTM-VKU4E-46BQ2");
	}

	public void startLocation() {
		if (mIsStart) {
			return;
		}
		
		int req = mTencentLocationManager.requestLocationUpdates(mTencentLocationRequest, mTencentLocationListener);
		System.out.println("Tencent requestLocationUpdate return value is " + req);
		mIsStart = true;
	}
	
	public void stopLocation() {
		if (!mIsStart) {
			return;
		}
		
		mTencentLocationManager.removeUpdates(mTencentLocationListener);
//		m_TencentLocation = null;
		mIsStart = false;
	}
	
	/**
	 * 获取位置信息
	 * @return
	 */
	public TencentLocation getLocationInfo() {
		return m_TencentLocation;
	}
	
	/**
	 * 位置信息监听类
	 *
	 */
	TencentLocationListener mTencentLocationListener = new TencentLocationListener() {
		
		@Override
		public void onStatusUpdate(String arg0, int arg1, String arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(TencentLocation arg0, int arg1, String arg2) {
			// TODO Auto-generated method stub
			
			if (arg1 == TencentLocation.ERROR_OK) {	
				m_TencentLocation = arg0;
				
				Message msg = m_handler.obtainMessage();
				msg.what = LOCSUCCESS;
				msg.sendToTarget();			
			} else {
				Message msg = m_handler.obtainMessage();
				msg.what = LOCFAILURE;
				msg.sendToTarget();
					
			}		
		}
	};	
	
	private static final int LOCSUCCESS = 0xff01;
	private static final int LOCFAILURE = 0xff02;
	
	private Handler m_handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOCSUCCESS:
				updataLocation();	
				break;
			case LOCFAILURE:
				locationFailure();	
				break;
			default:
				break;
			}
		};
	};
	
	public void setLocationListener(SmLocationListener listener) {
		mSmLocationListener = listener;	
	}
	
	private void updataLocation() {
		if (mSmLocationListener == null) {
			return;
		}
		
		mSmLocationListener.onLocationSuccess();
		
		GPSData data = new GPSData();
		data.dAccuracy = m_TencentLocation.getAccuracy();
		data.dLongitude = m_TencentLocation.getLongitude();
		data.dLatitude = m_TencentLocation.getLatitude();
		mSmLocationListener.onLocationChanged(data);
	}
	
	private void locationFailure() {
		mSmLocationListener.onLocationFailure();
	}
}
