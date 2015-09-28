package com.supermap.navigation.demo;

public abstract class BaseView {
	
	protected boolean mIsShowing = false;
	
	abstract public void show();
	 
	abstract public void close();
	
	public boolean isShowing() {
		return mIsShowing;
	}
}
