package com.supermap.navigation.demo;

import java.text.DecimalFormat;
import com.supermap.android.tools.GeoToolkit;
import com.supermap.data.Point2D;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navigation.demo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class SelectPointView extends BaseView {

	static private SelectPointView mSelectPointView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;;
	private MapControl mMapControl = null;
	
	private RelativeLayout mViewMainBottom = null;
	private RelativeLayout mViewMainTop = null;
	private RelativeLayout mViewMainControl = null;
	
	private RelativeLayout mViewSelectTop = null;
	private RelativeLayout mViewSelectBottom = null;
	
	private View mViewSelectPoint = null;
	private ImageButton btnSelectPointBack = null;
	private TextView txtSelectPointDistance = null;
	private TextView txtSelectPointName = null;
	private TextView txtSelectPointOK = null;
	
	private String strPoiShowName = "";
	private Point2D mPoint = null;
	private boolean mIsEnd = true; //是否是设置终点
	
	static public SelectPointView getInstance(Context context, MapView mapView, View mainView) {
		if (mSelectPointView == null) {
			mSelectPointView = new SelectPointView(context, mapView, mainView);
		}
		return mSelectPointView;
	}
	
	private SelectPointView(Context context, MapView mapView, View mainView) {
		mContext = context;
		mMainView = mainView;
		mMapView = mapView;
		mMapControl = mMapView.getMapControl();
		initView();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	private void initView() {	
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewMainControl = (RelativeLayout)mMainView.findViewById(R.id.ly_main_control);
		mViewMainTop= (RelativeLayout)mMainView.findViewById(R.id.ly_main_top);
		
		mViewSelectPoint= LayoutInflater.from(mContext).inflate(R.layout.select_pt_view, null);
		mViewSelectTop = (RelativeLayout)mViewSelectPoint.findViewById(R.id.ly_select_pt_view_top);
		mViewSelectBottom = (RelativeLayout)mViewSelectPoint.findViewById(R.id.ly_select_pt_view_bottom);
		mViewSelectTop.setClickable(true);
		mViewSelectBottom.setClickable(true);
		
		btnSelectPointBack = (ImageButton) mViewSelectPoint.findViewById(R.id.btn_select_pt_view_back);
		btnSelectPointBack.setOnClickListener(buttonOnClickListener);
		
		txtSelectPointDistance = (TextView) mViewSelectPoint.findViewById(R.id.txt_select_pt_view_distance);
		txtSelectPointName = (TextView) mViewSelectPoint.findViewById(R.id.txt_select_pt_view_name);
		
		txtSelectPointOK = (TextView) mViewSelectPoint.findViewById(R.id.txt_select_pt_view_ok);
		txtSelectPointOK.setOnClickListener(buttonOnClickListener);
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_select_pt_view_back:
				ViewManager.getInstance().fallback();
				break;

			case R.id.txt_select_pt_view_ok:			
				setPoint();
				break;

			default:
				break;
			}
		}
	};
	
	private void setPoint() {
		RouteSetView routeSetView = RouteSetView.getInstance(mContext, mMapView, mMainView);
		
		if (mIsEnd) {
			routeSetView.setEndPtName(strPoiShowName);
			NaviManager.getInstance(mContext).setDestinationPoint(mPoint.getX(), mPoint.getY());
		} else {
			routeSetView.setStartPtName(strPoiShowName);
			NaviManager.getInstance(mContext).setStartPoint(mPoint.getX(), mPoint.getY());
		}
		
		ViewManager.getInstance().fallback();
		ViewManager.getInstance().fallback();
	}
	
	
	public void close() {
		mViewMainBottom.removeView(mViewSelectPoint);
		mIsShowing = false;
		
		mViewMainControl.setVisibility(View.VISIBLE);
		mViewMainTop.setVisibility(View.VISIBLE);
		
		clear();
	}
	
	public void show() {
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mViewMainBottom.removeView(mViewSelectPoint);
		mViewMainBottom.addView(mViewSelectPoint, param);
		
		mViewMainControl.setVisibility(View.INVISIBLE);
		mViewMainTop.setVisibility(View.INVISIBLE);
		
		mIsShowing = true;
	}
	
	private void updateView() {
		Point2D location = NaviManager.getInstance(mContext).getLocationPoint();
		double distance = GeoToolkit.getDistance(mPoint,location);
		 
		DecimalFormat df = new DecimalFormat("0.0");		
		String strDistance;
		
		if (distance < 0) {
			strDistance = 0 + "米";
		} else if (distance < 1000) {
			strDistance = (int)distance + "米";
		} else {
			strDistance = df.format(distance/1000.0) + "公里";
		}
				
		txtSelectPointDistance.setText("距离 "+strDistance);
	}
	
	private void clear() {
		mMapView.removeCallOut("selectPoint");
		mMapView.refresh();
	}
	
	public void setPoiName(String name) {
		strPoiShowName = name;
		txtSelectPointName.setText(name);
	}
	
	public void setPoiPoint(Point2D point) {
		GeoToolkit.MapToLongitude_Latitude(mMapControl.getMap(), point);
		mPoint = point;
		
		MapManager.getInstance(mContext).showPoiPointByCallOut(mPoint,
				"selectPoint", R.drawable.icon_select_point, CalloutAlignment.BOTTOM);
		
		updateView();
	}
	
	public void setMode(boolean isEnd) {
		mIsEnd = isEnd;
	}
}
