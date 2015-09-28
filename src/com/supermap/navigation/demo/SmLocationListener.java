package com.supermap.navigation.demo;

import com.supermap.plugin.LocationManagePlugin.GPSData;

public interface SmLocationListener {
	
	public void onLocationChanged(GPSData data);
	
	public void onLocationFailure();
	
	public void onLocationSuccess();
}
