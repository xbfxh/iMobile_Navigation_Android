package com.supermap.navigation.demo;

import java.text.DecimalFormat;
import com.supermap.android.tools.GeoToolkit;
import com.supermap.data.Point2D;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class PoiShowView extends BaseView {

	static private PoiShowView mPoiShowView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;
	private MapControl mMapControl = null;
	
	private RelativeLayout mViewMainBottom = null;
	
	private View mViewPoiShowBottom = null;
	private Button btnPoiNearbyPlace = null;
	private Button btnPoiOpenRoute = null;
	private ImageButton btnPoiShowDetail = null;
	private TextView txtPoiShowDistance = null;
	private TextView txtPoiShowName = null;
	
	private String strPoiShowName = "";
	
	private int mPoiId = -1;
	private double mDistance = 0;
	
	static public PoiShowView getInstance(Context context, MapView mapView, View mainView) {
		if (mPoiShowView == null) {
			mPoiShowView = new PoiShowView(context, mapView, mainView);
		}
		return mPoiShowView;
	}
	
	private PoiShowView(Context context, MapView mapView, View mainView) {
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
		mViewPoiShowBottom= LayoutInflater.from(mContext).inflate(R.layout.poi_show_bottom, null);
		mViewPoiShowBottom.setClickable(true);
		
		btnPoiNearbyPlace = (Button) mViewPoiShowBottom.findViewById(R.id.btn_poi_show_nearby_place);
		btnPoiNearbyPlace.setOnClickListener(buttonOnClickListener);

		btnPoiOpenRoute = (Button) mViewPoiShowBottom.findViewById(R.id.btn_poi_show_open_route);
		btnPoiOpenRoute.setOnClickListener(buttonOnClickListener);

		btnPoiShowDetail = (ImageButton) mViewPoiShowBottom.findViewById(R.id.btn_poi_show_detail);
		btnPoiShowDetail.setOnClickListener(buttonOnClickListener);
		
		txtPoiShowDistance = (TextView) mViewPoiShowBottom.findViewById(R.id.txt_poi_show_distance);
		txtPoiShowName = (TextView) mViewPoiShowBottom.findViewById(R.id.txt_poi_show_name);	
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_poi_show_nearby_place:
				searchNearby();
				break;

			case R.id.btn_poi_show_open_route:			
				openRoute();
				break;

			case R.id.btn_poi_show_detail:
				showPoiDetail();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 到这去
	 */
	private void openRoute() {
		RouteSetView view = RouteSetView.getInstance(mContext, mMapView, mMainView);
		
		Point2D pt = NaviManager.getInstance(mContext).getLocationPoint();
		NaviManager.getInstance(mContext).setStartPoint(pt.getX(), pt.getY());
		
		Point2D ptPoi = NaviManager.getInstance(mContext).getPoiPoint();
		NaviManager.getInstance(mContext).setDestinationPoint(ptPoi.getX(), ptPoi.getY());
		
		view.setStartPtName("我的位置");
		view.setEndPtName(strPoiShowName);
		view.show();
		ViewManager.getInstance().addView(view);
		close();
	}
	
	/**
	 * 周边搜索
	 */
	private void searchNearby() {
		close();
		NearbySearchView view = NearbySearchView.getInstance(mContext, mMapView, mMainView);
		Point2D ptCurrent = NaviManager.getInstance(mContext).getPoiPoint();
		view.setPtCurrent(ptCurrent);
		view.show();
		ViewManager.getInstance().addView(view);
	}
	
	/**
	 * 显示POI详情
	 */
	private void showPoiDetail() {
		PoiDetailsView view = PoiDetailsView.getInstance(mContext, mMapView, mMainView);
		
		String name = DataManager.getInstance(mContext).mCurDatasetName;
		view.setDatasetName(name);
		view.setPoiID(mPoiId);
		view.setDistance(mDistance);
		view.show();
		ViewManager.getInstance().addView(view);
		close();
	}
	
	public void close() {
		mViewMainBottom.removeView(mViewPoiShowBottom);
		mIsShowing = false;
		clear();
	}
	
	public void show() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		float mDensity = dm.density;
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (120*mDensity));

		mViewMainBottom.removeView(mViewPoiShowBottom);
		mViewMainBottom.addView(mViewPoiShowBottom, param);
		
		updateView();
		mIsShowing = true;
	}
	
	private void updateView() {
		Point2D ptLocation = NaviManager.getInstance(mContext).getLocationPoint();
		Point2D ptpoi = NaviManager.getInstance(mContext).getPoiPoint();
		double distance = GeoToolkit.getDistance(ptLocation, ptpoi);
		mDistance = distance;
		
		DecimalFormat df = new DecimalFormat("0.0");		
		String strDistance;
		
		if (distance < 0) {
			strDistance = 0 + "米";
		} else if (distance < 1000) {
			strDistance = (int)distance + "米";
		} else {
			strDistance = df.format(distance/1000.0) + "公里";
		}
				
		txtPoiShowDistance.setText("距离 "+strDistance);
		
		Point2D startPoint = NaviManager.getInstance(mContext).getLocationPoint();
		Point2D destinationPoint = NaviManager.getInstance(mContext).getPoiPoint();
		
		MapManager.getInstance(mContext).showLocationPointByCallOut(startPoint, 
				"location", R.drawable.navi_start, CalloutAlignment.CENTER);
		MapManager.getInstance(mContext).showPoiPointByCallOut(destinationPoint,
				"poiPoint", R.drawable.icon_poi_mark, CalloutAlignment.BOTTOM);
	}
	
	private void clear() {
		mMapView.removeCallOut("poiPoint");
		mMapView.refresh();
		Point2D startPoint = NaviManager.getInstance(mContext).getLocationPoint();
		MapManager.getInstance(mContext).showLocationPointByCallOut(startPoint, 
				"location", R.drawable.navi_start, CalloutAlignment.CENTER);
	}
	
	public void setPoiName(String name) {
		strPoiShowName = name;
		txtPoiShowName.setText(name);
	}
	
	public void setPoiID(int id) {
		mPoiId = id;
	}
}
