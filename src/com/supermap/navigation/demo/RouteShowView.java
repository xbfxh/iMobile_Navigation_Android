package com.supermap.navigation.demo;

import java.text.DecimalFormat;
import com.supermap.android.app.MyApplication;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.NaviPath;
import com.supermap.navi.Navigation;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class RouteShowView extends BaseView {

	static private RouteShowView mRouteShowView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;
	private Navigation mNavigation = null;
	private MapControl mMapControl = null;
	
	private RelativeLayout mViewMainBottom = null;
	private RelativeLayout mViewMainTop = null;
	private RelativeLayout mViewMainControl = null;
	private ProgressBar mProgressNaviRate = null;
	
	private View mViewRouteShowTop = null;
	private View mViewRouteShowBottom = null;
	private Button btnRealNavi = null;
	private Button btnSimNavi = null;
	private ImageButton btnRouteDetail = null;
	private ImageButton btnRouteShowBack = null;
	private TextView txtRouteShowDistance = null;
	
	private int	naviMode = 1;                    // 0:真实导航; 1:模拟导航; 2:定点巡航
	
	private double mDistance = 0;
	
	static public RouteShowView getInstance(Context context, MapView mapView, View mainView) {
		if (mRouteShowView == null) {
			mRouteShowView = new RouteShowView(context, mapView, mainView);
		}
		return mRouteShowView;
	}
	
	private RouteShowView(Context context, MapView mapView, View mainView) {
		mContext = context;
		
		mMainView = mainView;
		mMapView = mapView;
		mMapControl = mMapView.getMapControl();
		mNavigation = mMapControl.getNavigation();

		mNavigation.addNaviInfoListener(new NaviListener() {
			
			@Override
			public void onStopNavi() {
				// TODO Auto-generated method stub
				ViewManager.getInstance().fallback();
				mViewMainControl.setVisibility(View.VISIBLE);
				mProgressNaviRate.setVisibility(View.INVISIBLE);
//				mViewMainTop.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onStartNavi() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNaviInfoUpdate(NaviInfo arg0) {
				// TODO Auto-generated method stub
				double diatance = arg0.RouteRemainDis;
				int rate = (int) (100 - diatance/mDistance*100);
				mProgressNaviRate.setProgress(rate);
			}
			
			@Override
			public void onAarrivedDestination() {
				// TODO Auto-generated method stub
				ViewManager.getInstance().fallback();
				mViewMainControl.setVisibility(View.VISIBLE);
				mProgressNaviRate.setVisibility(View.INVISIBLE);
//				mViewMainTop.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAdjustFailure() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPlayNaviMessage(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		initView();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	private void initView() {
		
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewMainControl = (RelativeLayout)mMainView.findViewById(R.id.ly_main_control);
		mViewMainTop= (RelativeLayout)mMainView.findViewById(R.id.ly_main_top);
		mProgressNaviRate = (ProgressBar)mMainView.findViewById(R.id.progress_navi_rate);
				
		mViewRouteShowTop = LayoutInflater.from(mContext).inflate(R.layout.route_show_top, null);
		mViewRouteShowBottom= LayoutInflater.from(mContext).inflate(R.layout.route_show_bottom, null);
		mViewRouteShowTop.setClickable(true);
		mViewRouteShowBottom.setClickable(true);
		
		btnRealNavi = (Button) mViewRouteShowBottom.findViewById(R.id.btn_route_show_real_navi);
		btnRealNavi.setOnClickListener(buttonOnClickListener);

		btnSimNavi = (Button) mViewRouteShowBottom.findViewById(R.id.btn_route_show_sim_navi);
		btnSimNavi.setOnClickListener(buttonOnClickListener);

		btnRouteDetail = (ImageButton) mViewRouteShowBottom.findViewById(R.id.btn_route_show_detail);
		btnRouteDetail.setOnClickListener(buttonOnClickListener);
		
		btnRouteShowBack = (ImageButton) mViewRouteShowTop.findViewById(R.id.btn_route_show_back);
		btnRouteShowBack.setOnClickListener(buttonOnClickListener);
		
		txtRouteShowDistance = (TextView)mViewRouteShowBottom.findViewById(R.id.txt_route_show_distance);
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_route_show_real_navi:
				naviMode = 0;
				startGuide();
				break;

			case R.id.btn_route_show_sim_navi:
				naviMode = 1;
				startGuide();
				break;

			case R.id.btn_route_show_detail:
				showRouteDetail();
				break;
			case R.id.btn_route_show_back:
				close();
				break;
			default:
				break;
			}
		}
	};
	
	private void startGuide() {
		mNavigation.startGuide(naviMode);
		
		BaseView view = new BaseView() {
			
			@Override
			public void show() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void close() {
				// TODO Auto-generated method stub
				
			}
		};
		
		mMapControl.zoomTo(1/9999.0, 100);
		mMapControl.getMap().refresh();
		
		ViewManager.getInstance().addView(view);
		closeMe();
		mViewMainControl.setVisibility(View.INVISIBLE);
		mProgressNaviRate.setVisibility(View.VISIBLE);
		mViewMainTop.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 显示路径详情
	 */
	private void showRouteDetail() {
		closeMe();
		RouteiDetailsView view = RouteiDetailsView.getInstance(mContext, mMapView, mMainView);
		view.show();
		ViewManager.getInstance().addView(view);
	}
	
	private void closeMe() {
		mViewMainBottom.removeView(mViewRouteShowBottom);
		mIsShowing = false;
	}
	
	public void close() {
		closeMe();
		clear();
		MyApplication.getInstance().setLongPressEnable(true);
		MyApplication.getInstance().setSingleTapEnable(true);
	}
	
	public void show() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		float mDensity = dm.density;
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (120*mDensity));
		mViewMainBottom.removeView(mViewRouteShowBottom);
		mViewMainBottom.addView(mViewRouteShowBottom, param);
		mIsShowing = true;
		
		MyApplication.getInstance().setLongPressEnable(false);
		MyApplication.getInstance().setSingleTapEnable(false);
		
		showDistance();
		
//		mMapView.removeCallOut("poiPoint");
//		mMapView.refresh();
	}
	
	private void clear() {
		mNavigation.cleanPath();
		mMapControl.getMap().refresh();
	}
	
	private void showDistance() {
		
		NaviPath naviPath = mNavigation.getNaviPath();	
		if (naviPath == null) {
			return;
		}
		
		double distance = naviPath.getLength();
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
				
		txtRouteShowDistance.setText(strDistance);
	}
}
