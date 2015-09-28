package com.supermap.navigation.demo;

import com.supermap.android.app.MyApplication;
import com.supermap.android.tools.GeoToolkit;
import com.supermap.data.GeoPoint;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.Navigation;
import com.supermap.navi.SuperMapPatent;
import com.supermap.navigation.demo.R;
import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.tencent.map.geolocation.TencentLocation;
import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	// 定义按钮控件
    private Button      btn_locating		= null; 
    private Button      btn_location		= null; 
    private Button      btn_zoomIn			= null;
    private Button      btn_zoomOut			= null;
    private Button      btn_MainSearch		= null;
    private Button      btn_Nearby			= null;
    
    private ImageButton      btn_to_left	= null;
    private ImageButton      btn_to_right	= null;
    
    private View mMainView = null;
    
	// 定义地图控件
	private MapView        mMapView        = null;
//	private Workspace      mWorkspace      = null;
	private MapControl     mMapControl     = null;
	private Map            mMap            = null;
	private Navigation     mNavigation     = null;
	
	// 定义几何对象   	
	private Point2D        mPoiPoint       = null;	// 定义POI点
//	private Point2D        mStartPoint     = null;	// 定义起点
	
	// 定义布尔变量
//	private boolean isEndPoint         = true;
    private boolean mExitEnable        = false;

	private LocationTencent m_MyLocation = null;
	private MyApplication mApp   = null;
	
	private PoiShowView mPoiShowView = null;
	private boolean mIsLocate = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApp   = (MyApplication) getApplication();
		mApp.registerActivity(this);
		m_MyLocation = new LocationTencent(this);
		
		initView();                                           // 初始化界面控件，布局文件中包含地图显示控件，需在许可初始化后再显示布局
        
		boolean  isOpenMap = false;
		isOpenMap = initMap();                                // 打开工作空间，打开地图 
		if(isOpenMap){
			mNavigation = mMapControl.getNavigation();
			mNavigation.setEncryption(new SuperMapPatent());
			NaviManager.getInstance(this).initNaviData();	// 初始化导航数据
		}else{
			showInfo("Initialize Map failed.");
		}
		
		mPoiShowView = PoiShowView.getInstance(this, mMapView, mMainView);
	} 

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		naviLocate();
		super.onStart();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	public void initView() {
		// 显示布局
		mMainView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);;
		setContentView(mMainView);
		// 获取按钮控件，并设置监听

		btn_locating = (Button) findViewById(R.id.btn_navi_locating);
		
		btn_location = (Button) findViewById(R.id.btn_navi_locate);
		btn_location.setOnClickListener(imageButtonOnClickListener);

		btn_zoomOut = (Button) findViewById(R.id.btn_navidemo_zoomout);
		btn_zoomOut.setOnClickListener(imageButtonOnClickListener);

		btn_zoomIn = (Button) findViewById(R.id.btn_navidemo_zoomin);
		btn_zoomIn.setOnClickListener(imageButtonOnClickListener);

		btn_MainSearch = (Button) findViewById(R.id.btn_main_search);
		btn_MainSearch.setOnClickListener(imageButtonOnClickListener);
		
		btn_Nearby = (Button) findViewById(R.id.btn_navi_nearby);
		btn_Nearby.setOnClickListener(imageButtonOnClickListener);
		
		btn_to_left = (ImageButton) findViewById(R.id.btn_navidemo_to_left);
		btn_to_left.setOnClickListener(imageButtonOnClickListener);
		
		btn_to_left = (ImageButton) findViewById(R.id.btn_navidemo_to_right);
		btn_to_left.setOnClickListener(imageButtonOnClickListener);
		
		ViewManager.getInstance().setViewChangeListener(new ViewChangeListener() {
			
			@Override
			public void onViewAdd() {
				// TODO Auto-generated method stub
				mMainView.findViewById(R.id.ly_main_top).setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onAllViewClose() {
				// TODO Auto-generated method stub
				mMainView.findViewById(R.id.ly_main_top).setVisibility(View.VISIBLE);
			}
		});
	}

	OnClickListener imageButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_navi_locate:
				naviLocate();
				break;

			case R.id.btn_navidemo_zoomin:
				mMapControl.getMap().zoom(2);
				mMapControl.getMap().refresh();
				break;

			case R.id.btn_navidemo_zoomout:
				mMapControl.getMap().zoom(0.5);
				mMapControl.getMap().refresh();
				break;
				
			case R.id.btn_main_search:
				searchByname();
				break;
				
			case R.id.btn_navi_nearby:
				nearby();
				break;
				
			case R.id.btn_navidemo_to_left:
				panTo(true);
				break;
				
			case R.id.btn_navidemo_to_right:
				panTo(false);
				break;
				
			default:
				break;
			}
		}
	};
	
	private void naviLocate() {
		if(mNavigation.isGuiding()){
			showInfo("请先按返回键，退出导航");
			return;
		}
		
		TencentLocation location = m_MyLocation.getLocationInfo();
		if(location == null ){
					
			m_MyLocation.setLocationListener(new SmLocationListener() {
				
				@Override
				public void onLocationFailure() {
					// TODO Auto-generated method stub
					//showInfo("现在无法定位，请稍后再试");
					updataLocation(null);
					stopLocateAnimation();
					m_MyLocation.stopLocation();
				}
				
				@Override
				public void onLocationChanged(GPSData data) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLocationSuccess() {
					// TODO Auto-generated method stub
					stopLocateAnimation();
					TencentLocation location = m_MyLocation.getLocationInfo();
					updataLocation(location);
					m_MyLocation.stopLocation();
				}
			});
			
			m_MyLocation.startLocation();
			
			// 定位按钮旋转动画
			startLocateAnimation();
	
		} else {
			updataLocation(location);
		}
	
		mMap.refresh();
	}
	
	private void updataLocation(TencentLocation location) {	
		Point2D point2D = null;
		
		if (location == null) {
//			showInfo("现在无法定位，请稍后再试");	
//			return;
	//		point2D = new Point2D(116.4019, 39.9892);
			point2D = new Point2D(116.5052061584224, 39.985749510080936);
		} else {
			point2D = mNavigation.encryptGPS(location.getLongitude(), location.getLatitude());	
			Rectangle2D rect = mMap.getBounds();
			if (!rect.contains(point2D)) {
//				point2D.setX(116.4019);
//				point2D.setY(39.9892);
				point2D.setX(116.5052061584224);
				point2D.setY(39.985749510080936);
			}
		}
		
		mIsLocate = true;
		
		NaviManager.getInstance(this).setLocationPoint(point2D.getX(), point2D.getY());
		
		GeoToolkit.Longitude_LatitudeToMap(mMap, point2D);
		MapManager.getInstance(this).showLocationPointByCallOut(point2D, "location", R.drawable.navi_start, CalloutAlignment.CENTER);
		
		mMapControl.panTo(point2D, 300);
		mMapControl.zoomTo(1/24990.0, 300);
		// 在地图上显示该点
//		mMap.setCenter(point2D);
//		mMap.setScale(1 / 57373.046875);
		mMap.refresh();
	}
	
	/**
	 * 打开工作空间，显示地图
	 * @return
	 */
	public boolean initMap() {
        // 获取地图控件
		mMapView    = (MapView) findViewById(R.id.mapView);
		MapManager mapManager = MapManager.getInstance(this);
		mapManager.init(mMapView);
		
		mMapControl = mapManager.getMapControl();
		mMap = mMapControl.getMap();
    	
		// 设置长按事件监听
		mMapControl.setGestureDetector(new GestureDetector(MainActivity.this, longTouchListener));
		return true;
	}
	
	// 长按事件监听
	SimpleOnGestureListener longTouchListener = new SimpleOnGestureListener() {

		public void onLongPress(MotionEvent event) {
			if (mApp.isLongPressEnable()) {              // 允许获取长按事件结果时才响应			
//				if (isEndPoint) {
					
					int x = (int) event.getX();
					int y = (int) event.getY();

					// 转换为地图二维点
					Point2D pt = mMap.pixelToMap(new Point(x, y));

					SelectPointView view = SelectPointView.getInstance(MainActivity.this, mMapView, mMainView);
					if (view.isShowing()) {
						view.setPoiName("未知点");
						view.setPoiPoint(pt);
					} else {
						setPoiPoint(pt, "未知点", -1);
					}
//				}
			}
		}
		
        public boolean onSingleTapUp(MotionEvent e) {
        	if (mApp.isSingleTapEnable()) { 
	        	int x = (int) e.getX();
	        	int y = (int) e.getY();
	        	Point pt = new Point(x, y);
	        	 	
	        	queryPoiPoint(pt);
        	}
            return true;
        }
	};

	/**
	 * 获取并显示POI点
	 */
	private void queryPoiPoint(Point pt) {
		Recordset poiRecordset  = DataManager.getInstance(MainActivity.this).queryNearset(pt);    
		
		if (poiRecordset == null) {
			return;
		}
		
		if (poiRecordset.getRecordCount() < 1) {
			return;
		}
		
		int id = poiRecordset.getID();
		String name = poiRecordset.getString("Name");
		GeoPoint poi = (GeoPoint)poiRecordset.getGeometry();
		Point2D pt2d = new Point2D(poi.getX(), poi.getY());
		
		SelectPointView view = SelectPointView.getInstance(this, mMapView, mMainView);
		if (view.isShowing()) {
			view.setPoiName(name);
			view.setPoiPoint(pt2d);
		} else {
			setPoiPoint(pt2d,name,id);
		}	
	}
	
	/**
	 * 获取并显示POI点
	 */
	public void setPoiPoint(Point2D pt, String name, int id) {
		mPoiPoint = new Point2D(pt);	
		MapManager.getInstance(this).showPoiPointByCallOut(mPoiPoint, 
				"poiPoint", R.drawable.icon_poi_mark, CalloutAlignment.BOTTOM);
		
		GeoToolkit.MapToLongitude_Latitude(mMap, pt);
		NaviManager.getInstance(this).setPoiPoint(mPoiPoint.getX(), mPoiPoint.getY());
		
		mMapControl.panTo(mPoiPoint, 300);
//		mMapControl.zoomTo(1/24990.0, 300);
		mMap.refresh();
		
		// 显示POI详情展示
		if (!mPoiShowView.isShowing()) {
			ViewManager.getInstance().addView(mPoiShowView);
		}
		
		mPoiShowView.setPoiID(id);
		mPoiShowView.setPoiName(name);
		mPoiShowView.show();	
	}
	  
	/**
	 * Toast message
	 * @param mesg
	 */
	public void showInfo(String mesg) {
		Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			
			if (mNavigation != null && mNavigation.isGuiding()){			
				NaviManager.getInstance(this).stopNaviDialog();
				return true;
			} 

			if (ViewManager.getInstance().fallback()) {
				return true;
			}
			
			if(!mExitEnable){
				Toast.makeText(this, "再按一次退出程序！", 1500).show();
				mExitEnable = true;
			} else {
				mApp.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 搜索
	 */
	private void searchByname() {
		PoiSearchView view = PoiSearchView.getInstance(this, mMapView, mMainView);
		view.show();
		ViewManager.getInstance().addView(view);
	}
	
	private void nearby() {
		if (!mIsLocate) {
			showInfo("无法确定当前位置");
			return;
		}
		
		NearbySearchView view = NearbySearchView.getInstance(this, mMapView, mMainView);
		Point2D ptCurrent = NaviManager.getInstance(this).getLocationPoint();
		view.setPtCurrent(ptCurrent);
		view.show();
		ViewManager.getInstance().addView(view);
	}
	
	/**
	 * 开始定位动画
	 */
	private void startLocateAnimation() {	
		float pivotX = btn_locating.getWidth()/2;
		float pivotY = btn_locating.getHeight()/2;
		
		if (pivotX == 0) {
			return;
		}
		
		RotateAnimation animation = new RotateAnimation(0, 355, pivotX, pivotY);
		animation.setDuration(800);
		animation.setRepeatCount(-1);
		animation.setInterpolator(new LinearInterpolator());
		btn_locating.startAnimation(animation);
		btn_locating.setVisibility(View.VISIBLE);
		
		btn_location.setBackgroundResource(R.drawable.navi_drawable_locate_car_null);
	}
	
	private void stopLocateAnimation(){
		Animation animation = btn_locating.getAnimation();
		if (animation == null) {
			return;
		}

		btn_locating.clearAnimation();
		btn_locating.setVisibility(View.INVISIBLE);
		btn_location.setBackgroundResource(R.drawable.btn_navi_drawable_locate_car_selector);
	}
	
	private void panTo(boolean isLeft) {
		Point2D pt2d = mMapControl.getMap().getCenter();
		int width = mMapControl.getWidth();
		
		Point pt = mMapControl.getMap().mapToPixel(pt2d);
		
		Point pt1 = new Point();
		pt1.setX((int) (pt.getX() + width));
		pt1.setY((int) (pt.getY()));
		
		Point2D pt2d1 = mMapControl.getMap().pixelToMap(pt1);
		
		double length = pt2d1.getX() - pt2d.getX();	
		
		if (isLeft) {
			length = -length;
		}
		
		mMapControl.getMap().pan(length, 0);
		mMapControl.getMap().refresh();
	}
}


