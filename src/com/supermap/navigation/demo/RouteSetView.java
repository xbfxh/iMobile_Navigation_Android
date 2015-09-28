package com.supermap.navigation.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import com.supermap.android.app.MyApplication;
import com.supermap.data.GeoLine;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.Navigation;

public class RouteSetView extends BaseView {
	
	static private RouteSetView mRouteSetView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;
	private MapControl mMapControl = null;
	private Navigation mNavigation = null;
	
	private RelativeLayout mViewMainBottom = null;
	
	private View mViewRouteSet = null;
	private ImageButton btnRouteSetViewBack = null;
	private Button btnRouteSetViewOK = null;
	
	private TextView txtRouteStartPt = null;
	private TextView txtRouteEndPt = null;
	
	private RadioGroup rgNaviMode = null;
	
	private String mStrStartPtName = "我的位置";
	private String mStrEndPtName = "";
	
	static public RouteSetView getInstance(Context context, MapView mapView, View mainView) {
		if (mRouteSetView == null) {
			mRouteSetView = new RouteSetView(context, mapView, mainView);
		}
		return mRouteSetView;
	}
	
	private RouteSetView(Context context, MapView mapView, View mainView) {
		mContext = context;
		mMainView = mainView;
		mMapView = mapView;
		mMapControl = mMapView.getMapControl();
		mNavigation = mMapControl.getNavigation();
		
		initView();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	private void initView() {	
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewRouteSet= LayoutInflater.from(mContext).inflate(R.layout.route_set_view, null);
		mViewRouteSet.setClickable(true);
		
		btnRouteSetViewBack = (ImageButton) mViewRouteSet.findViewById(R.id.btn_route_set_view_back);
		btnRouteSetViewBack.setOnClickListener(buttonOnClickListener);

		btnRouteSetViewOK = (Button) mViewRouteSet.findViewById(R.id.btn_route_set_view_ok);
		btnRouteSetViewOK.setOnClickListener(buttonOnClickListener);
		
		txtRouteStartPt = (TextView) mViewRouteSet.findViewById(R.id.txt_route_set_view_startpt);
		txtRouteStartPt.setOnClickListener(buttonOnClickListener);
		txtRouteEndPt = (TextView) mViewRouteSet.findViewById(R.id.txt_route_set_view_endpt);
		txtRouteEndPt.setOnClickListener(buttonOnClickListener);
		
		rgNaviMode = (RadioGroup) mViewRouteSet.findViewById(R.id.rg_navi_mode);
		rgNaviMode.setOnCheckedChangeListener(rgOnClickListener);
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_route_set_view_back:
				ViewManager.getInstance().fallback();
				break;

			case R.id.btn_route_set_view_ok:			
				openRoute();
				break;
				
			case R.id.txt_route_set_view_startpt:
				openSearchView(false);
				break;

			case R.id.txt_route_set_view_endpt:			
				openSearchView(true);
				break;
			default:
				break;
			}
		}
	};
	
	OnCheckedChangeListener rgOnClickListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch (checkedId) {	
			case R.id.rbtn_tuijian:
				NaviManager.getInstance(mContext).setRouteAnalystMode(0);
				break;
			case R.id.rbtn_shijian:			
				NaviManager.getInstance(mContext).setRouteAnalystMode(1);
				break;
			case R.id.rbtn_juli:
				NaviManager.getInstance(mContext).setRouteAnalystMode(2);
				break;
			case R.id.rbtn_feiyong:
				NaviManager.getInstance(mContext).setRouteAnalystMode(3);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 网络分析
	 */
	private void openRoute() {
		int result = NaviManager.getInstance(mContext).routeAnalyst();
		if (result == 0) {
			Toast.makeText(mContext, "分析失败", Toast.LENGTH_SHORT).show();
		} else {
			
			GeoLine line = mNavigation.getRoute();
			if (line != null) {
				Rectangle2D bounds = line.getBounds();
				Rectangle2D boundNew = new Rectangle2D();
				boundNew.setLeft(bounds.getCenter().getX() - bounds.getWidth()/1);
				boundNew.setRight(bounds.getCenter().getX() + bounds.getWidth()/1);
				boundNew.setTop(bounds.getCenter().getY() + bounds.getHeight()/1);
				boundNew.setBottom(bounds.getCenter().getY() - bounds.getHeight()/1);
				
				mMapControl.getMap().setViewBounds(boundNew);
				mMapControl.getMap().refresh();
				line.dispose();
			}
			
			close();		
			RouteShowView view = RouteShowView.getInstance(mContext, mMapView, mMainView);
			view.show();
			ViewManager.getInstance().addView(view);
					
			Point2D startPoint = NaviManager.getInstance(mContext).getStartPoint();
			Point2D destinationPoint = NaviManager.getInstance(mContext).getDestinationPoint();
			
			MapManager.getInstance(mContext).showPoiPointByCallOut(startPoint, 
					"location", R.drawable.icon_track_navi_start, CalloutAlignment.BOTTOM);
			MapManager.getInstance(mContext).showPoiPointByCallOut(destinationPoint, 
					"poiPoint", R.drawable.icon_track_navi_end, CalloutAlignment.BOTTOM);
		}	
	}
	
	public void close() {
		mViewMainBottom.removeView(mViewRouteSet);
		MyApplication.getInstance().setLongPressEnable(true);
	}
	
	public void show() {
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mViewMainBottom.removeView(mViewRouteSet);
		mViewMainBottom.addView(mViewRouteSet, param);
		
		MyApplication.getInstance().setLongPressEnable(false);
		
		txtRouteStartPt.setText("起点：" + mStrStartPtName);
		txtRouteEndPt.setText("终点：" + mStrEndPtName);
	}
	
	public void setStartPtName(String name) {
		this.mStrStartPtName = name;
	}
	
	public void setEndPtName(String name) {
		this.mStrEndPtName = name;
	}
	
	private void openSearchView(boolean isEnd) {
		StartPointSearchView view = StartPointSearchView.getInstance(mContext, mMapView, mMainView);		
		view.setMode(isEnd);
		view.show();
		ViewManager.getInstance().addView(view);
		close();
	}
}
