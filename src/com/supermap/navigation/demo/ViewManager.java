package com.supermap.navigation.demo;

import java.util.Stack;

public class ViewManager {
	private Stack<BaseView> mViewStack = null;
	
	private ViewChangeListener mViewChangeListener = null;
	
	static private ViewManager mPoiShowView = null;
	
	static public ViewManager getInstance() {
		if (mPoiShowView == null) {
			mPoiShowView = new ViewManager();
		}
		return mPoiShowView;
	}
	
	private ViewManager() {
		mViewStack = new Stack<BaseView>();
	}
	
	/**
	 * 视图回退
	 * @return
	 */
	public boolean fallback() {
		
		if (mViewStack.empty()) {
			return false;
		} else {
			BaseView currentView = mViewStack.pop();
			if (currentView != null) {
				currentView.close();
			}
			
			if (!mViewStack.empty()) {
				BaseView pretView = mViewStack.peek();
				if (pretView != null) {
					pretView.show();
				}
			} else {
				mViewChangeListener.onAllViewClose();
			}

			return true;
		}
	}
	
	/**
	 * 添加视图
	 * @param view
	 * @return
	 */
	public boolean addView(BaseView view) {
		if (view == null) {
			return false;
		}
		
		mViewChangeListener.onViewAdd();
		
		mViewStack.push(view);
		return true;
	}
	
	public void setViewChangeListener(ViewChangeListener listener) {
		this.mViewChangeListener = listener;
	}

}
